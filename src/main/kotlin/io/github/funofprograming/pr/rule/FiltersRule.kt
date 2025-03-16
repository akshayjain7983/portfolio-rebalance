package io.github.funofprograming.pr.rule

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.filter
import java.util.*

class FiltersRule: PortfolioRule {

    var rootFilter: CompoundFilter? = null

    override fun execute(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {
        return securities?.filter { row->rootFilter?.execute(rebalanceId, row) ?: true }
    }
}