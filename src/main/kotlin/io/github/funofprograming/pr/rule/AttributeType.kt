package io.github.funofprograming.pr.rule

import io.github.funofprograming.pr.rule.ComparisonOperator.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

enum class AttributeType(val symbol:String, val type:Class<*>) {

    STRING("String", String::class.java),
    NUMBER("Number", BigDecimal::class.java),
    LOCAL_DATE("Date", LocalDate::class.java),
    LOCAL_DATE_TIME("DateTime", LocalDateTime::class.java),
    ZONED_DATE_TIME("DateTimeZone", ZonedDateTime::class.java),
    INSTANT("UTC", Instant::class.java);

    companion object {

        fun fromSymbol(symbol:String):AttributeType {
            return when (symbol) {
                "String" -> STRING
                "Number" -> NUMBER
                "Date" -> LOCAL_DATE
                "DateTime" -> LOCAL_DATE_TIME
                "DateTimeZone" -> ZONED_DATE_TIME
                "Instant" -> INSTANT
                else -> throw IllegalArgumentException("Unexpected value: $symbol")
            }
        }
    }
}