package io.jumpinggoose.unworthy.scenes

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FillViewport
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.UnworthyApp
import io.jumpinggoose.unworthy.core.AssetManager
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
import io.jumpinggoose.unworthy.objects.Text
import io.jumpinggoose.unworthy.objects.entities.IEntity
import io.jumpinggoose.unworthy.objects.entities.Player
import io.jumpinggoose.unworthy.utils.drawRectangle
import io.jumpinggoose.unworthy.utils.fillRectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.graphics.LetterboxingViewport
import ktx.graphics.use

class Level(game: UnworthyApp) : Scene(game) {

    override val viewport = ExtendViewport(
        Constants.TARGET_WIDTH.toFloat(),
        Constants.TARGET_HEIGHT.toFloat()
    )

    private val sceneLoader = SceneLoader("level")

    private var startTime: Long = 0
    private val fadeEffect = FadeEffect()
    private lateinit var vignette: SpriteGameObject
    private var vignetteColor = Color.BLACK.cpy()
    private var currentLerpTime = 0f
    private var isFlashingVignetteRed = false
    private lateinit var player: Player

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
    private var timeSinceLastInteraction = 0f
    private var hudAlpha = 1f
    private val alphaShaderProgram: ShaderProgram = ShaderProgram(
        Gdx.files.internal("Shaders/alpha.vert"),
        Gdx.files.internal("Shaders/alpha.frag")
    )

    val boundaries = CompositeShape()
    val killTriggers = CompositeShape()
    lateinit var cameraController: CameraController
    lateinit var terrain: Terrain
    val enemies = mutableListOf<IEntity>()

    val statsCounterText = Text("Kills: 0  Deaths: 0", game.font, Vector2(1f, 1f))

    lateinit var analogControl: ControlAnalog
    lateinit var attackButton: ControlButton
    lateinit var jumpButton: ControlButton
    lateinit var pauseButton: ControlButton

    private var otherTextureAssets = listOf(
        AssetManager.loadTextureAsync("UI/vignette.png"),
        AssetManager.loadTextureAsync("UI/life_clock.png"),
        AssetManager.loadTextureAsync("UI/attack_button.png"),
        AssetManager.loadTextureAsync("UI/jump_button.png"),
        AssetManager.loadTextureAsync("UI/pause_button.png")
    )

    init {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
        sceneLoader.scheduleLoadingOfTextureAssets()
        alphaShaderProgram.bind()
        alphaShaderProgram.setUniformf("u_alpha", 1f)
    }

    override fun load(onCompletion: () -> Unit) {
        KtxAsync.launch {
            sceneLoader.loadTextureAssets()
            otherTextureAssets.joinAll()
            createGameObjects()
            delay(200)
            onCompletion()
        }
    }

    private fun createGameObjects() {
        sceneLoader.createObjects().forEach { add(it) }
        terrain = findGameObjectById("Terrain") as Terrain

        cameraController = CameraController(camera)
        sceneLoader.getCameraBounds().forEach { bounds ->
            cameraController.addBounds(bounds)
        }

        sceneLoader.getLevelBoundaries().forEach { bounds ->
            boundaries.add(bounds)
        }

        sceneLoader.getKillTriggers().forEach { bounds ->
            killTriggers.add(bounds)
        }

        val playerFactory = sceneLoader.getPlayer() ?: throw Exception("Player not found in scene.")
        player = Player(this, playerFactory.position)
        add(player)

        sceneLoader.getEntities().forEach { obj ->
            val entity = obj.create() ?: return@forEach
            add(entity)
            if (entity is IEntity && !entity.isFriendly) {
                enemies.add(entity)
            }
        }

        hudCanvas.add(HPIndicator("HPIndicator", player), Vector2(0.05f, 0.95f))
        hudCanvas.add(statsCounterText, Vector2(0.9f, 0.995f))

        analogControl = ControlAnalog(
            radius = 300f,
            touchRadius = 750f,
            fadeOutWhenIdle = false
        )
        attackButton = ControlButton(
            radius = 175f,
            texture = AssetManager.get<Texture>("UI/attack_button.png"),
            buttonColor = Color(0f, 0f, 0f, 0.25f),
            buttonTouchedColor = Color(1f, 1f, 1f, 0.1f)
        )
        jumpButton = ControlButton(
            radius = 175f,
            texture = AssetManager.get<Texture>("UI/jump_button.png"),
            buttonColor = Color(0f, 0f, 0f, 0.25f),
            buttonTouchedColor = Color(1f, 1f, 1f, 0.1f)
        )
        pauseButton = ControlButton(
            radius = 80f,
            texture = AssetManager.get<Texture>("UI/pause_button.png"),
            textureTouchedColor = Color(0xdd100eff.toInt()),
            buttonColor = Color(0f, 0f, 0f, 0.25f),
            buttonTouchedColor = Color(0f, 0f, 0f, 0.25f)
        )

        if (Gdx.app.type == ApplicationType.Android || Gdx.app.type == ApplicationType.iOS) {
            hudCanvas.add(analogControl, Vector2(0.15f, 0.225f))
            hudCanvas.add(attackButton, Vector2(0.825f, 0.155f))
            hudCanvas.add(jumpButton, Vector2(0.925f, 0.275f))
            hudCanvas.add(pauseButton, Vector2(0.95f, 0.925f))
        }

        vignette = SpriteGameObject("Vignette", "UI/vignette.png")
        vignetteCanvas.add(vignette, Vector2(0.5f, 0.5f))
    }

    override fun show() {
        startTime = System.currentTimeMillis()
        player.initialize()
        enemies.forEach { it.initialize() }
        fadeEffect.start(1500, true)
    }

    override fun pause() {
        updateServerPlayerData()
    }

    override fun resume() {
        startTime = System.currentTimeMillis()
    }

    fun updateServerPlayerData() {
        game.playerData.totalPlaytime += System.currentTimeMillis() - startTime
        game.playerData.enemiesDefeated += player.killCount
        game.playerData.deaths += player.deathCount
        game.updatePlayerData()
        startTime = System.currentTimeMillis()
        player.killCount = 0
        player.deathCount = 0
    }

    fun restart() {
        updateServerPlayerData()
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
        if (timeSinceLastInteraction >= 10f) {
            hudAlpha = MathUtils.lerp(hudAlpha, 0f, 1.5f * delta)
            hudAlpha.coerceIn(0f, 1f)
            alphaShaderProgram.setUniformf("u_alpha", hudAlpha)
        } else {
            timeSinceLastInteraction += delta
        }

        hudCanvas.update(delta)
        statsCounterText.text = "Kills: ${game.playerData.enemiesDefeated + player.killCount}  Deaths: ${game.playerData.deaths}"
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
        if (Constants.DEBUG) {
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
        if (timeSinceLastInteraction >= 10f) {
            uiBatch.shader = alphaShaderProgram
            hudCanvas.draw(uiBatch)
            uiBatch.shader = null
        } else {
            hudCanvas.draw(uiBatch)
        }

        // Draw fade effect
        fadeEffect.draw(uiBatch)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        onAnyInteraction()
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        onAnyInteraction()
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        onAnyInteraction()
        return false
    }

    fun onAnyInteraction() {
        alphaShaderProgram.setUniformf("u_alpha", 1f)
        timeSinceLastInteraction = 0f
        hudAlpha = 1f
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
        alphaShaderProgram.dispose()
        super.dispose()
    }
}
