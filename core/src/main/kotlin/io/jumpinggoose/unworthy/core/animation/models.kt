package io.jumpinggoose.unworthy.core.animation

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.*

data class AnimationCycle(
    val frames: List<Int> = emptyList(),
    val isLooping: Boolean = false,
    val isPingPong: Boolean = false,
    val isReversed: Boolean = false,
    val frameDuration: Float = 0.2f
)

data class TextureAtlasData(
    val texture: String = "",
    val regionWidth: Int = 0,
    val regionHeight: Int = 0,
)

class AnimationCycleMapSerializer : JsonSerializer<Map<String, AnimationCycle>> {

    override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): Map<String, AnimationCycle> {
        val result = mutableMapOf<String, AnimationCycle>()
        jsonData.forEach { entry ->
            result[entry.name] = json.readValue<AnimationCycle>(entry)
        }
        return result
    }

    override fun write(json: Json, value: Map<String, AnimationCycle>, type: Class<*>?) {
        throw NotImplementedError()
    }
}

data class AnimationData(
    var textureAtlas: TextureAtlasData = TextureAtlasData(),
    var cycles: Map<String, AnimationCycle> = emptyMap()
) : Json.Serializable {

    override fun read(json: Json, jsonData: JsonValue) {
        textureAtlas = json.readValue<TextureAtlasData>(jsonData, "textureAtlas")
        json.setSerializer<Map<String, AnimationCycle>>(AnimationCycleMapSerializer())
        cycles = json.readValue(jsonData, "cycles")
    }

    override fun write(json: Json) {
        throw NotImplementedError()
    }
}
