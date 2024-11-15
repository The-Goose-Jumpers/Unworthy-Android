package io.jumpinggoose.unworthy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.math.*

class MainMenu(private val game: UnworthyApp) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT, camera)

    private val titleSprite = Sprite("UI/title.png").apply {
        setOriginCenter()
        setRelativePosition(0.3f, 0.75f)
    }

    private val characterSprite = Sprite("UI/character_fall.png").apply {
        setOriginCenter()
        setRelativePosition(0.75f, 0.5f)
    }
    private val characterStartPosition = Vector2(characterSprite.x, characterSprite.y)

    private val startButtonSprite = Sprite("UI/start_button.png").apply {
        setOrigin(0f, 0f)
        setRelativePosition(0.1f, 0.5f)
    }

    val spriteObjects = listOf(titleSprite, characterSprite, startButtonSprite)

    val gyroscopeAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Gyroscope)

    fun update(delta: Float) {
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
    }

    fun draw() {
        game.batch.use(camera) {
            spriteObjects.forEach { sprite ->
                sprite.draw(game.batch)
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
