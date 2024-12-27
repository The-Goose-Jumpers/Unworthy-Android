package io.jumpinggoose.unworthy.objects.entities

import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameCollideable2D

interface IEntity : IGameCollideable2D {
    val isFriendly: Boolean
    var health: Int

    val isDead: Boolean
        get() = health <= 0

    fun initialize() {}

    fun takeDamage(source: GameObject, damage: Int, impactForce: Float = 0f)
}
