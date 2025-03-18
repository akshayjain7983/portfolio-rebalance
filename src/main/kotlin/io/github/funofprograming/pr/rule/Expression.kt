package io.github.funofprograming.pr.rule

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import io.github.funofprograming.pr.rule.filter.ComparisonExpression
import org.jetbrains.kotlinx.dataframe.DataRow

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(name = "ComparisonExpression", value = ComparisonExpression::class)
)
interface Expression<T> {

    fun execute(row:DataRow<*>): T?
}