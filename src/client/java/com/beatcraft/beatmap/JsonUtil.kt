package com.beatcraft.beatmap

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

object JsonUtil {
    @JvmStatic
    val JsonDefaultParser: Json = Json {
        ignoreUnknownKeys = true
    }

    @JvmStatic
    fun <T> parseArray(jsonElement: JsonElement): List<T> {
        return JsonDefaultParser.decodeFromJsonElement<List<T>>(jsonElement)
    }

    @JvmStatic
    fun <T> parseArray(jsonElement: JsonElement, clazz: Class<T>): List<T> {
        return parseArray(jsonElement)
    }

    @JvmStatic
    fun jsonElementToFloat(jsonElement: JsonElement): Float? {
        return if (jsonElement is JsonPrimitive) {
            jsonElement.content.toFloatOrNull()
        } else {
            null
        }
    }
}
