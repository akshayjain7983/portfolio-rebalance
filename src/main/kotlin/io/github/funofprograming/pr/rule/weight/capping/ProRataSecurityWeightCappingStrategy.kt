package io.github.funofprograming.pr.rule.weight.capping

import io.github.funofprograming.pr.util.safeDivide
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal

object ProRataSecurityWeightCappingStrategy: SecurityWeightCappingStrategy {

    override fun securityWeightCappingStrategyId():String = "ProRataSecurityWeightCappingStrategy"

    override fun capWeights(existingDistribution:DataFrame<*>?, targetTotalDistribution: BigDecimal):DataFrame<*>? {

        val weightColumn by column<BigDecimal>("source_value")
        val existingTotalDistribution = existingDistribution?.sum(weightColumn) ?: BigDecimal.ZERO
        val targetDistribution:DataFrame<*>? =
            existingDistribution?.add(column<BigDecimal>("target_value")) {
                weightColumn().safeDivide(existingTotalDistribution).multiply(targetTotalDistribution)
            }?.select("security_id", "target_value")

        return targetDistribution
    }
}