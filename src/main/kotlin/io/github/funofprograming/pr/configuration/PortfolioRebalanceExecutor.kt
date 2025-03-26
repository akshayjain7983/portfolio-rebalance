package io.github.funofprograming.pr.configuration

import PORTFOLIO_SIZE_CURRENT
import REBAL_CMD_KEY
import REBAL_OUTPUT_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.PortfolioRuleExecutor
import io.github.funofprograming.pr.util.getGlobalRebalanceContext
import io.github.funofprograming.pr.vo.PortfolioRebalance
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import io.github.funofprograming.pr.vo.PortfolioRebalanceMetrics
import kotlinx.datetime.Clock
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sum
import java.math.BigDecimal
import java.util.*

class PortfolioRebalanceExecutor(val portfolioRebalanceCommand: PortfolioRebalanceCommand) {

    fun execute(): PortfolioRebalance {

        val rebalanceId: UUID = portfolioRebalanceCommand.rebalanceId
        val rebalanceMetrics = PortfolioRebalanceMetrics(Clock.System.now(), null)
        val portfolioRebalance = PortfolioRebalance(rebalanceId, rebalanceMetrics, null, null, null, null)
        val rebalanceContext = getGlobalRebalanceContext(rebalanceId)
        rebalanceContext?.add(REBAL_CMD_KEY, portfolioRebalanceCommand)
        rebalanceContext?.add(REBAL_OUTPUT_KEY, portfolioRebalance)
        val portfolioRuleExecutor = PortfolioRuleExecutor(rebalanceId)
        val securities:DataFrame<*>? = portfolioRuleExecutor.execute()
        val portfolioRebalanceFinal: PortfolioRebalance? = rebalanceContext?.fetch(REBAL_OUTPUT_KEY)
        portfolioRebalanceFinal?.let { preparePortfolioRebalance(it, securities ?: DataFrame.empty()) }
        (portfolioRebalanceFinal ?: portfolioRebalance).rebalanceMetrics?.endTimestamp = Clock.System.now()
        return portfolioRebalanceFinal ?: portfolioRebalance
    }

    private fun preparePortfolioRebalance(portfolioRebalanceFinal: PortfolioRebalance, securities: DataFrame<*>) {

        val rbContext = getGlobalRebalanceContext(portfolioRebalanceCommand.rebalanceId)
        var pc = portfolioRebalanceCommand.portfolioConfiguration
        var secOutAdditionalColList = pc.constituentOutputAdditionalColumns
        var secOutCols = mutableListOf("security_id", "rebalance_price", "rebalance_units", "market_value", "rebalance_weight")
        secOutAdditionalColList?.let { secOutCols.addAll(it) }
        portfolioRebalanceFinal.portfolioConstituents = securities.select(columns = secOutCols.toTypedArray())
        val currentPortfolioSize:BigDecimal = rbContext?.fetch(PORTFOLIO_SIZE_CURRENT) ?: portfolioRebalanceCommand.portfolioConfiguration.portfolioInvestmentAmountLimit ?: BigDecimal.ZERO
        portfolioRebalanceFinal.investmentMarketValue = portfolioRebalanceFinal.portfolioConstituents?.sum(column<BigDecimal>("market_value")) ?: BigDecimal.ZERO
        portfolioRebalanceFinal.portfolioCash = currentPortfolioSize - (portfolioRebalanceFinal.investmentMarketValue ?: BigDecimal.ZERO)
    }
}