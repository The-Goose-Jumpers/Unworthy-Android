package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.utils.getPenetrationVector
import io.jumpinggoose.unworthy.utils.overlaps

interface IGameCollideable2D {
    val bounds: Shape2D

    fun collidesWith(other: Shape2D): Boolean {
        return bounds.overlaps(other)
    }

    fun collidesWith(other: IGameCollideable2D): Boolean {
        return bounds.overlaps(other.bounds)
    }

    fun getPenetrationVector(other: Shape2D): Vector2 {
        return bounds.getPenetrationVector(other)
    }

    fun getPenetrationVector(other: IGameCollideable2D): Vector2 {
        return bounds.getPenetrationVector(other.bounds)
    }
}
