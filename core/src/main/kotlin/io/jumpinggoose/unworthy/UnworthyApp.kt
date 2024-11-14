package io.jumpinggoose.unworthy

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class UnworthyApp : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(MainMenu())
        setScreen<MainMenu>()
    }
}

