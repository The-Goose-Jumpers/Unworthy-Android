package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable
import ktx.math.plusAssign
import ktx.math.times
import kotlin.math.ceil

class ScrollingBackground(
    id: String,
    val texture: Texture,
    position: Vector2,
    private val width: Int,
    private val height: Int,
    private val speed: Vector2
) : GameObject(id, position), IGameDrawable {

    override fun update(delta: Float) {
        position += speed * Constants.PIXELS_PER_UNIT * delta
        position.x %= texture.width
        position.y %= texture.height
    }

    override fun draw(batch: SpriteBatch) {
        val startX = globalPosition.x % texture.width - texture.width
        val startY = globalPosition.y % texture.height - texture.height

        val tilesX = ceil(width.toDouble() / texture.width).toInt() + 1
        val tilesY = ceil(height.toDouble() / texture.height).toInt() + 1

        for (i in 0 until tilesX) {
            for (j in 0 until tilesY) {
                batch.draw(texture, startX + i * texture.width, startY + j * texture.height)
            }
        }
    }

    override fun dispose() {
        texture.dispose()
    }
}
