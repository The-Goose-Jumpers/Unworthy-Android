package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.utils.overlaps

class CompositeShape : Shape2D {
    val shapes = mutableListOf<Shape2D>()

    private var m_dirty: Boolean = false
    private var m_x: Float = 0f
    private var m_y: Float = 0f
    private var m_width: Float = 0f
    private var m_height: Float = 0f

    fun add(shape: Shape2D) {
        shapes.add(shape)
        m_dirty = true
    }

    fun remove(shape: Shape2D) {
        shapes.remove(shape)
        m_dirty = true
    }

    fun clear() {
        shapes.clear()
        m_dirty = false
        m_width = 0f
        m_height = 0f
    }

    override fun contains(point: Vector2): Boolean {
        return shapes.any { it.contains(point) }
    }

    override fun contains(x: Float, y: Float): Boolean {
        return shapes.any { it.contains(x, y) }
    }

    fun overlaps(other: CompositeShape): Boolean {
        return shapes.any { shape -> other.shapes.any { shape.overlaps(it) } }
    }

    fun overlaps(other: Shape2D): Boolean {
        return shapes.any { it.overlaps(other) }
    }

    private fun recalculateSize() {
        var minX: Float = Float.MAX_VALUE;
        var minY: Float = Float.MAX_VALUE;
        var maxX: Float = Float.MIN_VALUE;
        var maxY: Float = Float.MIN_VALUE;
        for (shape in shapes) {
            var left: Float
            var top: Float
            var right: Float
            var bottom: Float
            when (shape) {
                is Circle -> {
                    left = shape.x - shape.radius
                    top = shape.y - shape.radius
                    right = shape.x + shape.radius
                    bottom = shape.y + shape.radius
                }
                is Rectangle -> {
                    left = shape.x
                    top = shape.y
                    right = shape.x + shape.width
                    bottom = shape.y + shape.height
                }
                is CompositeShape -> {
                    left = shape.x
                    top = shape.y
                    right = shape.x + shape.width
                    bottom = shape.y + shape.height
                }
                else -> continue
            }
            minX = minX.coerceAtMost(left)
            minY = minY.coerceAtMost(top)
            maxX = maxX.coerceAtLeast(right)
            maxY = maxY.coerceAtLeast(bottom)
        }
        m_x = minX
        m_y = minY
        m_width = maxX - minX
        m_height = maxY - minY
    }

    val x: Float
        get() {
            if (m_dirty) {
                recalculateSize()
                m_dirty = false
            }
            return m_x
        }

    val y: Float
        get() {
            if (m_dirty) {
                recalculateSize()
                m_dirty = false
            }
            return m_y
        }

    val width: Float
        get() {
            if (m_dirty) {
                recalculateSize()
                m_dirty = false
            }
            return m_width
        }

    val height: Float
        get() {
            if (m_dirty) {
                recalculateSize()
                m_dirty = false
            }
            return m_height
        }
}
