package io.github.funofprograming.pr.rule

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.column
import java.util.*

abstract class MinRunLockableFilter(): Filter {

    override fun execute(rebalanceId: UUID, row: DataRow<*>):Boolean {
        val minRunLockedColumn by column<Boolean?>("min_run_locked")
        val filtered = row.getValueOrNull(minRunLockedColumn) ?: filterNonMinRunLockable(rebalanceId, row)
        return filtered
    }

    abstract fun filterNonMinRunLockable(rebalanceId: UUID, row: DataRow<*>): Boolean
}