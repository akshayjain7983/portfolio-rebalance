package io.github.funofprograming.pr.rule

enum class ComparisonOperator(val symbol:String) {

    GREATER_THAN(">"),
    GREATER_THAN_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUAL("<="),
    NOT_EQUAL("!="),
    EQUAL("==");

    companion object {

        fun fromSymbol(symbol:String):ComparisonOperator {
            return when (symbol) {
                ">" -> GREATER_THAN
                ">=" -> GREATER_THAN_EQUAL
                "<" -> LESS_THAN
                "<=" -> LESS_THAN_EQUAL
                "!=" -> NOT_EQUAL
                "==" -> EQUAL
                else -> throw IllegalArgumentException("Unexpected value: $symbol")
            }
        }
    }
}