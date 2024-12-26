package io.jumpinggoose.unworthy.core.sceneloader

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.readArrayValue

class Layer(
    var name: String = "",
    var objects: List<Object> = emptyList()
) : Json.Serializable {

    override fun read(json: Json, jsonData: JsonValue) {
        name = jsonData.getString("name")
        objects = json.readArrayValue<List<Object>, Object>(jsonData, "objects")
    }

    override fun write(json: Json) {
        throw NotImplementedError()
    }
}
