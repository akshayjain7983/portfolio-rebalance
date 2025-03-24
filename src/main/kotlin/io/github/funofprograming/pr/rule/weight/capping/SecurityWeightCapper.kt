package io.github.funofprograming.pr.rule.weight.capping

import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

interface SecurityWeightCapper {

    fun securityWeightCapperId(): String

    fun capWeights(rebalanceId: UUID, securities: DataFrame<*>?, cappingParams:Map<String, Any>?=null): DataFrame<*>?
}