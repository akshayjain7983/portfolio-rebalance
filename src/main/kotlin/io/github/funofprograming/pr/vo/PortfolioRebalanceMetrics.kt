package io.github.funofprograming.pr.vo

import kotlinx.datetime.Instant

data class PortfolioRebalanceMetrics (
    var startTimestamp: Instant?,
    var endTimestamp: Instant?
)