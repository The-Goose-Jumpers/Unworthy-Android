package io.jumpinggoose.unworthy.core.sceneloader

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.SpriteGameObject
import io.jumpinggoose.unworthy.objects.Ground
import io.jumpinggoose.unworthy.objects.Stars
import io.jumpinggoose.unworthy.objects.entities.Flye
import ktx.json.readArrayValue

data class Object(
    var type: String = "",
    var width: Int = 0,
    var height: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
    var rotation: Float = 0f,
    var visible: Boolean = false,
    var properties: List<CustomProperty> = emptyList()
) : Json.Serializable {

    data class CustomProperty(
        var name: String,
        var type: String,
        var value: String
    )

    private val typeToAsset = mapOf(
        "RockPlatform" to "Objects/small_platform.png",
        "LargeRockPlatform" to "Objects/large_platform.png",
        "Door" to "Objects/door.png",
        "BossHouse" to "Objects/boss_house.png",
        "CloudsUp" to "Background/clouds_up.png",
        "CloudsDown" to "Background/clouds_down.png",
        "CloudsUpEnd" to "Background/clouds_up_end.png",
        "CloudsDownEnd" to "Background/clouds_down_end.png",
        "TreesFront" to "Background/trees_front.png",
        "TreesBack" to "Background/trees_back.png",
        "CityLimits" to "Background/city_limits.png",
        "CityForest" to "Background/city_forest.png",
        "City1" to "Background/city1.png",
        "City2" to "Background/city2.png",
        "City3" to "Background/city3.png",
        "Figures" to "Background/figures.png"
    )

    private val typeToOrigin = mapOf(
        "Player" to Vector2(131.84f, 152f),
        "Flye" to Vector2(299.5f, 196f)
    )

    private val rectangleTypes = setOf(
        "Ground",
        "Platform",
        "Boundary",
        "CameraBounds",
        "Stars",
        "KillTrigger"
    )

    val position: Vector2
        get() {
            typeToOrigin[type]?.let {
                return Vector2(x + it.x, y + it.y)
            }
            if (rectangleTypes.contains(type)) {
                return Vector2(x.toFloat(), y.toFloat())
            }
            return Vector2(x + width / 2f, y + height / 2f)
        }

    val isFlippedHorizontally: Boolean
        get() = properties.any { it.name == "FlipX" && it.value == "true" }

    fun create(): GameObject? {
        return when (type) {
            "Flye" -> Flye(position)
            "Stars" -> Stars(position, width, height)
            "Ground" -> Ground(position, width.toFloat(), height.toFloat())
            "Platform" -> Ground(position, width.toFloat(), height.toFloat())
            else -> {
                typeToAsset[type]?.let {
                    val obj = SpriteGameObject(type, it, position)
                    if (isFlippedHorizontally) obj.sprite.setFlip(true, false)
                    obj
                }
            }
        }
    }

    override fun read(json: Json, jsonData: JsonValue) {
        type = jsonData.getString("type")
        width = jsonData.getInt("width")
        height = jsonData.getInt("height")
        x = jsonData.getInt("x")

        y = -jsonData.getInt("y")
        if (rectangleTypes.contains(type)) {
            y -= height
        }

        rotation = jsonData.getFloat("rotation")
        visible = jsonData.getBoolean("visible")
        if (jsonData.has("properties")) {
            properties = json.readArrayValue<List<CustomProperty>, CustomProperty>(jsonData, "properties")
        }
    }

    override fun write(json: Json) {
        throw NotImplementedError()
    }
}
