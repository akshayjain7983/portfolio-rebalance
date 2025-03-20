package io.github.funofprograming.pr.rule.weight

import io.github.funofprograming.pr.util.addOrUpdateColumnInDataFrame
import io.github.funofprograming.pr.util.safeDivide
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.sumOf
import java.math.BigDecimal
import java.util.*

abstract class AbstractSimpleSecurityWeightCalculator: SecurityWeightCalculator {

    override fun setupWeights(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {

        var securitiesResult = securities
        val weightingAttributeCol by column<Number>(getSourceWeightingAttributeColName(rebalanceId))
        val weightAttributeCol by column<BigDecimal>(getTargetWeightAttributeColName(rebalanceId))
        val totalWeightingAttribute = securitiesResult?.sumOf { BigDecimal.valueOf(weightingAttributeCol().toDouble()) }
        val totalWeightingAttributeToUse = overrideTotalWeightingAttribute(rebalanceId, totalWeightingAttribute ?: BigDecimal.ZERO)
        securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(weightAttributeCol)
                                { row, _ -> BigDecimal.valueOf(row[weightingAttributeCol].toDouble()).safeDivide(totalWeightingAttributeToUse ?: BigDecimal.ZERO)}
        return securitiesResult
    }

    abstract fun getSourceWeightingAttributeColName(rebalanceId: UUID): String

    abstract fun getTargetWeightAttributeColName(rebalanceId: UUID): String

    protected open fun overrideTotalWeightingAttribute(rebalanceId: UUID, totalWeightingAttribute:BigDecimal):BigDecimal? = totalWeightingAttribute
}