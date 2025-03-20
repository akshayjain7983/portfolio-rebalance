package io.github.funofprograming.pr.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicReference

object JsonMapperProvider {

    private val mapperReference:AtomicReference<JsonMapper> = AtomicReference<JsonMapper>()

    fun getJsonMapper():JsonMapper {
        if(mapperReference.get() == null){
            mapperReference.set(
                jacksonMapperBuilder()
                    .findAndAddModules()
                    .addModule(JavaTimeModule())
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                    .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
                    .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                    .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                    .serializationInclusion(JsonInclude.Include.NON_NULL)
                    .addModule(PortfolioConfigurationModule())
                    .build()
            )
        }

        return mapperReference.get()
    }

    fun getJsonMapper(initializer: KotlinModule.Builder.() -> Unit):JsonMapper {
        if(mapperReference.get() == null){
            mapperReference.set(
                jacksonMapperBuilder(initializer)
                    .addModule(PortfolioConfigurationModule())
                    .build()
            )
        }

        return mapperReference.get()
    }

    fun getJsonMapper(initializer: JsonMapper.Builder):JsonMapper {
        if(mapperReference.get() == null){
            mapperReference.set(
                initializer
                    .addModule(PortfolioConfigurationModule())
                    .build()
            )
        }

        return mapperReference.get()
    }

    fun reset() = mapperReference.set(null)

}

fun <T> buildJsonSerializer(handledType: Class<T>?, jsonSerializerOperation: (T?, JsonGenerator, SerializerProvider)->Unit): StdSerializer<T> {
    return object : StdSerializer<T>(handledType) {
        @Throws(IOException::class)
        override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
            jsonSerializerOperation.invoke(value, gen, provider)
        }
    }
}

fun <T> buildJsonDeserializer(handledType: Class<T>?, jsonDeserializerOperation: (JsonParser, DeserializationContext)->T): StdDeserializer<T> {
    return object : StdDeserializer<T>(handledType) {
        @Throws(IOException::class, JacksonException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
            return jsonDeserializerOperation.invoke(p, ctxt)
        }
    }
}

fun <T> buildJsonMapKeyDeserializer(handledType: Class<T>?, jsonMapKeyDeserializerOperation: (String?, DeserializationContext?)->T): StdKeyDeserializer {
    return object : StdKeyDeserializer(-1, handledType) {
        @Throws(IOException::class, JacksonException::class)
        override fun deserializeKey(k: String, ctxt: DeserializationContext): T {
            return jsonMapKeyDeserializerOperation.invoke(k, ctxt)
        }
    }
}

inline fun toJson(inObject: Any?, jsonMapper: JsonMapper = JsonMapperProvider.getJsonMapper()): String {
    try {
        return jsonMapper.writeValueAsString(inObject)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

inline fun <reified T> fromJson(json: String?, jsonMapper: JsonMapper = JsonMapperProvider.getJsonMapper()): T? {
    try {
        var jsonToUse = adjustJsonForParsingTemporal<T>(json)
        return jsonMapper.readValue<T>(jsonToUse ?: "")
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

inline fun <reified T> fromJson(json: InputStream?, jsonMapper: JsonMapper = JsonMapperProvider.getJsonMapper()): T? {
    try {
        return json?.let { jsonMapper.readValue<T>(json) }
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

inline fun <reified T> viaJson(source: Any?, jsonMapper: JsonMapper = JsonMapperProvider.getJsonMapper()): T {
    return jsonMapper.convertValue<T>(source)
}

inline fun <reified T> adjustJsonForParsingTemporal(json: String?): String? {

    var jsonToUse = json

    if(java.time.temporal.Temporal::class.java.isAssignableFrom(T::class.java)) { //for dates jackson wants extra "" inside the string
        jsonToUse = if (jsonToUse?.startsWith("\"") != true) "\""+jsonToUse else jsonToUse
        jsonToUse = if (jsonToUse?.endsWith("\"") != true) jsonToUse+"\"" else jsonToUse
    }
    return jsonToUse
}
