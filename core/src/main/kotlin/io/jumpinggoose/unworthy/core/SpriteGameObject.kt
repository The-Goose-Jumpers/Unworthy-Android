package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.utils.Sprite

open class SpriteGameObject(
    id: String,
    open val sprite: Sprite
) : GameObject(id), IGameDrawable, IGameCollideable2D {

    constructor(id: String, assetName: String) : this(id, Sprite(assetName))

    constructor(id: String, sprite: Sprite, position: Vector2 = Vector2()) : this(id, sprite) {
        this.position = position
    }

    constructor(id: String, assetName: String, position: Vector2 = Vector2())
        : this(id, Sprite(assetName), position)

    override var position: Vector2
        get() {
            return Vector2(sprite.x + sprite.originX, sprite.y + sprite.originY)
        }
        set(value) {
            sprite.setOriginBasedPosition(value.x, value.y)
        }

    var rotation: Float
        get() = sprite.rotation
        set(value) {
            sprite.rotation = value
        }

    fun setAlpha(alpha: Float) {
        sprite.setAlpha(alpha)
    }

    fun setColor(color: Color) {
        sprite.color = color
    }

    override val bounds: Shape2D
        get() = sprite.boundingRectangle

    override fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }

    override fun update(delta: Float) {}
}
