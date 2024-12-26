package io.jumpinggoose.unworthy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.jumpinggoose.unworthy.core.Scene
import io.jumpinggoose.unworthy.scenes.Level
import io.jumpinggoose.unworthy.scenes.MainMenu
import io.jumpinggoose.unworthy.utils.DrawingHelper
import ktx.app.KtxGame
import ktx.app.KtxInputAdapter
import ktx.assets.async.AssetStorage
import ktx.assets.toInternalFile
import ktx.async.KtxAsync

class UnworthyApp : KtxGame<Scene>(), KtxInputAdapter {
    lateinit var batch: SpriteBatch
        private set
    lateinit var assetStorage: AssetStorage
        private set
    lateinit var bgm: Music
        private set

    private val currentScene: Scene
        get() = currentScreen as Scene

    override fun create() {
        KtxAsync.initiate()

        batch = SpriteBatch()
        assetStorage = AssetStorage()

        addScreen(MainMenu(this))
        addScreen(Level(this))

        Gdx.input.inputProcessor = this
        bgm = Gdx.audio.newMusic("Sounds/Midnight_Dreams.ogg".toInternalFile()).apply {
            isLooping = true
            volume = 0.5f
        }

        setScreen<MainMenu>()
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        println("Touched at $screenX, $screenY, pointer: $pointer, button: $button")
        return currentScene.touchUp(screenX, screenY, pointer, button)
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assetStorage.dispose()
        bgm.dispose()
        DrawingHelper.dispose()
    }
}
