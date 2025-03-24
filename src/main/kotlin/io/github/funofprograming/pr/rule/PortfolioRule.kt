package io.github.funofprograming.pr.rule

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import io.github.funofprograming.pr.rule.filter.FiltersRule
import io.github.funofprograming.pr.rule.loop.LoopPortfolioRule
import io.github.funofprograming.pr.rule.weight.WeightCalculationRule
import io.github.funofprograming.pr.rule.weight.capping.WeightCappingRule
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(name = "LoopPortfolioRule", value = LoopPortfolioRule::class),
    JsonSubTypes.Type(name = "FiltersRule", value = FiltersRule::class),
    JsonSubTypes.Type(name = "WeightCalculationRule", value = WeightCalculationRule::class),
    JsonSubTypes.Type(name = "RegisteredRule", value = RegisteredRule::class),
    JsonSubTypes.Type(name = "WeightCappingRule", value = WeightCappingRule::class),
)
interface PortfolioRule {

    fun doExecute(rebalanceId: UUID): Boolean {
        return true
    }

    fun execute(rebalanceId: UUID, securities: DataFrame<*>?):DataFrame<*>?
}