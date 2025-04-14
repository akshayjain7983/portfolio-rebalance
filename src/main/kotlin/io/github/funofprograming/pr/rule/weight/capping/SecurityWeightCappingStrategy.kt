package io.github.funofprograming.pr.rule.weight.capping

import org.jetbrains.kotlinx.dataframe.DataFrame
import java.math.BigDecimal

interface SecurityWeightCappingStrategy {

    fun securityWeightCappingStrategyId():String

    fun capWeights(existingWeightDistribution: DataFrame<*>?, targetTotalDistribution: BigDecimal): DataFrame<*>?
}