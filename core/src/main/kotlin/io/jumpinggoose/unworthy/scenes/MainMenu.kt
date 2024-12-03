package io.jumpinggoose.unworthy.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.utils.Sprite
import io.jumpinggoose.unworthy.UnworthyApp
import io.jumpinggoose.unworthy.gameobjects.Stars
import io.jumpinggoose.unworthy.utils.setRelativePosition
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.*

class MainMenu(private val game: UnworthyApp) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Constants.TARGET_WIDTH.toFloat(), Constants.TARGET_HEIGHT.toFloat(), camera)

    private val titleSprite = Sprite("UI/title.png").apply {
        setRelativePosition(0.3f, 0.75f)
    }

    private val characterSprite = Sprite("UI/character_fall.png").apply {
        setRelativePosition(0.75f, 0.5f)
    }
    private val characterStartPosition = Vector2(characterSprite.x, characterSprite.y)

    private val tapToBeginText = Sprite("UI/taptobegin.png").apply {
        setRelativePosition(0.2f, 0.25f)
    }
    var tapToBeginTextTime = 0f

    private val stars = Stars(Vector2(), Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT)

    val spriteObjects = listOf(titleSprite, tapToBeginText, characterSprite)

    val gyroscopeAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Gyroscope)

    fun update(delta: Float) {
        stars.update(delta)
        if (gyroscopeAvailable) {
            // Calculate target position based on gyroscope input
            val gyroscopeOffset = Vector2(Gdx.input.gyroscopeX * 150f, Gdx.input.gyroscopeY * 50f)
            val targetPosition = characterStartPosition + gyroscopeOffset
            // Apply damping
            val newPosition = characterStartPosition.cpy().lerp(targetPosition, 0.3f)
            characterSprite.setPosition(newPosition.x, newPosition.y)
            // Rotate character based on gyroscope input
            val rotation = Gdx.input.gyroscopeZ * -10f
            characterSprite.rotation = MathUtils.lerp(characterSprite.rotation, rotation, 0.1f)
        }
        // Slowly dim of the tap to begin text back and forth
        tapToBeginTextTime += delta * 3
        tapToBeginText.setAlpha(0.5f + (1f - 0.5f) * 0.5f * (1f + MathUtils.sin(tapToBeginTextTime)))
    }

    fun draw() {
        game.batch.use(camera) {
            stars.draw(it)
            spriteObjects.forEach { sprite ->
                sprite.draw(it)
            }
        }
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        spriteObjects.forEach {
            it.texture.disposeSafely()
        }
    }
}
