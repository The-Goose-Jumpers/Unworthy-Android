package io.jumpinggoose.unworthy.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.Sprite
import io.jumpinggoose.unworthy.Constants
import ktx.assets.toInternalFile

fun Sprite(path: String) : Sprite {
    return Sprite(Texture(path.toInternalFile(), true).apply { setFilter(Linear, Linear) })
}

fun Sprite.setRelativePosition(x: Float, y: Float) {
    setOriginBasedPosition(x * Constants.TARGET_WIDTH, y * Constants.TARGET_HEIGHT)
}
