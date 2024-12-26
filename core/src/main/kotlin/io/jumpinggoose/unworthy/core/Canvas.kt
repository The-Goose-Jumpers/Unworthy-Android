package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.graphics.use

class Canvas(
    val viewport: Viewport
) : GameObjectList() {

    val camera: Camera = viewport.camera

    init {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    private val m_bounds: Rectangle = Rectangle(0f, 0f, camera.viewportWidth, camera.viewportHeight)
    override val bounds: Rectangle
        get() = m_bounds

    fun add(obj: GameObject, position: Vector2 = Vector2(), isAbsolute: Boolean = false) {
        val absolutePosition = if (isAbsolute) position else Vector2(position.x * m_bounds.width, position.y * m_bounds.height)
        obj.position = absolutePosition
        add(obj)
    }

    override fun draw(batch: SpriteBatch) {
        viewport.apply(true)
        batch.use(camera) {
            super.draw(batch)
        }
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
