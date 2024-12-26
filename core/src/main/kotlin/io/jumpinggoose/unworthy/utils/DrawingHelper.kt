package io.jumpinggoose.unworthy.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import space.earlygrey.shapedrawer.ShapeDrawer

object DrawingHelper : Disposable {
    private var pixel: TextureRegion

    init {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        pixel = TextureRegion(Texture(pixmap))
        pixmap.dispose()
    }

    fun getTexture(): TextureRegion {
        return pixel
    }

    fun fillRectangle(batch: SpriteBatch, x: Float, y: Float, width: Float, height: Float, color: Color) {
        val shapeDrawer = ShapeDrawer(batch, pixel)
        shapeDrawer.filledRectangle(x, y, width, height, color)
    }

    fun drawRectangle(batch: SpriteBatch, x: Float, y: Float, width: Float, height: Float, color: Color, thickness: Float) {
        val shapeDrawer = ShapeDrawer(batch, pixel)
        shapeDrawer.rectangle(x, y, width, height, color, thickness)
    }

    fun drawCircle(batch: SpriteBatch, x: Float, y: Float, radius: Float, color: Color, thickness: Float) {
        val shapeDrawer = ShapeDrawer(batch, pixel)
        shapeDrawer.setColor(color)
        shapeDrawer.circle(x, y, radius, thickness)
    }

    override fun dispose() {
        pixel.texture.dispose()
    }
}
