package io.github.funofprograming.pr.rule.weight.capping

import IS_WEIGHT_CAPPING_RUN
import io.github.funofprograming.pr.rule.PortfolioRule
import io.github.funofprograming.pr.util.breakLook
import io.github.funofprograming.pr.util.getGlobalRebalanceContext
import io.github.funofprograming.pr.util.getSecurityWeightCapper
import io.github.funofprograming.pr.util.isInnermostLoopIterationExhausted
import io.github.funofprograming.pr.vo.SecurityWeightCapperWithParams
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class WeightCappingRule: PortfolioRule {

    var securityWeightCappersWithParams:List<SecurityWeightCapperWithParams>? = null

    override fun execute(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {

        if(securityWeightCappersWithParams.isNullOrEmpty())
            return securities

        var securitiesResult:DataFrame<*>? = securities
        val rbContext = getGlobalRebalanceContext(rebalanceId)
        rbContext?.add(IS_WEIGHT_CAPPING_RUN, AtomicBoolean(false))
        for(capperWithParams in securityWeightCappersWithParams ?: emptyList()) {

            val capper = getSecurityWeightCapper(capperWithParams.securityWeightCapperId)
            securitiesResult = if(capper != null) capper?.capWeights(rebalanceId, securitiesResult, capperWithParams.capperParams) else securitiesResult
        }
        val isWeightCappingRun = rbContext?.fetch(IS_WEIGHT_CAPPING_RUN)

        if(isWeightCappingRun?.get() == true && isInnermostLoopIterationExhausted(rebalanceId))
            throw IllegalStateException("Unable to cap weights. Review/adjust portfolio configuration.")

        if(isWeightCappingRun?.get() == false)
            breakLook(rebalanceId)

        return securitiesResult
    }
}