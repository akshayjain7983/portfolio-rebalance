package io.github.funofprograming.pr.configuration

import io.github.funofprograming.pr.configuration.Currency
import io.github.funofprograming.pr.rule.PortfolioRule
import java.math.BigDecimal

data class PortfolioConfiguration (
    var currency: Currency,
    var portfolioInvestmentAmountLimit: BigDecimal? = null,
    var portfolioInvestmentAmountLimitUpdated: Boolean = false,
    var exchangesWithSecurityTypes: Map<String, Collection<SecurityType>>? = null,
    var constituentRules: List<PortfolioRule>? = null
)
