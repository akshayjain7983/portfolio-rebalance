package io.github.funofprograming.pr.vo

import java.math.BigDecimal
import java.util.*

data class PortfolioRebalanceTransaction (

    val rebalanceId: UUID,
    val transactionType: PortfolioRebalanceTransactionType,
    val securityId: String,
    val price: BigDecimal,
    val units: BigDecimal?,
    val marketValue: BigDecimal
)