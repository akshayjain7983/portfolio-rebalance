package io.github.funofprograming.pr.rule

import java.lang.reflect.Type

data class Attribute<T> (
    var name:String? = null,
    var type:Class<T>? = null,
    var literalValue:String? = null
) {
    override fun toString(): String {
        return literalValue ?: name ?: "null"
    }
}