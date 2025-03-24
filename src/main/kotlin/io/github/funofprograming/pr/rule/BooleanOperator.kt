package io.github.funofprograming.pr.rule

enum class BooleanOperator(val symbol:String) {
    AND("AND"),
    OR("OR"),
    NOT("NOT");

    companion object {

        fun fromSymbol(symbol:String):BooleanOperator {
            return when (symbol) {
                "AND" -> AND
                "OR" -> OR
                "NOT" -> NOT
                else -> throw IllegalArgumentException("Unexpected value: $symbol")
            }
        }
    }
}