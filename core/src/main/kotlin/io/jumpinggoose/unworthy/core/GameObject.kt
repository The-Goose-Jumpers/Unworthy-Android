package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ktx.math.plus
import java.util.UUID

abstract class GameObject(
    val id: String = UUID.randomUUID().toString(),
    open var position: Vector2 = Vector2(),
    var layer : Float = 0f
) : IGameLoop, Disposable {
    var parent : GameObject? = null

    open fun reset() {}

    val globalPosition: Vector2
        get() {
            return if (parent != null) parent!!.globalPosition + position else position
        }

    val root: GameObject
        get() {
            return parent?.root ?: this
        }

    override fun dispose() {}
}
