package io.github.funofprograming.pr.util

import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.funofprograming.pr.rule.BooleanOperator
import io.github.funofprograming.pr.rule.ComparisonOperator


class PortfolioConfigurationModule: SimpleModule {

    constructor() {

        addSerializer(ComparisonOperator::class.java, getComparisonOperatorSerializer())
        addDeserializer(ComparisonOperator::class.java, getComparisonOperatorDeserializer())
        addSerializer(BooleanOperator::class.java, getBooleanOperatorSerializer())
        addDeserializer(BooleanOperator::class.java, getBooleanOperatorDeserializer())
    }

    fun getComparisonOperatorSerializer(): StdSerializer<ComparisonOperator> {
        return buildJsonSerializer(ComparisonOperator::class.java) { value, gen, serializers->value?.let {gen.writeString(it.symbol)} }
    };

    fun getComparisonOperatorDeserializer(): StdDeserializer<ComparisonOperator> {
        return buildJsonDeserializer(ComparisonOperator::class.java) { p, ctxt -> p.getValueAsString()?.let{ComparisonOperator.fromSymbol(it)} ?: throw IllegalArgumentException("Unsupported ComparisonOperator") }
    }

    fun getBooleanOperatorSerializer(): StdSerializer<BooleanOperator> {
        return buildJsonSerializer(BooleanOperator::class.java) { value, gen, serializers->value?.let {gen.writeString(it.symbol)} }
    };

    fun getBooleanOperatorDeserializer(): StdDeserializer<BooleanOperator> {
        return buildJsonDeserializer(BooleanOperator::class.java) { p, ctxt -> p.getValueAsString()?.let{BooleanOperator.fromSymbol(it)} ?: throw IllegalArgumentException("Unsupported BooleanOperator") }
    }
}