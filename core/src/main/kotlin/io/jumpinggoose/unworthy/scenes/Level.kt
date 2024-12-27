package io.jumpinggoose.unworthy.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FillViewport
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.UnworthyApp
import io.jumpinggoose.unworthy.core.CameraController
import io.jumpinggoose.unworthy.core.Canvas
import io.jumpinggoose.unworthy.core.CompositeShape
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.core.Scene
import io.jumpinggoose.unworthy.core.SpriteGameObject
import io.jumpinggoose.unworthy.core.sceneloader.SceneLoader
import io.jumpinggoose.unworthy.input.ControlAnalog
import io.jumpinggoose.unworthy.input.ControlButton
import io.jumpinggoose.unworthy.objects.FadeEffect
import io.jumpinggoose.unworthy.objects.HPIndicator
import io.jumpinggoose.unworthy.objects.Terrain
import io.jumpinggoose.unworthy.objects.entities.IEntity
import io.jumpinggoose.unworthy.objects.entities.Player
import io.jumpinggoose.unworthy.utils.drawRectangle
import io.jumpinggoose.unworthy.utils.fillRectangle
import ktx.graphics.LetterboxingViewport
import ktx.graphics.use

class Level(private val game: UnworthyApp) : Scene(game) {

    override val viewport = ExtendViewport(
        Constants.TARGET_WIDTH.toFloat(),
        Constants.TARGET_HEIGHT.toFloat()
    )

    private val fadeEffect = FadeEffect()
    private val vignette = SpriteGameObject("vignette", "UI/vignette.png")
    private var vignetteColor = Color.BLACK.cpy()
    private var currentLerpTime = 0f
    private var isFlashingVignetteRed = false
    private val player: Player

    private val vignetteCanvas = Canvas(FillViewport(
        Constants.TARGET_WIDTH.toFloat(),
        Constants.TARGET_HEIGHT.toFloat()
    ))
    private val hudCanvas = Canvas(LetterboxingViewport(
        600f,
        600f,
        Gdx.graphics.width / Gdx.graphics.height.toFloat()
    ))
    private val uiBatch = SpriteBatch()

    val boundaries = CompositeShape()
    val killTriggers = CompositeShape()
    val cameraController: CameraController
    val terrain: Terrain
    val enemies = mutableListOf<IEntity>()

    val analogControl: ControlAnalog
    val attackButton: ControlButton
    val jumpButton: ControlButton

    init {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)

        val scene = SceneLoader("level")
        scene.createObjects().forEach { add(it) }
        terrain = findGameObjectById("Terrain") as Terrain

        cameraController = CameraController(camera)
        scene.getCameraBounds().forEach { bounds ->
            cameraController.addBounds(bounds)
        }

        scene.getLevelBoundaries().forEach { bounds ->
            boundaries.add(bounds)
        }

        scene.getKillTriggers().forEach { bounds ->
            killTriggers.add(bounds)
        }

        val playerFactory = scene.getPlayer() ?: throw Exception("Player not found in scene.")
        player = Player(this, playerFactory.position)
        add(player)

        scene.getEntities().forEach { obj ->
            val entity = obj.create() ?: return@forEach
            add(entity)
            if (entity is IEntity && !entity.isFriendly) {
                enemies.add(entity)
            }
        }

        analogControl = ControlAnalog(
            radius = Gdx.graphics.width * 0.1f
        )
        attackButton = ControlButton(
            radius = Gdx.graphics.ppiX * 0.325f,
            texture = Texture("UI/attack_button.png").apply { setFilter(Linear, Linear) },
            buttonColor = Color(0f, 0f, 0f, 0.25f),
            buttonTouchedColor = Color(1f, 1f, 1f, 0.1f)
        )
        jumpButton = ControlButton(
            radius = Gdx.graphics.ppiX * 0.325f,
            texture = Texture("UI/jump_button.png").apply { setFilter(Linear, Linear) },
            buttonColor = Color(0f, 0f, 0f, 0.25f),
            buttonTouchedColor = Color(1f, 1f, 1f, 0.1f)
        )

        hudCanvas.add(HPIndicator("HPIndicator", player), Vector2(0.05f, 0.95f))
        hudCanvas.add(analogControl, Vector2(0.15f, 0.225f))
        hudCanvas.add(attackButton, Vector2(0.825f, 0.15f))
        hudCanvas.add(jumpButton, Vector2(0.925f, 0.25f))

        vignetteCanvas.add(vignette, Vector2(0.5f, 0.5f))
    }

    override fun show() {
        player.initialize()
        enemies.forEach { it.initialize() }
        fadeEffect.start(1500, true)
    }

    fun restart() {
        Gdx.app.log("Level", "Restarting level")
        fadeEffect.start(1500, false) {
            reset()
            fadeEffect.start(1500, true)
        }
    }

    fun flashVignetteRed() {
        isFlashingVignetteRed = true
        currentLerpTime = 0f
    }

    override fun update(delta: Float) {
        hudCanvas.update(delta)
        fadeEffect.update(delta)
        super.update(delta)

        if (isFlashingVignetteRed) {
            currentLerpTime += delta
            if (currentLerpTime > 0.5f) {
                currentLerpTime = 0.5f
            }

            val factor = currentLerpTime / 0.5f
            vignetteColor.lerp(Color.RED, factor)
            if (vignetteColor == Color.RED) {
                isFlashingVignetteRed = false
                currentLerpTime = 0f
            }
        } else if (vignetteColor != Color.BLACK) {
            currentLerpTime += delta
            if (currentLerpTime > 1f) {
                currentLerpTime = 1f
            }
            vignetteColor.lerp(Color.BLACK, currentLerpTime)
        }
        vignette.setColor(vignetteColor)
    }

    override fun draw() {
        super.draw()
        if (Constants.DRAW_DEBUG) {
            game.batch.use(camera) {
                boundaries.shapes.forEach{ bound ->
                    if (bound is Rectangle) {
                        it.drawRectangle(bound, Color.YELLOW, 3f)
                    }
                }
                killTriggers.shapes.forEach{ bound ->
                    if (bound is Rectangle) {
                        it.fillRectangle(bound, Color(1f, 0f, 0f, 0.3f))
                    }
                }
                player.drawDebug(it)
                enemies.forEach { enemy ->
                    (enemy as IGameDrawable).drawDebug(it)
                }
            }
        }
        drawUI()
    }

    private fun drawUI() {
        // Draw vignette
        vignetteCanvas.draw(uiBatch)
        // Draw HUD elements
        hudCanvas.draw(uiBatch)
        // Draw fade effect
        fadeEffect.draw(uiBatch)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        vignetteCanvas.resize(width, height)
        hudCanvas.resize(width, height)
        fadeEffect.resize(width, height)
    }

    override fun dispose() {
        vignetteCanvas.dispose()
        hudCanvas.dispose()
        super.dispose()
    }
}
