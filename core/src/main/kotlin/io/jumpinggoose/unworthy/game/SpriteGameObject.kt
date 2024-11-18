package io.jumpinggoose.unworthy.game

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

abstract class SpriteGameObject(
    id: String,
    val sprite: Sprite,
    position: Vector2 = Vector2()
) : GameObject(id, position), IGameDrawable, IGameCollideable2D {

    override val bounds: Rectangle
        get() = sprite.boundingRectangle

    override fun draw(batch: SpriteBatch) {
        batch.draw(sprite, globalPosition.x, globalPosition.y)
    }
}
