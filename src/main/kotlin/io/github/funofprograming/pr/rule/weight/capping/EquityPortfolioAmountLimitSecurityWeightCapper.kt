package io.github.funofprograming.pr.rule.weight.capping

import IS_WEIGHT_CAPPING_RUN
import PORTFOLIO_SIZE_CURRENT
import REBAL_CMD_KEY
import io.github.funofprograming.pr.util.addOrUpdateColumnInDataFrame
import io.github.funofprograming.pr.util.getGlobalRebalanceContext
import io.github.funofprograming.pr.util.safeDivide
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import java.util.*

object EquityPortfolioAmountLimitSecurityWeightCapper:SecurityWeightCapper {

    override fun securityWeightCapperId(): String = "EquityPortfolioAmountLimitSecurityWeightCapper"

    override fun capWeights(rebalanceId: UUID, securities: DataFrame<*>?, cappingParams: Map<String, Any>?): DataFrame<*>? {

        var securitiesResult = securities
        val rbContext = getGlobalRebalanceContext(rebalanceId)
        val portfolioSizeCurrent = rbContext?.fetch(PORTFOLIO_SIZE_CURRENT) ?: BigDecimal.ZERO
        val prc = rbContext?.fetch(REBAL_CMD_KEY)
        val pc = prc?.portfolioConfiguration
        val pcSizeAttr = pc?.portfolioCurrentSizeAttribute ?: ""
        val rbUnits by column<Long>("rebalance_units")
        val sizeAttr by column<BigDecimal>(pcSizeAttr)
        val totalSizeNow = securities?.sum(sizeAttr) ?: BigDecimal.ZERO
        val diff = totalSizeNow - portfolioSizeCurrent
        if(diff > BigDecimal.ZERO) {

            rbContext?.fetch(IS_WEIGHT_CAPPING_RUN)?.set(true)
            val cappingToApply = BigDecimal.ONE - diff.safeDivide(totalSizeNow)
            securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(rbUnits) { row, _ ->
                val minRunLockedRebalanceUnits = row.getValueOrNull<Long>("min_run_locked_rebalance_units") ?: 0
                val rebalanceUnitsCurrent = row.getValue<Long>("rebalance_units")
                val rbUnitsRequired = (cappingToApply * BigDecimal.valueOf(rebalanceUnitsCurrent)).toLong()
                val rbUnits = if(minRunLockedRebalanceUnits > rbUnitsRequired) minRunLockedRebalanceUnits else rbUnitsRequired
                return@addOrUpdateColumnInDataFrame rbUnits
            }



            securitiesResult = securitiesResult?.filter { sizeAttr() > BigDecimal.ZERO && rbUnits() > 0 }
        }

        return securitiesResult
    }
}