package io.github.funofprograming.pr.rule.loop

import REBAL_LOOP_RULE_STATES_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.PortfolioRule
import io.github.funofprograming.pr.util.isLoopContinueNextIteration
import io.github.funofprograming.pr.util.isLoopInnermost
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class LoopPortfolioRule: PortfolioRule {

    var loopLabel: String? = null
    var maxIterations: Int? = null
    var portfolioRules: List<PortfolioRule>? = null

    override fun execute(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {

        if(portfolioRules.isNullOrEmpty()) {
            return securities
        }

        val rebalanceContext = getGlobalContext(rebalanceId.toString())
        initiateLooping(rebalanceId)
        var securitiesLooped:DataFrame<*>? = securities

        RULE_LOOP@ for(iteration in 1..(maxIterations ?: 0)) {

            rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()?.currentIteration?.set(iteration)
            rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()?.continueNextIteration?.set(false)

            for(pr: PortfolioRule in portfolioRules ?: emptyList()) {

                securitiesLooped = pr.execute(rebalanceId, securitiesLooped)

                if(isLoopContinueNextIteration(rebalanceId, loopLabel))
                    continue@RULE_LOOP

                if(!isLoopInnermost(rebalanceId, loopLabel))
                    break@RULE_LOOP
            }
        }

        if(isLoopInnermost(rebalanceId, loopLabel))
            rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.pop()

        return securitiesLooped
    }

    private fun initiateLooping(rebalanceId: UUID) {

        val rebalanceContext = getGlobalContext(rebalanceId.toString())
        if(rebalanceContext?.exists(REBAL_LOOP_RULE_STATES_KEY)?.not() == true) {
            rebalanceContext?.add(REBAL_LOOP_RULE_STATES_KEY, LinkedBlockingDeque())
        }
        rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.add(LoopState(loopLabel, maxIterations, AtomicInteger(0), AtomicBoolean(false)))
    }
}