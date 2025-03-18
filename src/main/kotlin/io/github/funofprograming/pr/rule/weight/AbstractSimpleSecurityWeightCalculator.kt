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
        val weightingAttributeCol by column<Number>(getSourceWeightingAttributeColName())
        val weightAttributeCol by column<BigDecimal>(getTargetWeightAttributeColName())
        val totalWeightingAttribute = securitiesResult?.sumOf { BigDecimal.valueOf(weightingAttributeCol().toDouble()) }
        securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(weightAttributeCol)
                                { row, _ -> BigDecimal.valueOf(row[weightingAttributeCol].toDouble()).safeDivide(totalWeightingAttribute ?: BigDecimal.ZERO)}
        return securitiesResult
    }

    abstract fun getSourceWeightingAttributeColName(): String

    abstract fun getTargetWeightAttributeColName(): String
}