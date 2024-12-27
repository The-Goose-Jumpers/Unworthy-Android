package io.jumpinggoose.unworthy.core.sceneloader

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Json
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.objects.Terrain
import ktx.assets.toInternalFile
import ktx.json.fromJson

class SceneLoader(sceneName: String) {
    val json = Json()

    private val layerDepths = mapOf(
        "Background0" to Constants.LAYER_BACKGROUND_0,
        "Background1" to Constants.LAYER_BACKGROUND_1,
        "Ground" to Constants.LAYER_GROUND,
        "Platforms" to Constants.LAYER_PLATFORMS,
        "Background2" to Constants.LAYER_BACKGROUND_2,
        "Objects" to Constants.LAYER_OBJECTS,
        "Entities" to Constants.LAYER_ENTITIES,
        "Foreground" to Constants.LAYER_FOREGROUND
    )

    private val scene: Scene = json.fromJson<Scene>("Maps/$sceneName.tmj".toInternalFile())

    fun createObjects(): List<GameObject> {
        val terrain = Terrain("Terrain")
        val objects = mutableListOf<GameObject>()
        for (layer in scene.layers) {
            if (layer.name == "Bounds" || layer.name == "KillTriggers" || layer.name == "Entities") continue
            for (factory in layer.objects) {
                // TODO: Invisible objects should still exist, just not be drawn. But for now, we skip them.
                if (!factory.visible) continue
                val obj = factory.create() ?: continue
                obj.layer = layerDepths[layer.name] ?: Constants.LAYER_OBJECTS
                if (factory.type == "Ground" || factory.type == "Platform") {
                    terrain.add(obj)
                } else {
                    objects.add(obj)
                }
            }
        }
        objects.add(terrain)
        return objects
    }

    fun getPlayer(): Object? {
        for (layer in scene.layers) {
            if (layer.name == "Entities") {
                for (factory in layer.objects) {
                    if (factory.type == "Player") {
                        return factory
                    }
                }
            }
        }
        return null
    }

    fun getEntities(): List<Object> {
        val entities = mutableListOf<Object>()
        for (layer in scene.layers) {
            if (layer.name == "Entities") {
                for (factory in layer.objects) {
                    if (factory.type != "Player") {
                        entities.add(factory)
                    }
                }
            }
        }
        return entities
    }

    fun getLevelBoundaries(): List<Rectangle> {
        val boundaries = mutableListOf<Rectangle>()
        for (layer in scene.layers) {
            for (factory in layer.objects) {
                if (factory.type == "Boundary") {
                    boundaries.add(Rectangle(factory.x.toFloat(), factory.y.toFloat(), factory.width.toFloat(), factory.height.toFloat()))
                }
            }
        }
        return boundaries
    }

    fun getCameraBounds(): List<Rectangle> {
        val cameraBounds = mutableListOf<Rectangle>()
        for (layer in scene.layers) {
            for (factory in layer.objects) {
                if (factory.type == "CameraBounds") {
                    cameraBounds.add(Rectangle(factory.x.toFloat(), factory.y.toFloat(), factory.width.toFloat(), factory.height.toFloat()))
                }
            }
        }
        return cameraBounds
    }

    fun getKillTriggers(): List<Rectangle> {
        val killTriggers = mutableListOf<Rectangle>()
        for (layer in scene.layers) {
            for (factory in layer.objects) {
                if (factory.type == "KillTrigger") {
                    killTriggers.add(Rectangle(factory.x.toFloat(), factory.y.toFloat(), factory.width.toFloat(), factory.height.toFloat()))
                }
            }
        }
        return killTriggers
    }
}
