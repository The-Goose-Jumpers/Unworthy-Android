package io.jumpinggoose.unworthy.game

import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.utils.overlaps

class CompositeShape : Shape2D {
    val shapes = mutableListOf<Shape2D>()

    fun add(shape: Shape2D) {
        shapes.add(shape)
    }

    fun remove(shape: Shape2D) {
        shapes.remove(shape)
    }

    fun clear() {
        shapes.clear()
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
}
