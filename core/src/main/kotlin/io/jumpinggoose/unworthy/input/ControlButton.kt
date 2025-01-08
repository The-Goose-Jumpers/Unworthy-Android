package io.jumpinggoose.unworthy.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.core.Canvas
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.utils.fillCircle

class ControlButton(
    position: Vector2 = Vector2(),
    private val radius: Float,
    texture: Texture? = null,
    private val buttonColor: Color = Color(1f, 1f, 1f, 0.5f),
    private val buttonTouchedColor: Color = Color(1f, 1f, 1f, 0.75f),
    private val textureColor: Color = Color.WHITE,
    private val textureTouchedColor: Color = Color.WHITE,
    private val padding: Float = 0f
) : GameObject(position = position), IGameDrawable {
    private val input = Gdx.input
    private val buttonCircle = Circle(position, radius)
    private val sprite: Sprite?

    init {
        if (texture != null) {
            sprite = Sprite(texture)
            if (textureColor != Color.WHITE) {
                sprite.color = textureColor
            }
            sprite.setBounds(
                position.x - radius + padding,
                position.y - radius + padding,
                radius * 2 - padding * 2,
                radius * 2 - padding * 2
            )
        } else {
            sprite = null
        }
    }

    override var position: Vector2 = position
        set(value) {
            field = value
            buttonCircle.setPosition(value)
            sprite?.setPosition(value.x - radius + padding, value.y - radius + padding)
        }

    private var previousState: Boolean = false
    private var currentState: Boolean = false

    /**
     * Returns true if the button was touched in the current frame but not in the previous frame.
     * Typically used if you want to check if the button was just pressed.
     */
    val wasTouched: Boolean
        get() {
            return currentState && !previousState
        }

    /**
     * Returns true if the button is currently being touched.
     * Typically used if you want to check if the button is being held down.
     */
    val isTouched: Boolean
        get() {
            return currentState && previousState
        }

    override fun update(delta: Float) {
        previousState = currentState
        currentState = false

        if (!input.isTouched) return

        for (i in 0 until 3) {
            if (!input.isTouched(i)) continue
            val touchPosition = world.viewport.unproject(
                Vector2(input.getX(i).toFloat(), input.getY(i).toFloat())
            )
            if (buttonCircle.contains(touchPosition)) {
                currentState = true
                break
            }
        }
    }

    override fun draw(batch: SpriteBatch) {
        val buttonColor = if (currentState) buttonTouchedColor else buttonColor
        batch.fillCircle(buttonCircle, buttonColor)
        if (sprite == null) return
        val textureColor = if (currentState) textureTouchedColor else textureColor
        if (sprite.color != textureColor) {
            sprite.color = textureColor
        }
        sprite.draw(batch)
    }

    private val world: Canvas
        get() = root as Canvas
}
