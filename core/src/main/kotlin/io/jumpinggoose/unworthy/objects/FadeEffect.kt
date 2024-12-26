package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.utils.DrawingHelper
import io.jumpinggoose.unworthy.utils.drawWithColor

class FadeEffect : GameObject(), IGameDrawable {
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
        val texture = DrawingHelper.getTexture()
        val width = Constants.TARGET_WIDTH.toFloat()
        val height = Constants.TARGET_HEIGHT.toFloat()
        batch.drawWithColor(Color(0f, 0f, 0f, alpha)) {
            batch.draw(texture, position.x, position.y, width, height)
        }
    }
}
