package io.github.funofprograming.pr.util

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.funofprograming.pr.rule.Attribute
import io.github.funofprograming.pr.rule.AttributeType
import io.github.funofprograming.pr.rule.BooleanOperator
import io.github.funofprograming.pr.rule.ComparisonOperator


class PortfolioConfigurationModule: SimpleModule {

    constructor() {

        addSerializer(ComparisonOperator::class.java, getComparisonOperatorSerializer())
        addDeserializer(ComparisonOperator::class.java, getComparisonOperatorDeserializer())
        addSerializer(BooleanOperator::class.java, getBooleanOperatorSerializer())
        addDeserializer(BooleanOperator::class.java, getBooleanOperatorDeserializer())
        addDeserializer(Attribute::class.java, getAttributeDeserializer())
    }

    fun getComparisonOperatorSerializer(): StdSerializer<ComparisonOperator> {
        return buildJsonSerializer(ComparisonOperator::class.java) { value, gen, serializers->value?.let {gen.writeString(it.symbol)} }
    }

    fun getComparisonOperatorDeserializer(): StdDeserializer<ComparisonOperator> {
        return buildJsonDeserializer(ComparisonOperator::class.java) { p, ctxt -> p.getValueAsString()?.let{ComparisonOperator.fromSymbol(it)} ?: throw IllegalArgumentException("Unsupported ComparisonOperator") }
    }

    fun getBooleanOperatorSerializer(): StdSerializer<BooleanOperator> {
        return buildJsonSerializer(BooleanOperator::class.java) { value, gen, serializers->value?.let {gen.writeString(it.symbol)} }
    }

    fun getBooleanOperatorDeserializer(): StdDeserializer<BooleanOperator> {
        return buildJsonDeserializer(BooleanOperator::class.java) { p, ctxt -> p.getValueAsString()?.let{BooleanOperator.fromSymbol(it)} ?: throw IllegalArgumentException("Unsupported BooleanOperator") }
    }

    fun getAttributeDeserializer() : StdDeserializer<Attribute<*>> {

        return buildJsonDeserializer(Attribute::class.java) { p, ctxt ->

            if(p.currentToken != JsonToken.START_OBJECT)
                return@buildJsonDeserializer Attribute<Any>()

            var name:String? = null
            var type:AttributeType? = null
            var literalValue:Any? = null

            while(p.nextToken() != JsonToken.END_OBJECT) {

                val field = if(p.currentToken == JsonToken.FIELD_NAME) p.getValueAsString() else p.nextFieldName()

                when(field) {
                    "name" -> name = p.nextTextValue()
                    "type" -> type = p.nextTextValue()?.let { AttributeType.fromSymbol(it) }
                    "literalValue" -> literalValue = p.nextTextValue()?.let { fromJson(adjustJsonForParsingTemporal(it, type?.type), type?.type) }
                }
            }

            return@buildJsonDeserializer Attribute<Any>(name, type, literalValue)
        }
    }
}