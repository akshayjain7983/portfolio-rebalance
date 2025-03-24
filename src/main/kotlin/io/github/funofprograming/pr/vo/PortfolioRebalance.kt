package io.github.funofprograming.pr.vo

import org.jetbrains.kotlinx.dataframe.DataFrame
import java.math.BigDecimal
import java.util.*

data class PortfolioRebalance(

    val rebalanceId: UUID,
    val rebalanceMetrics: PortfolioRebalanceMetrics,
    var investmentMarketValue: BigDecimal?,
    var portfolioCash: BigDecimal?,
    var portfolioConstituents: DataFrame<*>?,
    var portfolioRebalanceTransactions: DataFrame<*>?
)