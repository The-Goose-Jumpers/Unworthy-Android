package io.jumpinggoose.unworthy

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class UnworthyApp : KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch

    override fun create() {
        KtxAsync.initiate()

        batch = SpriteBatch()

        addScreen(MainMenu(this))
        setScreen<MainMenu>()
    }
}
