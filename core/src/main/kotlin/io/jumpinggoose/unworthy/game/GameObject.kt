package io.jumpinggoose.unworthy.game

import com.badlogic.gdx.math.Vector2
import ktx.math.plus

abstract class GameObject(
    val id: String,
    var position: Vector2 = Vector2()
) : IGameLoop {
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
}
