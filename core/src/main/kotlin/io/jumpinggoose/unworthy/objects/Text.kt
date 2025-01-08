package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable

class Text(
    text: String,
    font: BitmapFont,
    val origin: Vector2 = Vector2(0f, 0f)
) : GameObject(), IGameDrawable {

    private val glyphLayout: GlyphLayout = GlyphLayout().apply {
        setText(font, text)
    }

    var text: String = text
        set(value) {
            if (field == value) return
            field = value
            glyphLayout.setText(font, value)
        }

    var font: BitmapFont = font
        set(value) {
            field = value
            glyphLayout.setText(value, text)
        }

    init {
        glyphLayout.setText(font, text)
    }

    override fun update(delta: Float) {}

    override fun draw(batch: SpriteBatch) {
        val adjustedX = globalPosition.x - glyphLayout.width * origin.x
        val adjustedY = globalPosition.y - glyphLayout.height * origin.y
        font.draw(batch, glyphLayout, adjustedX, adjustedY)
    }

    fun setOrigin(x: Float, y: Float) {
        origin.set(x, y)
    }
}
