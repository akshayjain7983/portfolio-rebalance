package io.github.funofprograming.pr.rule.weight

import PORTFOLIO_SIZE_CURRENT
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.util.addOrUpdateColumnInDataFrame
import io.github.funofprograming.pr.util.safeDivide
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.values
import java.math.BigDecimal
import java.util.*

object MarketValueSecurityWeightCalculator: AbstractSimpleSecurityWeightCalculator() {

    override fun securityWeightCalculatorId(): String = "MarketValueSecurityWeightCalculator"

    override fun getSourceWeightingAttributeColName(rebalanceId: UUID): String = "market_value"

    override fun getTargetWeightAttributeColName(rebalanceId: UUID): String = "market_value_weight"

    override fun overrideTotalWeightingAttribute(rebalanceId: UUID, totalWeightingAttribute:BigDecimal):BigDecimal {

        val rbContext = getGlobalContext(rebalanceId.toString())
        val currentPortfolioSize:BigDecimal = rbContext?.fetch(PORTFOLIO_SIZE_CURRENT) ?: BigDecimal.ZERO
        return if(currentPortfolioSize < totalWeightingAttribute) totalWeightingAttribute else currentPortfolioSize
    }
}