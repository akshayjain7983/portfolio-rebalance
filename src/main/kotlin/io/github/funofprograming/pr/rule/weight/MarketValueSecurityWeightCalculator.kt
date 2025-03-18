package io.github.funofprograming.pr.rule.weight

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

    override fun getSourceWeightingAttributeColName(): String = "market_value"

    override fun getTargetWeightAttributeColName(): String = "market_value_weight"
}