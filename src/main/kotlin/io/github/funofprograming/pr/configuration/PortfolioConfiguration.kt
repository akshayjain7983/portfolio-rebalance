package io.github.funofprograming.pr.configuration

import io.github.funofprograming.pr.rule.PortfolioRule
import java.math.BigDecimal
import java.time.LocalDate

data class PortfolioConfiguration (
    var portfolioConfigurationStartDate:LocalDate,
    var currency: Currency,
    var portfolioInvestmentAmountLimit: BigDecimal? = null,
    var portfolioInvestmentAmountLimitUpdated: Boolean = false,
    var portfolioCurrentSizeCalculator: String? = null,
    var portfolioCurrentSizeAttribute: String? = null,
    var exchangesWithSecurityTypes: Map<String, Collection<SecurityType>>? = null,
    var constituentRules: List<PortfolioRule>? = null
)
