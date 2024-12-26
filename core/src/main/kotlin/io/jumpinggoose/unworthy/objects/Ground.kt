package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameCollideable2D
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.utils.fillRectangle

class Ground(
    position: Vector2,
    val width: Float,
    val height: Float
) : GameObject("Ground", position), IGameDrawable, IGameCollideable2D {
    private val rect: Rectangle = Rectangle(position.x, position.y, width, height)
    private val color: Color = Color.WHITE

    override val bounds: Rectangle = rect

    override fun draw(batch: SpriteBatch) {
        batch.fillRectangle(rect, color)
    }

    override fun update(delta: Float) { }
}
