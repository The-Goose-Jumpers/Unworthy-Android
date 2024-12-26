package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Shape2D
import java.util.UUID

abstract class GameObjectList(
    id: String = UUID.randomUUID().toString()
) : GameObject(id), IGameDrawable, IGameCollideable2D {
    val children = mutableListOf<GameObject>()

    private val m_bounds = CompositeShape()
    private var m_boundsDirty = true

    val width: Float
        get() {
            return (bounds as CompositeShape).width
        }
    val height: Float
        get() {
            return (bounds as CompositeShape).height
        }

    override val bounds: Shape2D
        get() {
            if (m_boundsDirty) {
                m_boundsDirty = false
                m_bounds.clear()
                children.forEach {
                    if (it is IGameCollideable2D) {
                        m_bounds.add(it.bounds)
                    }
                }
            }
            return m_bounds
        }

    fun add(gameObject: GameObject) {
        children.add(gameObject)
        children.sortBy { it.layer }
        gameObject.parent = this
        if (gameObject is IGameCollideable2D) {
            m_boundsDirty = true
        }
    }

    fun remove(gameObject: GameObject) {
        children.remove(gameObject)
        gameObject.parent = null
        if (gameObject is IGameCollideable2D) {
            m_boundsDirty = true
        }
    }

    fun findGameObjectById(id: String): GameObject? {
        return children.find { it.id == id }
    }

    override fun update(delta: Float) {
        children.forEach { it.update(delta) }
    }

    override fun draw(batch: SpriteBatch) {
        children.forEach {
            if (it is IGameDrawable) {
                it.draw(batch)
            }
        }
    }

    override fun reset() {
        children.forEach { it.reset() }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
    }
}
