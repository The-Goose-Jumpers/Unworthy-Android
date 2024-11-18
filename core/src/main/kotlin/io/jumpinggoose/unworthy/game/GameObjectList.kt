package io.jumpinggoose.unworthy.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class GameObjectList(id: String) : GameObject(id), IGameDrawable, IGameCollideable2D {
    private val children = mutableListOf<GameObject>()

    private val m_bounds = CompositeShape()
    private var m_boundsDirty = true

    override val bounds: CompositeShape
        get() {
            if (m_boundsDirty) {
                m_bounds.clear()
                children.forEach {
                    if (it is IGameCollideable2D) {
                        m_bounds.add(it.bounds)
                    }
                }
                m_boundsDirty = false
            }
            return m_bounds
        }

    fun add(gameObject: GameObject) {
        children.add(gameObject)
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

    override fun update(deltaTime: Float) {
        children.forEach { it.update(deltaTime) }
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
}
