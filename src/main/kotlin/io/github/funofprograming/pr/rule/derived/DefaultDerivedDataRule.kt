package io.github.funofprograming.pr.rule.derived

import PORTFOLIO_SIZE_CURRENT
import REBAL_CMD_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.PortfolioRule
import io.github.funofprograming.pr.util.getMarketValueCalculator
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import java.util.*

class DefaultDerivedDataRule: DerivedDataRule {

   override fun getDerivations(): List<(UUID, DataFrame<*>?) -> DataFrame<*>?> {
        return listOf(
            updatePortfolioCurrentSize
        )
    }

    override fun registerableRuleId(): String {
        return "DefaultDerivedDataRule"
    }

    private val updatePortfolioCurrentSize: (UUID, DataFrame<*>?)->DataFrame<*>? = portfolioCurrentSize@{ rebalanceId, securities ->

        val rbContext = getGlobalContext(rebalanceId.toString())
        val rbCmd = rbContext?.fetch(REBAL_CMD_KEY)
        val pc = rbCmd?.portfolioConfiguration
        val portfolioCurrentSizeCalculatorId = pc?.portfolioCurrentSizeCalculator
        val portfolioCurrentSizeAttribute = pc?.portfolioCurrentSizeAttribute
        val lastRebalanceConstituents = rbCmd?.lastRebalanceConstituents

        val lastRebalanceConstituentsCurrentData =
            lastRebalanceConstituents?.let { securities?.select("security_id", "close_price")?.join(lastRebalanceConstituents, type = JoinType.Inner) {"security_id" match "security_id"} }


        val portfolioCurrentSizeCalculator = portfolioCurrentSizeCalculatorId?.let { getMarketValueCalculator(portfolioCurrentSizeCalculatorId) }
        val lastRebalanceConstituentsCurrentSize = portfolioCurrentSizeCalculator?.setupMarketValues(rebalanceId, lastRebalanceConstituentsCurrentData)
        val totalCurrentSize = lastRebalanceConstituentsCurrentSize?.sum(column<BigDecimal>(portfolioCurrentSizeAttribute ?: "market_value"))
        var currentPortfolioSize: BigDecimal? = null
        if(totalCurrentSize != null)
            currentPortfolioSize = totalCurrentSize
        else if(pc?.portfolioConfigurationStartDate == rbCmd?.rebalanceDate?.toJavaLocalDate()
            && pc?.portfolioInvestmentAmountLimitUpdated ?: false
            && pc?.portfolioInvestmentAmountLimit != null)
            currentPortfolioSize = pc.portfolioInvestmentAmountLimit

        currentPortfolioSize?.let { rbContext?.add(PORTFOLIO_SIZE_CURRENT, currentPortfolioSize) }
        return@portfolioCurrentSize securities
    }
}