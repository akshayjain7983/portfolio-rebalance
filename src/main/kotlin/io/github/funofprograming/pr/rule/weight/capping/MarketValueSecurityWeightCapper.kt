package io.github.funofprograming.pr.rule.weight.capping

import IS_WEIGHT_CAPPING_RUN
import PORTFOLIO_SIZE_CURRENT
import io.github.funofprograming.context.ApplicationContext
import io.github.funofprograming.pr.util.*
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import java.util.*

object MarketValueSecurityWeightCapper: SecurityWeightCapper {

    override fun securityWeightCapperId(): String = "MarketValueSecurityWeightCapper"

    override fun capWeights(rebalanceId: UUID, securities: DataFrame<*>?, cappingParams: Map<String, Any>?): DataFrame<*>? {

        var securitiesResult:DataFrame<*>? = securities?.let { DataFrame.empty(securities.schema()) } ?: DataFrame.Empty
        val capWeightsByGroup:Map<String, BigDecimal> = viaJson<Map<String, BigDecimal>>(cappingParams?.get("capWeightsByGroup")) ?: emptyMap()
        val weightCappingStrategyId:String? = viaJson<String>(cappingParams?.get("weightCappingStrategyId"))
        val marketValWeightAttribute:String = viaJson<String>(cappingParams?.get("marketValueWeightAttribute")) ?: "market_value_weight"
        val marketValAttribute:String = viaJson<String>(cappingParams?.get("marketValueAttribute")) ?: "market_value"

        val marketValWeightColumn by column<BigDecimal>(marketValWeightAttribute)
        val marketValColumn by column<BigDecimal>(marketValAttribute)
        val secIdColumn by column<String>("security_id")
        val rbUnitsColumn by column<Long>("rebalance_units")
        val minRunLockedRbUnitsColumn by column<Long>("min_run_locked_rebalance_units")

        val rbContext:ApplicationContext? = getGlobalRebalanceContext(rebalanceId)
        val portfolioSizeCurrent = rbContext?.fetch(PORTFOLIO_SIZE_CURRENT) ?: BigDecimal.ZERO

        for(groupAttribute in capWeightsByGroup.keys) {

            val groupWeightCap = capWeightsByGroup[groupAttribute]
            val groupColumn by column<Any?>(groupAttribute)
            val securitiesGrouped:GroupBy<Any?, Any?>? = securities?.groupBy { groupColumn }

            securitiesGrouped?.let {

                for(groupedSecurities in it.groups) {

                    val weightOfGroup = groupedSecurities.sum(marketValWeightColumn)
                    if(weightOfGroup > groupWeightCap) {
                        rbContext?.fetch(IS_WEIGHT_CAPPING_RUN)?.set(true)
                        val existingDistribution = groupedSecurities.select(secIdColumn, marketValColumn).rename(marketValColumn).into("source_value")
                        val groupPortfolioAmountLimit = portfolioSizeCurrent.multiply(groupWeightCap)
                        val weightCappingStrategy = getSecurityWeightCappingStrategy(weightCappingStrategyId ?: "")
                        val targetDistribution = weightCappingStrategy?.capWeights(existingDistribution, groupPortfolioAmountLimit)
                        val securitiesTargetMv = targetDistribution?.let { groupedSecurities.join(targetDistribution) { secIdColumn }.rename("target_value").into("target_market_value") }
                        val securitiesTargetRbUnits = securitiesTargetMv?.addOrUpdateColumnInDataFrame(rbUnitsColumn, {row, _ ->
                            val minRunLockedRbUnits = row.getValueOrNull(minRunLockedRbUnitsColumn) ?: 0L
                            val cappedMv = row.getValueOrNull<BigDecimal>("target_market_value")
                            val rebalancePrice = row.getValueOrNull<BigDecimal>("rebalance_price")
                            val targetRbUnits = rebalancePrice?.let { cappedMv?.safeDivide(rebalancePrice)?.toLong() } ?: 0L
                            val rbUnitsFinal = if(minRunLockedRbUnits > targetRbUnits) minRunLockedRbUnits else targetRbUnits
                            return@addOrUpdateColumnInDataFrame rbUnitsFinal
                        })?.remove("target_market_value")

                        securitiesResult = securitiesTargetRbUnits?.let { securitiesResult?.concat(securitiesTargetRbUnits) }
                    } else {
                        securitiesResult = securitiesResult?.concat(groupedSecurities)
                    }
                }
            }
        }

        return securitiesResult
    }
}