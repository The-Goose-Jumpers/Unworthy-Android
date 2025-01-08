package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.core.IGameLoop
import io.jumpinggoose.unworthy.utils.DrawingHelper
import ktx.graphics.use

class FadeEffect : IGameLoop, IGameDrawable {
    private val sprite = Sprite(DrawingHelper.getTexture()).apply {
        color = Color(0f, 0f, 0f, 0f)
        setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }
    private val viewport = ScreenViewport()
    private var isEnabled = false
    private var hasEnded = false
    private var alpha = 0f
    private var fadeDuration = 0
    private var startTime: Long = 0L
    private var isFadeIn = false
    private var onCompleted: (() -> Unit)? = null

    fun start(
        duration: Int,
        fadeIn: Boolean = false,
        onCompleted: (() -> Unit)? = null
    ) {
        if (!isEnabled || hasEnded) {
            alpha = if (fadeIn) 1f else 0f
            startTime = 0L
        }
        fadeDuration = duration
        isFadeIn = fadeIn
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
        // viewport.apply(true)
        batch.use(viewport.camera) {
            sprite.setAlpha(alpha)
            sprite.draw(batch)
        }
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
