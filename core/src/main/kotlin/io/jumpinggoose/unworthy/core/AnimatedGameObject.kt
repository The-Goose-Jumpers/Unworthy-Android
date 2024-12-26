package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.core.animation.Spritesheet

open class AnimatedGameObject(
    id: String,
    sprite: AnimatedSprite,
    position: Vector2 = Vector2()
) : SpriteGameObject(id, sprite, position) {

    override val sprite: AnimatedSprite
        get() = super.sprite as AnimatedSprite

    constructor(id: String, spritesheet: Spritesheet, position: Vector2 = Vector2())
        : this(id, AnimatedSprite(spritesheet), position)

    constructor(id: String, assetName: String, position: Vector2 = Vector2())
        : this(id, Spritesheet(assetName), position)

    fun playAnimation(name: String, onCompleted: (() -> Unit)? = null) {
        sprite.play(name, onCompleted)
    }

    override fun update(delta: Float) {
        sprite.update(delta)
    }
}
