package io.github.funofprograming.pr.rule.filter

import io.github.funofprograming.pr.rule.BooleanOperator
import org.jetbrains.kotlinx.dataframe.DataRow
import java.util.*

class CompoundFilter(): RelaxableFilter() {

    var operator: BooleanOperator? = null
    var filters: List<Filter>? = null

    override fun filterNormal(rebalanceId: UUID, row: DataRow<*>): Boolean {
        return when(operator) {
            BooleanOperator.AND -> filters?.all { it.execute(rebalanceId, row) } ?: true
            BooleanOperator.OR -> filters?.any { it.execute(rebalanceId, row) } ?: true
            BooleanOperator.NOT -> filters?.none { it.execute(rebalanceId, row) } ?: true
            null -> throw NullPointerException("Missing operator")
        }
    }
}