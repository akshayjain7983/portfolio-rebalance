package io.github.funofprograming.pr.rule

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.filter
import java.util.*

class ExpressionFilter(): RelaxableFilter() {

    var expression: Expression<Boolean>? = null

    override fun filterNormal(rebalanceId: UUID, row: DataRow<*>): Boolean {
        return expression?.execute(row) ?: true
    }
}