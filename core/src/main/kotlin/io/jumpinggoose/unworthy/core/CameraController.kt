package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.utils.closestPointTo
import io.jumpinggoose.unworthy.utils.getVertices
import ktx.graphics.lerpTo
import ktx.graphics.moveTo
import ktx.math.plusAssign
import ktx.math.times
import kotlin.div
import kotlin.text.compareTo
import kotlin.times

class CameraController(val camera: OrthographicCamera) : IGameLoop {
    private val bounds = mutableListOf<Rectangle>()
    private var followTarget: GameObject? = null
    private var cameraOffset = Vector2()

    fun setFollowTarget(followTarget: GameObject) {
        this.followTarget = followTarget

        val boundsFollowTargetIsIn = bounds.firstOrNull { it.contains(followTarget.position) } ?: Rectangle()
        var targetPosition = Vector2(
            followTarget.position.x - camera.viewportWidth / 2,
            followTarget.position.y - camera.viewportHeight / 2
        )
        // Apply camera offset
        targetPosition += cameraOffset * Constants.PIXELS_PER_UNIT
        // Clamp camera position to bounds
        camera.moveTo(clampCameraToBounds(targetPosition, boundsFollowTargetIsIn))
        camera.update()
    }

    private fun clampCameraToBounds(targetPosition: Vector2, boundsFollowTargetIsIn: Rectangle): Vector2 {
        val halfViewportWidth = camera.viewportWidth / 2
        val halfViewportHeight = camera.viewportHeight / 2

        // Check if the bounds are valid
        if (boundsFollowTargetIsIn.width < halfViewportWidth * 2 || boundsFollowTargetIsIn.height < halfViewportHeight * 2) {
            // If bounds are invalid, return the target position without clamping
            return targetPosition
        }

        val clampedX = targetPosition.x.coerceIn(
            boundsFollowTargetIsIn.x + halfViewportWidth,
            boundsFollowTargetIsIn.x + boundsFollowTargetIsIn.width - halfViewportWidth
        )
        val clampedY = targetPosition.y.coerceIn(
            boundsFollowTargetIsIn.y + halfViewportHeight,
            boundsFollowTargetIsIn.y + boundsFollowTargetIsIn.height - halfViewportHeight
        )

        return Vector2(clampedX, clampedY)
    }

    fun addBounds(bounds: Rectangle) {
        this.bounds.add(bounds)
    }

    var offset: Vector2
        get() = cameraOffset
        set(value) {
            cameraOffset = value
        }

    override fun update(delta: Float) {
        followTarget?.let { target ->
            // Check if the follow target is within any of the bounds, if not, do nothing
            val boundsFollowTargetIsIn = bounds.firstOrNull { it.contains(target.position) }

            if (boundsFollowTargetIsIn == null) {
                println("Target is not in any of the bounds")
                println("Target position: ${target.position}")
                bounds.forEach { println("Bounds: $it") }
                return
            } else {
                println("Target is in bounds: $boundsFollowTargetIsIn")
            }

            var targetPosition = target.position.cpy()
            // Apply camera offset
            targetPosition += cameraOffset * Constants.PIXELS_PER_UNIT

            // Clamp camera position to bounds
            targetPosition = clampCameraToBounds(targetPosition, boundsFollowTargetIsIn)

            // Smoothly move the camera to the target position
            camera.lerpTo(targetPosition, 6.0f * delta)
            camera.update()
        }
    }
}
