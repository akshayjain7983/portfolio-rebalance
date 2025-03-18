package io.github.funofprograming.pr.rule.weight

import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

interface SecurityWeightCalculator {

    fun securityWeightCalculatorId(): String

    fun setupWeights(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>?
}