package io.jumpinggoose.unworthy

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.jumpinggoose.unworthy.core.AssetManager
import io.jumpinggoose.unworthy.core.Scene
import io.jumpinggoose.unworthy.models.PlayerData
import io.jumpinggoose.unworthy.scenes.Level
import io.jumpinggoose.unworthy.scenes.MainMenu
import io.jumpinggoose.unworthy.utils.DrawingHelper
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.app.KtxInputAdapter
import ktx.async.KtxAsync

class UnworthyApp(
    private val playerDataRepository: PlayerDataRepository
) : KtxGame<Scene>(), KtxInputAdapter {
    private val assetManager: AssetManager by lazy { AssetManager }

    lateinit var batch: SpriteBatch
        private set
    lateinit var bgm: Music
        private set
    lateinit var font: BitmapFont
        private set

    var playerData: PlayerData = PlayerData()
        private set

    override fun create() {
        KtxAsync.initiate()

        batch = SpriteBatch()

        if (Constants.DEBUG) {
            Gdx.app.logLevel = Application.LOG_DEBUG
        }

        val game = this
        KtxAsync.launch {
            assetManager.apply {
                bgm = load<Music>("Sounds/Midnight_Dreams.ogg").apply {
                    isLooping = true
                    volume = 0.5f
                }
                font = load<BitmapFont>("Fonts/chiller.fnt").apply {
                    data.setScale(0.5f)
                }
                addScreen(MainMenu(game))
                addScreen(Level(game))
                setScreen<MainMenu>()
                Gdx.input.inputProcessor = game
            }
        }

        playerDataRepository.getData(
            onSuccess = { data ->
                playerData = data
                Gdx.app.log("UnworthyApp", "Got player data: $playerData")
            },
            onFailure = { exception ->
                Gdx.app.error("UnworthyApp", "Failed to load player data", exception)
            }
        )
    }

    fun updatePlayerData() {
        playerDataRepository.setData(
            playerData,
            onSuccess = {
                Gdx.app.log("UnworthyApp", "Player data saved successfully")
            },
            onFailure = { exception ->
                Gdx.app.error("UnworthyApp", "Failed to save player data", exception)
            }
        )
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return (currentScreen as Scene).touchUp(screenX, screenY, pointer, button)
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assetManager.dispose()
        DrawingHelper.dispose()
    }
}
