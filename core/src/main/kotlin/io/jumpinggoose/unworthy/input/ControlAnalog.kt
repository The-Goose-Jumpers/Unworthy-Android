package io.jumpinggoose.unworthy.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.core.Canvas
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.utils.fillCircle
import ktx.math.*
import kotlin.math.min

class ControlAnalog(
    position: Vector2 = Vector2(),
    private val radius: Float,
    private val innerCircleRadius: Float = radius * 0.25f,
    private val zoneColor: Color = Color(1f, 1f, 1f, 0.15f),
    private val innerCircleColor: Color = Color(1f, 1f, 1f, 0.25f),
    private val zoneIdleColor: Color = Color(zoneColor.r, zoneColor.g, zoneColor.b, 0f),
    private val fadeOutWhenIdle: Boolean = true
) : GameObject(position = position), IGameDrawable {
    private val input = Gdx.input
    private val zone = Circle(position, radius)
    private val innerCirclePosition = position.cpy()
    private val currentZoneColor = zoneColor.cpy()

    override var position: Vector2 = position
        set(value) {
            field = value
            zone.setPosition(value)
            innerCirclePosition.set(value)
        }

    private var isTouched: Boolean = false

    var currentPointerId: Int = -1
        private set

    var timeSinceLastTouch = 0f
        private set

    val horizontalAxis: Float
        get() = if (!isTouched) 0f else (innerCirclePosition.x - position.x) / radius

    val verticalAxis: Float
        get() = if (!isTouched) 0f else (innerCirclePosition.y - position.y) / radius

    override fun update(delta: Float) {
        if (fadeOutWhenIdle) {
            timeSinceLastTouch += delta

            if (timeSinceLastTouch >= 5f) {
                currentZoneColor.lerp(zoneIdleColor, 2 * delta)
            }
        }

        if ((currentPointerId != -1 && !input.isTouched(currentPointerId)) || !input.isTouched) {
            isTouched = false
            currentPointerId = -1
            innerCirclePosition.set(position)
            return
        }

        var touchPosition = Vector2()
        if (!isTouched) {
            for (i in 0 until 3) {
                if (!input.isTouched(i)) continue
                touchPosition = world.viewport.unproject(
                    Vector2(input.getX(i).toFloat(), input.getY(i).toFloat())
                )
                isTouched = zone.contains(touchPosition)
                if (isTouched) {
                    currentPointerId = i
                    break
                }
            }
            if (!isTouched) return
        } else {
            touchPosition = world.viewport.unproject(
                Vector2(input.getX(currentPointerId).toFloat(), input.getY(currentPointerId).toFloat())
            )
        }

        timeSinceLastTouch = 0f
        currentZoneColor.set(zoneColor)

        val direction = (touchPosition - position).nor()
        val distance = min(touchPosition.dst(position), radius)
        val clampedPosition = position + (direction * distance)
        innerCirclePosition.set(clampedPosition)
    }

    override fun draw(batch: SpriteBatch) {
        if (currentZoneColor.a > 0f) {
            batch.fillCircle(zone, currentZoneColor)
        }
        if (!isTouched) return
        batch.fillCircle(innerCirclePosition, innerCircleRadius, innerCircleColor)
    }

    private val world: Canvas
        get() = root as Canvas
}
