package io.github.funofprograming.pr.rule.filter

import REBAL_RELAXATION_COND_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.RelaxationCondition
import org.jetbrains.kotlinx.dataframe.DataRow
import java.util.*

abstract class RelaxableFilter(): MinRunLockableFilter() {

    var relaxedFilters: Map<RelaxationCondition, Filter>? = null

    override fun filterNonMinRunLockable(rebalanceId: UUID, row: DataRow<*>): Boolean {
        val relaxedFilter = getRelaxedFilter(rebalanceId)
        val filtered = relaxedFilter?.execute(rebalanceId, row) ?: filterNormal(rebalanceId, row)
        return filtered
    }

    fun getRelaxedFilter(rebalanceId: UUID): Filter? {

        val rebalanceContext = getGlobalContext(rebalanceId.toString())
        val relaxationCondition = rebalanceContext?.fetch(REBAL_RELAXATION_COND_KEY)
        return relaxationCondition?.let { relaxedFilters?.get(relaxationCondition) }
    }

    abstract fun filterNormal(rebalanceId: UUID, row: DataRow<*>): Boolean
}