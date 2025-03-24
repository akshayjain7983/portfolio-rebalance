package io.github.funofprograming.pr.rule

import REBAL_CMD_KEY
import io.github.funofprograming.pr.configuration.PortfolioConfiguration
import io.github.funofprograming.pr.rule.derived.DefaultDerivedDataRule
import io.github.funofprograming.pr.util.getGlobalRebalanceContext
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import java.util.*

class PortfolioRuleExecutor(private val rebalanceId: UUID) {

    fun execute(): DataFrame<*> {

        val rebalanceContext = getGlobalRebalanceContext(rebalanceId)
        val portfolioRebalanceCommand: PortfolioRebalanceCommand? = rebalanceContext?.fetch(REBAL_CMD_KEY)
        val portfolioConfiguration:PortfolioConfiguration? = portfolioRebalanceCommand?.portfolioConfiguration
        val portfolioRules:List<PortfolioRule>? = addDefaultDerivedDataRule(portfolioConfiguration?.constituentRules)
        val inputSecurities:DataFrame<*>? = portfolioRebalanceCommand?.inputSecurities

        if(portfolioRules.isNullOrEmpty() || inputSecurities?.isEmpty() ?: false){
            return DataFrame.empty()
        }

        var outputSecurities:DataFrame<*>? = inputSecurities
        for(pr:PortfolioRule in portfolioRules) {

            if(pr.doExecute(rebalanceId)) {
                outputSecurities = pr.execute(rebalanceId, outputSecurities)
            }
        }


        return outputSecurities ?: DataFrame.empty()
    }

    private fun addDefaultDerivedDataRule(portfolioRules:List<PortfolioRule>?):List<PortfolioRule>? {

        portfolioRules?.let {
            val portfolioRulesMutable = portfolioRules.toMutableList()
            portfolioRulesMutable.addFirst(DefaultDerivedDataRule())
            return portfolioRulesMutable.toList()
        }

        return portfolioRules
    }
}