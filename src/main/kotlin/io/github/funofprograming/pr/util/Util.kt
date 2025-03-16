package io.github.funofprograming.pr.util

import REBAL_LOOP_RULE_STATES_KEY
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.Attribute
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import java.util.*

fun isLoopContinueNextIteration(rebalanceId: UUID, loopLabel: String?):Boolean {

    val rebalanceContext = getGlobalContext(rebalanceId.toString())
    val loopState = rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()
    return loopState?.loopLabel == loopLabel && loopState?.continueNextIteration?.get() ?: false
}

fun isLoopInnermost(rebalanceId: UUID, loopLabel: String?):Boolean {
    val rebalanceContext = getGlobalContext(rebalanceId.toString())
    val loopState = rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()
    return loopState?.loopLabel == loopLabel
}

fun <T> getObject(attr: Attribute<T>?, row: DataRow<*>): T? {

    val name = attr?.name
    val type = attr?.type
    val literal = attr?.literalValue

    if(literal != null) {
        return  parseLiteral(literal, type)

    } else if(name != null) {
        val accessor = getAccessor(name, type)
        return row.getValueOrNull((accessor as ColumnReference<*>)) as T?
    }

    throw IllegalArgumentException("Invalid attribute: $attr")
}

fun <T> parseLiteral(literal: String?, type: Class<T>?): T? {

    if(literal == null || type == null)
        return null

    return when{
        type.isAssignableFrom(java.lang.String::class.java) -> fromJson<java.lang.String>(literal)
        type.isAssignableFrom(java.lang.Short::class.java) -> fromJson<java.lang.Short>(literal)
        type.isAssignableFrom(java.lang.Integer::class.java) -> fromJson<java.lang.Integer>(literal)
        type.isAssignableFrom(java.lang.Long::class.java) -> fromJson<java.lang.Long>(literal)
        type.isAssignableFrom(java.lang.Float::class.java) -> fromJson<java.lang.Float>(literal)
        type.isAssignableFrom(java.lang.Double::class.java) -> fromJson<java.lang.Double>(literal)
        type.isAssignableFrom(java.math.BigInteger::class.java) -> fromJson<java.math.BigInteger>(literal)
        type.isAssignableFrom(java.math.BigDecimal::class.java) -> fromJson<java.math.BigDecimal>(literal)
        type.isAssignableFrom(java.time.LocalDate::class.java) -> fromJson<java.time.LocalDate>(literal)
        type.isAssignableFrom(java.time.LocalDateTime::class.java) -> fromJson<java.time.LocalDateTime>(literal)
        type.isAssignableFrom(java.time.ZonedDateTime::class.java) -> fromJson<java.time.ZonedDateTime>(literal)
        type.isAssignableFrom(java.time.Instant::class.java) -> fromJson<java.time.Instant>(literal)
        else -> throw IllegalArgumentException("Unsupported attribute type: $type")
    } as T?
}

fun <T> getAccessor(colName: String?, type: Class<T>?): ColumnAccessor<T>? {

    if(colName == null || type == null)
        return null

    return when{
        type.isAssignableFrom(java.lang.String::class.java) -> column<java.lang.String>(colName)
        type.isAssignableFrom(java.lang.Short::class.java) -> column<java.lang.Short>(colName)
        type.isAssignableFrom(java.lang.Integer::class.java) -> column<java.lang.Integer>(colName)
        type.isAssignableFrom(java.lang.Long::class.java) -> column<java.lang.Long>(colName)
        type.isAssignableFrom(java.lang.Float::class.java) -> column<java.lang.Float>(colName)
        type.isAssignableFrom(java.lang.Double::class.java) -> column<java.lang.Double>(colName)
        type.isAssignableFrom(java.math.BigInteger::class.java) -> column<java.math.BigInteger>(colName)
        type.isAssignableFrom(java.math.BigDecimal::class.java) -> column<java.math.BigDecimal>(colName)
        type.isAssignableFrom(java.time.LocalDate::class.java) -> column<java.time.LocalDate>(colName)
        type.isAssignableFrom(java.time.LocalDateTime::class.java) -> column<java.time.LocalDateTime>(colName)
        type.isAssignableFrom(java.time.ZonedDateTime::class.java) -> column<java.time.ZonedDateTime>(colName)
        type.isAssignableFrom(java.time.Instant::class.java) -> column<java.time.Instant>(colName)
        else -> throw IllegalArgumentException("Unsupported attribute type: $type")
    } as ColumnAccessor<T>
}
