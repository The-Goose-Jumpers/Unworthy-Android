package io.jumpinggoose.unworthy.gameobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.game.GameObjectList
import kotlin.random.Random

class Stars(
    position: Vector2,
    width: Int,
    height: Int
) : GameObjectList("stars") {

    init {
        if (!isInitialized()) {
            layer1 = createStarsTexture(Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT, Color.WHITE)
            layer2 = createStarsTexture(Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT, Color.WHITE)
        }
        this.position = position
        add(ScrollingBackground("layer1", layer1, Vector2.Zero.cpy(), width, height, Vector2(0.0f, 0.05f)))
        add(ScrollingBackground("layer2", layer2, Vector2.Zero.cpy(), width, height, Vector2(0.0f, 0.1f)))
    }

    private companion object {
        lateinit var layer1: Texture
        lateinit var layer2: Texture

        const val MIN_RADIUS = 3
        const val MAX_RADIUS = 8
        const val MIN_DISTANCE = 25
        const val NUMBER_OF_STARS = 80

        fun isInitialized() = ::layer1.isInitialized && ::layer2.isInitialized

        fun createStarsTexture(width: Int, height: Int, color: Color): Texture {
            val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
            pixmap.setColor(0f, 0f, 0f, 0f) // Transparent background
            pixmap.fill()

            val starPositions = mutableListOf<Vector2>()
            val random = Random(System.currentTimeMillis())

            pixmap.setColor(color)
            while (starPositions.size < NUMBER_OF_STARS) {
                // Generate a random position
                var newPos = Vector2(random.nextInt(width).toFloat(), random.nextInt(height).toFloat())
                // Check the distance to all existing stars
                var isTooClose = starPositions.any { pos -> pos.dst(newPos) < MIN_DISTANCE }

                // If the new star is too close to an existing star, skip it
                if (isTooClose) continue

                // Generate random radius
                val radius = random.nextInt(MIN_RADIUS, MAX_RADIUS + 1)

                // Draw circle at the generated position
                pixmap.fillCircle(newPos.x.toInt(), newPos.y.toInt(), radius)
                starPositions.add(newPos)
            }

            val texture = Texture(pixmap)
            pixmap.dispose()
            return texture
        }
    }
}

