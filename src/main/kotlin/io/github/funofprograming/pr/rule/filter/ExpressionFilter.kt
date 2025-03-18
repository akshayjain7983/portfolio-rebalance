package io.github.funofprograming.pr.rule.filter

import io.github.funofprograming.pr.rule.Expression
import org.jetbrains.kotlinx.dataframe.DataRow
import java.util.*

class ExpressionFilter(): RelaxableFilter() {

    var expression: Expression<Boolean>? = null

    override fun filterNormal(rebalanceId: UUID, row: DataRow<*>): Boolean {
        return expression?.execute(row) ?: true
    }
}