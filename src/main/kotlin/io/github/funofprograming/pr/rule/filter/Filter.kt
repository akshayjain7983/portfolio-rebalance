package io.github.funofprograming.pr.rule.filter

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import org.jetbrains.kotlinx.dataframe.DataRow
import java.util.*
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(name = "ExpressionFilter", value = ExpressionFilter::class)
)
interface Filter {

    fun execute(rebalanceId: UUID, row: DataRow<*>):Boolean
}