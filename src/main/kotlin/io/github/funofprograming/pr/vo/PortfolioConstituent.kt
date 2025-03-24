package io.github.funofprograming.pr.vo

import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.util.*

data class PortfolioConstituent (

    val rebalanceId: UUID,
    val securityId: String,
    val price: BigDecimal,
    val units: BigDecimal?,
    val marketValue: BigDecimal,
    val weight: BigDecimal,
    val inPortfolioSince: LocalDate
)