package com.beatcraft.beatmap

import com.github.fernthedev.beatmap.impl.v3.BombNoteDataV3
import com.github.fernthedev.beatmap.impl.v3.ColorNoteDataV3
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
    fun parseNotesV3(jsonElement: JsonElement): List<ColorNoteDataV3> {
        return JsonDefaultParser.decodeFromJsonElement<List<ColorNoteDataV3>>(jsonElement)
    }

    @JvmStatic
    fun parseBombsV3(jsonElement: JsonElement): List<BombNoteDataV3> {
        return JsonDefaultParser.decodeFromJsonElement<List<BombNoteDataV3>>(jsonElement)
    }

    @JvmStatic
    fun parseFloatArray(jsonElement: JsonElement): List<Float> {
        return JsonDefaultParser.decodeFromJsonElement<List<Float>>(jsonElement)
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
