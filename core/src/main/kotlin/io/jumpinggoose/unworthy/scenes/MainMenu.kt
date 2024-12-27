package io.jumpinggoose.unworthy.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.UnworthyApp
import io.jumpinggoose.unworthy.core.Canvas
import io.jumpinggoose.unworthy.core.Scene
import io.jumpinggoose.unworthy.core.SpriteGameObject
import io.jumpinggoose.unworthy.objects.FadeEffect
import io.jumpinggoose.unworthy.objects.Stars
import ktx.graphics.LetterboxingViewport
import ktx.math.plus
import kotlin.math.cos

class MainMenu(private val game: UnworthyApp) : Scene(game) {

    override var viewport = LetterboxingViewport(
        600f,
        600f,
        Gdx.graphics.width / Gdx.graphics.height.toFloat()
    )

    private val canvas = Canvas(viewport)

    private val titleSprite = SpriteGameObject("title", "UI/title.png")

    private val characterSprite = SpriteGameObject("character", "UI/character_fall.png")
    private val characterStartPosition: Vector2
    private val characterEndPosition: Vector2
    private var isFallingAnimationInProgress = false
    private var animationStartTime = 0L
    private var animationDuration = 5000f
    private val fadeEffect = FadeEffect()

    private val tapToBeginText = SpriteGameObject("tapToBegin", "UI/taptobegin.png")
    var tapToBeginTextTime = 0f

    private val stars = Stars(width = Constants.TARGET_WIDTH, height = Constants.TARGET_HEIGHT)

    val gyroscopeAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Gyroscope)

    init {
        canvas.add(stars, Vector2(0.5f, 0.5f))
        canvas.add(titleSprite, Vector2(0.3f, 0.75f))
        canvas.add(characterSprite, Vector2(0.75f, 0.5f))
        canvas.add(tapToBeginText, Vector2(0.2f, 0.25f))
        characterStartPosition = characterSprite.position.cpy()
        characterEndPosition = Vector2(characterStartPosition.x, -characterSprite.sprite.height)
    }

    override fun show() {
        game.bgm.play()
    }

    override fun update(delta: Float) {
        stars.update(delta)

        if (gyroscopeAvailable) {
            // Calculate target position based on gyroscope input
            val gyroscopeOffset = Vector2(Gdx.input.gyroscopeX * 150f, Gdx.input.gyroscopeY * 50f)
            if (!isFallingAnimationInProgress) {
                val targetPosition = characterStartPosition + gyroscopeOffset
                // Apply damping
                characterSprite.position = characterStartPosition.cpy().lerp(targetPosition, 0.3f)
            }
            // Rotate character based on gyroscope input
            val rotation = Gdx.input.gyroscopeZ * -10f
            characterSprite.rotation = MathUtils.lerp(characterSprite.rotation, rotation, 0.1f)
        }

        if (isFallingAnimationInProgress) {
            if (animationStartTime == 0L) {
                animationStartTime = System.currentTimeMillis()
                fadeEffect.start(1500, false) {
                    game.setScreen<Level>()
                }
            }

            val currentTime = System.currentTimeMillis() - animationStartTime
            val t = currentTime / animationDuration

            val lerpAmount = easeInSine(t)
            characterSprite.position = Vector2(
                MathUtils.lerp(characterSprite.position.x, characterEndPosition.x, lerpAmount),
                MathUtils.lerp(characterSprite.position.y, characterEndPosition.y, lerpAmount)
            )

            if (characterSprite.position.dst(characterEndPosition) < 1f) {
                isFallingAnimationInProgress = false
            }
        }

        fadeEffect.update(delta)

        // Slowly dim of the tap to begin text back and forth
        tapToBeginTextTime += delta * 3
        tapToBeginText.setAlpha(0.5f + (1f - 0.5f) * 0.5f * (1f + MathUtils.sin(tapToBeginTextTime)))
    }

    override fun draw() {
        canvas.draw(game.batch)
        fadeEffect.draw(game.batch)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        isFallingAnimationInProgress = true
        return true
    }

    override fun resize(width: Int, height: Int) {
        canvas.resize(width, height)
        fadeEffect.resize(width, height)
    }

    override fun dispose() {
        canvas.dispose()
    }

    private fun easeInSine(t: Float): Float {
        return 1 - cos(t * MathUtils.PI / 2)
    }
}
