package io.github.funofprograming.pr.configuration

import REBAL_CMD_KEY
import REBAL_INPUT_SECURITIES_KEY
import REBAL_OUTPUT_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.PortfolioRuleExecutor
import io.github.funofprograming.pr.vo.PortfolioRebalance
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import io.github.funofprograming.pr.vo.PortfolioRebalanceMetrics
import kotlinx.datetime.Clock
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

class PortfolioRebalanceExecutor(val portfolioRebalanceCommand: PortfolioRebalanceCommand) {

    fun execute(inputSecurities: DataFrame<*>): PortfolioRebalance {

        val rebalanceId: UUID = portfolioRebalanceCommand.rebalanceId
        val rebalanceMetrics = PortfolioRebalanceMetrics(Clock.System.now(), null)
        val portfolioRebalance = PortfolioRebalance(rebalanceId, rebalanceMetrics, null, null, null, null)
        val rebalanceContext = getGlobalContext(rebalanceId.toString())
        rebalanceContext?.add(REBAL_CMD_KEY, portfolioRebalanceCommand)
        rebalanceContext?.add(REBAL_INPUT_SECURITIES_KEY, inputSecurities)
        rebalanceContext?.add(REBAL_OUTPUT_KEY, portfolioRebalance)
        val portfolioRuleExecutor = PortfolioRuleExecutor(rebalanceId)
        val constituents:DataFrame<*>? = portfolioRuleExecutor.execute()
        val portfolioRebalanceFinal: PortfolioRebalance? = rebalanceContext?.fetch(REBAL_OUTPUT_KEY)

        TODO("convert constituents into DataFrame<PortfolioConstituent>? and setup portfolioRebalanceFinal")

        (portfolioRebalanceFinal ?: portfolioRebalance).rebalanceMetrics?.endTimestamp = Clock.System.now()
        return portfolioRebalanceFinal ?: portfolioRebalance
    }
}