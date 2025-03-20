package io.github.funofprograming.pr.rule

import io.github.funofprograming.pr.util.getRegisteredPortfolioRule
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

class RegisteredRule: PortfolioRule {

    var registeredRuleId: String? = null

    override fun execute(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {
        val registerableRule = getRegisteredPortfolioRule(registeredRuleId ?: "")
        val securitiesResult = registerableRule?.execute(rebalanceId, securities) ?: securities
        return securitiesResult
    }
}