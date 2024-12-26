package io.jumpinggoose.unworthy.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
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
import io.jumpinggoose.unworthy.core.Scene
import io.jumpinggoose.unworthy.core.SpriteGameObject
import io.jumpinggoose.unworthy.core.sceneloader.SceneLoader
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
    private var vignetteColor = Color.BLACK
    private var currentLerpTime = 0f
    private var isFlashingVignetteRed = false
    private val player: Player

    private val vignetteCanvas = Canvas(FillViewport(
        Constants.TARGET_WIDTH.toFloat(),
        Constants.TARGET_HEIGHT.toFloat()
    ))
    private val uiCanvas: Canvas
    private val uiBatch: SpriteBatch = SpriteBatch()

    val boundaries = CompositeShape()
    val killTriggers = CompositeShape()
    val cameraController: CameraController
    val terrain: Terrain
    val enemies = mutableListOf<IEntity>()

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

        // scene.getEntities().forEach { obj ->
        //     val entity = obj.create() ?: return@forEach
        //     add(entity)
        //     if (entity is IEntity && !entity.isFriendly) {
        //         enemies.add(entity)
        //     }
        // }

        uiCanvas = Canvas(LetterboxingViewport(
            600f,
            600f,
            Gdx.graphics.width / Gdx.graphics.height.toFloat()
        ))
        uiCanvas.add(HPIndicator("HPIndicator", player), Vector2(0.05f, 0.95f))
        uiCanvas.add(fadeEffect)

        vignetteCanvas.add(vignette, Vector2(0.5f, 0.5f))
        vignette.setColor(vignetteColor)
    }

    override fun show() {
        player.initialize()
        fadeEffect.start(1500, true)
    }

    fun restart() {
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
        super.update(delta)

        fadeEffect.update(delta)

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

            val factor = currentLerpTime / 1f
            vignetteColor.lerp(Color.BLACK, factor)
        }
    }

    override fun draw() {
        super.draw()
        if (Constants.DRAW_DEBUG) {
            game.batch.use(camera) {
                boundaries.shapes.forEach{ bound ->
                    if (bound is Rectangle) {
                        it.drawRectangle(bound, Color.YELLOW, 1.5f)
                    }
                }
                killTriggers.shapes.forEach{ bound ->
                    if (bound is Rectangle) {
                        it.fillRectangle(bound, Color(1f, 0f, 0f, 0.25f))
                    }
                }
                player.drawDebug(it)
            }
        }
        drawUI()
    }

    private fun drawUI() {
        // Draw vignette
        vignetteCanvas.draw(uiBatch)

        // Draw HUD elements and fade effect
        uiCanvas.draw(uiBatch)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        uiCanvas.resize(width, height)
    }

    override fun dispose() {
        uiCanvas.dispose()
        super.dispose()
    }
}
