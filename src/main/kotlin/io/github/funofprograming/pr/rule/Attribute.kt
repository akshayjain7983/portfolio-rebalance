package io.github.funofprograming.pr.rule

data class Attribute<T> (
    var name:String? = null,
    var type:AttributeType? = null,
    var literalValue:T? = null
) {
    override fun toString(): String {
        return literalValue?.toString() ?: name ?: "null"
    }
}