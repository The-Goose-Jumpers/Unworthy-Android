package io.jumpinggoose.unworthy.core.sceneloader

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.readArrayValue

data class Scene(
    var layers: List<Layer> = emptyList()
) : Json.Serializable {

    override fun read(json: Json, jsonData: JsonValue) {
        layers = json.readArrayValue<List<Layer>, Layer>(jsonData, "layers")
    }

    override fun write(json: Json) {
        throw NotImplementedError()
    }
}
