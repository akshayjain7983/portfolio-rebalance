package io.github.funofprograming.pr.rule

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(name = "LoopPortfolioRule", value = LoopPortfolioRule::class),
    JsonSubTypes.Type(name = "FiltersRule", value = FiltersRule::class)
)
interface PortfolioRule {

    fun doExecute(rebalanceId: UUID): Boolean {
        return true
    }

    fun execute(rebalanceId: UUID, securities: DataFrame<*>?):DataFrame<*>?
}