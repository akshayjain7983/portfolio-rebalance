package io.github.funofprograming.pr.rule

import io.github.funofprograming.pr.util.fromJson
import io.github.funofprograming.pr.util.getAccessor
import io.github.funofprograming.pr.util.getObject
import kotlinx.datetime.*
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import java.math.BigDecimal
import java.time.ZoneOffset
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
                val leftDate = left.toJavaLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC)
                val rightDate = (if(right is LocalDate) right.toJavaLocalDate() else (right as java.time.LocalDate)).atStartOfDay().toInstant(ZoneOffset.UTC)
                return compareValues(leftDate, rightDate)
            }
            is LocalDateTime -> {
                val leftDate = left.toJavaLocalDateTime().toInstant(ZoneOffset.UTC)
                val rightDate = (if(right is LocalDateTime) right.toJavaLocalDateTime() else (right as java.time.LocalDateTime)).toInstant(ZoneOffset.UTC)
                return compareValues(leftDate, rightDate)
            }
            is ZonedDateTime -> compareValues(left, right)
            is Instant -> {
                val leftDate = left.toJavaInstant()
                val rightDate = (if(right is Instant) right.toJavaInstant() else (right as java.time.Instant))
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