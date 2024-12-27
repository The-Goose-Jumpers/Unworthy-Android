package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.core.IGameLoop
import io.jumpinggoose.unworthy.utils.DrawingHelper
import io.jumpinggoose.unworthy.utils.useColor
import ktx.graphics.use

class FadeEffect : IGameLoop, IGameDrawable {
    private val width = Gdx.graphics.width.toFloat()
    private val height = Gdx.graphics.height.toFloat()
    private val viewport = ScreenViewport()
    private var isEnabled = false
    private var hasEnded = false
    private var alpha = 0f
    private var fadeDuration = 0
    private var startTime: Long = 0L
    private var isFadeIn = false
    var onCompleted: (() -> Unit)? = null

    fun start(duration: Int, fadeIn: Boolean = false, onCompleted: (() -> Unit)? = null) {
        fadeDuration = duration
        isFadeIn = fadeIn
        alpha = if (fadeIn) 1f else 0f
        startTime = 0L
        isEnabled = true
        hasEnded = false
        this.onCompleted = onCompleted
    }

    override fun update(delta: Float) {
        if (!isEnabled || hasEnded) return

        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        val progress = MathUtils.clamp(elapsedTime / fadeDuration.toFloat(), 0f, 1f)

        alpha = if (isFadeIn) 1 - progress else progress

        if ((isFadeIn && alpha <= 0f) || (!isFadeIn && alpha >= 1f)) {
            hasEnded = true
            onCompleted?.invoke()
        }
    }

    override fun draw(batch: SpriteBatch) {
        if (!isEnabled) return
        if (alpha <= 0f) return
        viewport.apply(true)
        batch.use(viewport.camera) {
            it.useColor(Color(0f, 0f, 0f, alpha)) {
                it.draw(DrawingHelper.getTexture(), 0f, 0f, width, height)
            }
        }
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
