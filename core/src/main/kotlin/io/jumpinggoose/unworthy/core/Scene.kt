package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport
import io.jumpinggoose.unworthy.UnworthyApp
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use

abstract class Scene(
    val game: UnworthyApp
) : GameObjectList(), KtxScreen, KtxInputAdapter {

    abstract val viewport: Viewport
    val camera: OrthographicCamera
        get() = viewport.camera as OrthographicCamera

    open fun load(onCompletion: () -> Unit) {}

    open fun draw() {
        // viewport.apply()
        game.batch.use(camera) {
            super<GameObjectList>.draw(it)
        }
    }

    override fun render(delta: Float) {
        update(delta)
        clearScreen(0f, 0f, 0f)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        super<GameObjectList>.dispose()
    }
}
