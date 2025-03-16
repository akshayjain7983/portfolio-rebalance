package io.github.funofprograming.pr.rule

import REBAL_CMD_KEY
import REBAL_INPUT_SECURITIES_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.configuration.PortfolioConfiguration
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import java.util.*

class PortfolioRuleExecutor(private val rebalanceId: UUID) {

    fun execute(): DataFrame<*>? {

        val rebalanceContext = getGlobalContext(rebalanceId.toString())
        val portfolioRebalanceCommand: PortfolioRebalanceCommand? = rebalanceContext?.fetch(REBAL_CMD_KEY)
        val portfolioConfiguration:PortfolioConfiguration? = portfolioRebalanceCommand?.portfolioConfiguration
        val portfolioRules:List<PortfolioRule>? = portfolioConfiguration?.constituentRules
        val inputSecurities:DataFrame<*>? = rebalanceContext?.fetch(REBAL_INPUT_SECURITIES_KEY)

        if(portfolioRules.isNullOrEmpty() || inputSecurities?.isEmpty() ?: false){
            return null
        }

        var outputSecurities:DataFrame<*>? = inputSecurities
        for(pr:PortfolioRule in portfolioRules) {

            if(pr.doExecute(rebalanceId)) {
                outputSecurities = pr.execute(rebalanceId, outputSecurities)
            }
        }


        return outputSecurities
    }
}