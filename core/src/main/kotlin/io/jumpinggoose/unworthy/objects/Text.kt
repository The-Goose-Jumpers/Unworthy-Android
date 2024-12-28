package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable

class Text(
    var text: String,
    var font: BitmapFont
) : GameObject(), IGameDrawable {

    override fun update(delta: Float) {}

    override fun draw(batch: SpriteBatch) {
        font.draw(batch, text, globalPosition.x, globalPosition.y)
    }
}
