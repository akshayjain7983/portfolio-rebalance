package io.github.funofprograming.pr.rule.filter

import io.github.funofprograming.pr.rule.Attribute
import io.github.funofprograming.pr.rule.ComparisonOperator
import io.github.funofprograming.pr.rule.Expression
import io.github.funofprograming.pr.util.getObject
import kotlinx.datetime.*
import org.jetbrains.kotlinx.dataframe.DataRow
import java.math.BigDecimal
import java.time.ZonedDateTime

class ComparisonExpression(
    var leftSide: Attribute<*>? = null,
    var operator: ComparisonOperator? = null,
    var rightSide: Attribute<*>? = null
): Expression<Boolean> {

    override fun execute(row: DataRow<*>): Boolean? {

        val left = leftSide?.let { getObject(leftSide, row) }
        val right = rightSide?.let { getObject(rightSide, row) }
        if(left == null || right == null)
            return false

        return compare(left, right)
    }

    private fun <T> compare(left:T, right:T): Boolean {

        return when(left) {
            is String -> compareValues(left, right)
            is Number -> compareValues(BigDecimal(left.toString()), BigDecimal(right.toString()))
            is LocalDate -> {
                val leftDate = left.atStartOfDayIn(TimeZone.UTC)
                val rightDate = (if(right is java.time.LocalDate) right.toKotlinLocalDate() else (right as LocalDate)).atStartOfDayIn(TimeZone.UTC)
                return compareValues(leftDate, rightDate)
            }
            is java.time.LocalDate -> {
                val leftDate = left.toKotlinLocalDate().atStartOfDayIn(TimeZone.UTC)
                val rightDate = (if(right is java.time.LocalDate) right.toKotlinLocalDate() else (right as LocalDate)).atStartOfDayIn(TimeZone.UTC)
                return compareValues(leftDate, rightDate)
            }
            is LocalDateTime -> {
                val leftDate = left.toInstant(TimeZone.UTC)
                val rightDate = (if(right is java.time.LocalDateTime) right.toKotlinLocalDateTime() else (right as LocalDateTime)).toInstant(TimeZone.UTC)
                return compareValues(leftDate, rightDate)
            }
            is java.time.LocalDateTime -> {
                val leftDate = left.toKotlinLocalDateTime().toInstant(TimeZone.UTC)
                val rightDate = (if(right is java.time.LocalDateTime) right.toKotlinLocalDateTime() else (right as LocalDateTime)).toInstant(TimeZone.UTC)
                return compareValues(leftDate, rightDate)
            }
            is ZonedDateTime -> compareValues(left, right)
            is Instant -> {
                val leftDate = left
                val rightDate = (if(right is java.time.Instant) right.toKotlinInstant() else (right as Instant))
                compareValues(leftDate, rightDate)
            }
            is java.time.Instant -> {
                val leftDate = left.toKotlinInstant()
                val rightDate = (if(right is java.time.Instant) right.toKotlinInstant() else (right as Instant))
                compareValues(leftDate, rightDate)
            }
            else -> throw IllegalArgumentException("Unsupported type for comparison: ${left!!::class}")
        }
    }

    private fun <T> compareValues(left:T?, right:T?): Boolean {

        val leftComparable = left as Comparable<T>
        val rightComparable = right as Comparable<T>

        return when(operator ?: ComparisonOperator.EQUAL){
            ComparisonOperator.GREATER_THAN -> leftComparable > right
            ComparisonOperator.GREATER_THAN_EQUAL -> leftComparable >= right
            ComparisonOperator.LESS_THAN -> leftComparable < right
            ComparisonOperator.LESS_THAN_EQUAL -> leftComparable <= right
            ComparisonOperator.NOT_EQUAL -> leftComparable != right
            ComparisonOperator.EQUAL -> leftComparable == right
        }
    }
}