package io.github.funofprograming.pr.vo

import io.github.funofprograming.pr.configuration.PortfolioConfiguration
import kotlinx.datetime.LocalDate
import java.util.*

data class PortfolioRebalanceCommand (
    val portfolioConfiguration: PortfolioConfiguration,
    val rebalanceDate: LocalDate,
    val rebalanceId: UUID = UUID.randomUUID()
)