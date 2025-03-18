package io.github.funofprograming.pr.rule.weight

import io.github.funofprograming.pr.rule.PortfolioRule
import io.github.funofprograming.pr.util.addOrUpdateColumnInDataFrame
import io.github.funofprograming.pr.util.getMarketValueCalculator
import io.github.funofprograming.pr.util.getSecurityWeightCalculator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import java.math.BigDecimal
import java.util.*

class WeightCalculationRule: PortfolioRule {

    var securityWeightCalculators: Map<String, String>? = null
    var rebalanceWeightAttributeName: String? = null

    override fun execute(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {

        var securitiesResult:DataFrame<*>? = securities
        for(entry in securityWeightCalculators ?: emptyMap()) {

            val securityWeightCalculator = getSecurityWeightCalculator(entry.key)
            val marketValueCalculator = getMarketValueCalculator(entry.value)
            securitiesResult = marketValueCalculator?.setupMarketValues(rebalanceId, securitiesResult)
            securitiesResult = securityWeightCalculator?.setupWeights(rebalanceId, securitiesResult)
        }

        val rbWeightSourceCol by column<BigDecimal>(rebalanceWeightAttributeName ?: "rebalance_weight_by_securityWeightCalculator")
        val rbWeight by column<BigDecimal>("rebalance_weight")
        securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(rbWeight) {row, _ -> row[rbWeightSourceCol]}
        return securitiesResult
    }
}