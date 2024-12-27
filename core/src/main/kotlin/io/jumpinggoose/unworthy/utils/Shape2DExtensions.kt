package io.jumpinggoose.unworthy.utils

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.core.CompositeShape
import java.lang.UnsupportedOperationException
import kotlin.math.max
import kotlin.math.min

fun Shape2D.overlaps(other: Shape2D): Boolean {
    return when {
        this is Rectangle && other is Rectangle -> return this.overlaps(other)
        this is Rectangle && other is Circle -> return Intersector.overlaps(other, this)
        this is Rectangle && other is CompositeShape -> return other.overlaps(this)

        this is Circle && other is Circle -> return this.overlaps(other)
        this is Circle && other is Rectangle -> return Intersector.overlaps(this, other)
        this is Circle && other is CompositeShape -> return other.overlaps(this)

        this is CompositeShape && other is CompositeShape -> this.overlaps(other)
        this is CompositeShape && other is Rectangle -> this.overlaps(other)
        this is CompositeShape && other is Circle -> this.overlaps(other)

        else -> throw UnsupportedOperationException("overlaps between ${this::class} and ${other::class}")
    }
}

fun Shape2D.getPenetrationVector(other: Shape2D): Vector2 {
    return when {
        this is Rectangle && other is Rectangle -> penetrationVector(this, other)
        this is Rectangle && other is Circle -> penetrationVector(this, other)
        this is Rectangle && other is CompositeShape -> penetrationVector(this, other)

        this is Circle && other is Circle -> penetrationVector(this, other)
        this is Circle && other is Rectangle -> penetrationVector(this, other)
        this is Circle && other is CompositeShape -> penetrationVector(this, other)

        this is CompositeShape && other is CompositeShape -> penetrationVector(this, other)
        this is CompositeShape && other is Rectangle -> penetrationVector(this, other)
        this is CompositeShape && other is Circle -> penetrationVector(this, other)

        else -> throw UnsupportedOperationException("getPenetrationVector between ${this::class} and ${other::class}")
    }
}

private fun penetrationVector(circle1: Circle, circle2: Circle): Vector2 {
    if (!Intersector.overlaps(circle1, circle2)) {
        return Vector2()
    }

    val displacement = Vector2(circle2.x - circle1.x, circle2.y - circle1.y)
    val combinedRadius = circle1.radius + circle2.radius

    val desiredDisplacement = if (displacement.isZero) {
        Vector2(0f, -combinedRadius)
    } else {
        displacement.nor().scl(combinedRadius)
    }

    return displacement.sub(desiredDisplacement)
}

private fun penetrationVector(rect1: Rectangle, rect2: Rectangle): Vector2 {
    val intersection = Rectangle()
    if (!Intersector.intersectRectangles(rect1, rect2, intersection)) {
        return Vector2()
    }

    return if (intersection.width < intersection.height) {
        val d = if (rect1.x + rect1.width / 2 < rect2.x + rect2.width / 2) intersection.width else -intersection.width
        Vector2(d, 0f)
    } else {
        val d = if (rect1.y + rect1.height / 2 < rect2.y + rect2.height / 2) intersection.height else -intersection.height
        Vector2(0f, d)
    }
}

private fun penetrationVector(circle: Circle, rectangle: Rectangle): Vector2 {
    val collisionPoint = rectangle.closestPointTo(circle.x, circle.y)
    val cToCollPoint = Vector2(collisionPoint).sub(circle.x, circle.y)

    return if (rectangle.contains(circle.x, circle.y) || cToCollPoint.isZero) {
        val displacement = Vector2(rectangle.x + rectangle.width / 2 - circle.x, rectangle.y + rectangle.height / 2 - circle.y)

        val desiredDisplacement = if (!displacement.isZero) {
            // Calculate penetration in X or Y direction, choosing the smaller one
            val dispx = Vector2(displacement.x, 0f).nor().scl(circle.radius + rectangle.width / 2)
            val dispy = Vector2(0f, displacement.y).nor().scl(circle.radius + rectangle.height / 2)

            if (dispx.len2() < dispy.len2()) {
                displacement.y = 0f
                dispx
            } else {
                displacement.x = 0f
                dispy
            }
        } else {
            Vector2(0f, -circle.radius - rectangle.height / 2)
        }

        displacement.sub(desiredDisplacement)
    } else {
        cToCollPoint.nor().scl(circle.radius).sub(cToCollPoint)
    }
}

private fun penetrationVector(rectangle: Rectangle, circle: Circle): Vector2 {
    return penetrationVector(circle, rectangle).scl(-1f)
}

private fun penetrationVector(compositeShape: CompositeShape, shape: Shape2D): Vector2 {
    var penetrationVector = Vector2()
    compositeShape.shapes.forEach {
        if (it.overlaps(shape)) {
            penetrationVector.add(it.getPenetrationVector(shape))
        }
    }
    return penetrationVector
}

private fun penetrationVector(shape: Shape2D, compositeShape: CompositeShape): Vector2 {
    return penetrationVector(compositeShape, shape).scl(-1f)
}

private fun penetrationVector(compositeShape1: CompositeShape, compositeShape2: CompositeShape): Vector2 {
    var penetrationVector = Vector2()
    compositeShape1.shapes.forEach { shape1 ->
        compositeShape2.shapes.forEach { shape2 ->
            if (shape1.overlaps(shape2)) {
                penetrationVector.add(shape1.getPenetrationVector(shape2))
            }
        }
    }
    return penetrationVector
}

fun Rectangle.getVertices(): List<Vector2> {
    return listOf(
        Vector2(x, y),
        Vector2(x + width, y),
        Vector2(x + width, y + height),
        Vector2(x, y + height)
    )
}

fun Rectangle.closestPointTo(point: Vector2): Vector2 {
    return closestPointTo(point.x, point.y)
}

fun Rectangle.closestPointTo(x: Float, y: Float): Vector2 {
    val closestX = min(max(x, this.x), this.x + this.width)
    val closestY = min(max(y, this.y), this.y + this.height)
    return Vector2(closestX, closestY)
}
