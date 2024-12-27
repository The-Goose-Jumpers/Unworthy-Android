package io.jumpinggoose.unworthy.objects.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.core.AnimatedGameObject
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.scenes.Level
import io.jumpinggoose.unworthy.utils.drawCircle
import io.jumpinggoose.unworthy.utils.drawRectangle
import io.jumpinggoose.unworthy.utils.getPenetrationVector
import io.jumpinggoose.unworthy.utils.moveTowards
import io.jumpinggoose.unworthy.utils.nextFloat
import io.jumpinggoose.unworthy.utils.overlaps
import ktx.math.*
import kotlin.random.Random

class Flye(
    position: Vector2
) : AnimatedGameObject("Flye_${s_instanceCount++}", "Enemies/Flye/flye.sf", position), IEntity {

    private val startPosition: Vector2 = position.cpy()

    override val isFriendly: Boolean = false
    override var health: Int = 6

    private val normalSpeed: Float = 2.25f
    private val pursuitSpeed: Float = 2.5f
    private val dashSpeed: Float = 4.0f
    private val patrolArea: Rectangle = Rectangle(position.x - 1920, position.y - 750, 3840f, 1500f)

    private var patrolDestination: Vector2 = Vector2()
    private var isPatrolling: Boolean = false
    private var canPatrol: Boolean = true
    private var isInPursuit: Boolean = false
    private var playerLastKnownPosition: Vector2 = Vector2()
    private var hasReachedPlayerLastKnownPosition: Boolean = true

    private var attackDelay: Float = 2.0f
    private var attackTimer: Float = 0.0f

    private var knockbackTarget: Vector2? = null
    private var knockbackSpeed: Float = 10f
    private var staggerDuration: Float = 0.5f
    private var staggerTimer: Float = 0.0f
    private var colorLerpTimer: Float = 0.0f

    private var isFacingRight: Boolean = true

    private var deltaPosition: Vector2 = Vector2()

    private val random: Random = Random(id.hashCode())

    private lateinit var player: Player

    init {
        layer = Constants.LAYER_ENTITIES
        sprite.setOrigin(299.5f, 196f)
        sprite.setFrameRate(12)
    }

    override fun initialize() {
        player = (root as Level).findGameObjectById("Player") as Player
        playAnimation(Animation.IDLE) {
            canPatrol = true
        }
    }

    override fun reset() {
        position = startPosition
        health = 4
        isPatrolling = false
        canPatrol = true
        isInPursuit = false
        hasReachedPlayerLastKnownPosition = true
        attackTimer = 0.0f
    }

    override fun update(delta: Float) {
        colorLerpTimer += delta
        if (colorLerpTimer > 0.5f) {
            colorLerpTimer = 0.5f
        }
        val colorLerpFactor = colorLerpTimer / 0.5f
        if (sprite.color != Color.WHITE) {
            setColor(sprite.color.cpy().lerp(Color.WHITE, colorLerpFactor))
        } else {
            colorLerpTimer = 0.0f
        }

        if (isDead) {
            super.update(delta)
            return
        }

        knockbackTarget?.let {
            position = position.moveTowards(it, knockbackSpeed * Constants.PIXELS_PER_UNIT * delta)
            if (position.dst(it) < 1.0f) {
                knockbackTarget = null
            }
            super.update(delta)
            return
        }

        if (staggerTimer > 0) {
            staggerTimer -= delta
            super.update(delta)
            return
        }

        if (attackTimer > 0) {
            attackTimer -= delta
        }

        if (collidesWith(player)) {
            player.takeDamage(this, 1)
            // Clamp Flye's position to prevent it from overlapping with the player
            val penetrationVector = bounds.getPenetrationVector(player.bounds)
            position = position + penetrationVector
        }
        val previousPosition = position.cpy()

        checkForPlayer(delta)

        if (isAvailableToPatrol) {
            startPatrol()
        } else if (isPatrolling) {
            if (position.dst(patrolDestination) < 1.0f) {
                isPatrolling = false
                playAnimation(Animation.LOOK_AROUND) {
                    playAnimation(Animation.IDLE) {
                        canPatrol = true
                    }
                }
            } else {
                moveToDestination(patrolDestination, normalSpeed * delta)
            }
        }

        deltaPosition = position - previousPosition

        updateFacingDirection()
        super.update(delta)
    }

    private fun startPatrol() {
        patrolDestination.set(
            random.nextFloat(patrolArea.x, patrolArea.x + patrolArea.width),
            random.nextFloat(patrolArea.y, patrolArea.y + patrolArea.height)
        )
        isPatrolling = true
        canPatrol = false
    }

    private fun checkForPlayer(delta: Float) {
        if (!::player.isInitialized) return
        if (player.isDead) {
            isInPursuit = false
            canPatrol = true
            return
        }

        val playerHitbox = player.bounds

        var isPlayerInSight = sight.overlaps(playerHitbox)
        if (isPlayerInSight) {
            isPatrolling = false
            canPatrol = false
            hasReachedPlayerLastKnownPosition = false
            playerLastKnownPosition.set(player.position)
            if (!isInPursuit) {
                playAnimation(Animation.ALERT) {
                    isInPursuit = true
                }
            } else {
                if (attackTimer <= 0f && attackRange.overlaps(playerHitbox)) {
                    attack(player.position, delta)
                } else {
                    moveToDestination(player.position, pursuitSpeed * delta)
                }
            }
            return
        }

        if (!hasReachedPlayerLastKnownPosition) {
            isPatrolling = false
            isInPursuit = true
            moveToDestination(playerLastKnownPosition, pursuitSpeed * delta)
            isPlayerInSight = sight.overlaps(playerHitbox)
            if (isPlayerInSight) {
                hasReachedPlayerLastKnownPosition = false
                playerLastKnownPosition.set(player.position)
            } else if (position.dst(playerLastKnownPosition) < 1.0f) {
                hasReachedPlayerLastKnownPosition = true
            }
        }

        if (isInPursuit && !isPlayerInSight) {
            isInPursuit = false
            playAnimation(Animation.LOOK_AROUND) {
                canPatrol = true
            }
        }
    }

    private fun moveToDestination(destination: Vector2, speed: Float) {
        position = position.moveTowards(destination, speed * Constants.PIXELS_PER_UNIT)
        if (sprite.currentAnimation != Animation.MOVE) playAnimation(Animation.MOVE)
    }

    private fun attack(targetPosition: Vector2, delta: Float) {
        position = position.moveTowards(targetPosition, dashSpeed * Constants.PIXELS_PER_UNIT * delta)
        playAnimation(Animation.ATTACK) {
            attackTimer = attackDelay
        }
    }

    private fun updateFacingDirection() {
        if (isFacingRight && deltaPosition.x < 0f || !isFacingRight && deltaPosition.x > 0f) {
            isFacingRight = !isFacingRight
            sprite.isFlippedX = !isFacingRight
            sprite.setOrigin(sprite.width - sprite.originX, sprite.originY)
        }
    }

    override fun takeDamage(source: GameObject, damage: Int, impactForce: Float) {
        if (isDead) return
        health -= damage
        sprite.color = Color.RED
        if (isDead) {
            playAnimation(Animation.DEATH)
            return
        }
        val knockbackDirection = (position - source.position).nor()
        knockbackTarget = position + knockbackDirection * impactForce * Constants.PIXELS_PER_UNIT
        staggerTimer = staggerDuration
        playAnimation(Animation.IDLE)
    }

    private val isAvailableToPatrol: Boolean
        get() = !isPatrolling && !isInPursuit && canPatrol

    override fun drawDebug(batch: SpriteBatch) {
        if (isDead) return
        batch.drawCircle(bounds.x, bounds.y, bounds.radius, Color.RED, 3f)
        batch.drawCircle(sight.x, sight.y, sight.radius, Color.YELLOW, 3f)
        batch.drawCircle(attackRange.x, attackRange.y, attackRange.radius, Color.GREEN, 3f)
        batch.drawRectangle(patrolArea, Color.BLUE, 3f)
    }

    private val sight: Circle
        get() = Circle(
            globalPosition.x + if (isFacingRight) 200 else -200,
            globalPosition.y,
            if (isPatrolling) 700f else 1000f
        )

    private val attackRange: Circle
        get() = Circle(globalPosition.x, globalPosition.y, 600f)

    override val bounds: Circle
        get() = Circle(globalPosition.x, globalPosition.y, 160f)

    companion object {
        private var s_instanceCount = 0

        private object Animation {
            const val IDLE = "idle"
            const val MOVE = "move"
            const val ATTACK = "attack"
            const val ALERT = "alert"
            const val LOOK_AROUND = "patrol"
            const val DEATH = "death"
        }
    }
}
