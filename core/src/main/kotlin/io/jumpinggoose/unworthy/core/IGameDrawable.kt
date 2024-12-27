package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface IGameDrawable {
    fun draw(batch: SpriteBatch)
    fun drawDebug(batch: SpriteBatch) {}
}
