package io.jumpinggoose.unworthy.objects.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.VibrationType
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.core.AnimatedGameObject
import io.jumpinggoose.unworthy.core.AnimatedSprite
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.scenes.Level
import io.jumpinggoose.unworthy.utils.drawCircle
import io.jumpinggoose.unworthy.utils.drawRectangle
import io.jumpinggoose.unworthy.utils.fillRectangle
import io.jumpinggoose.unworthy.utils.getBoundingRectangle
import io.jumpinggoose.unworthy.utils.getCorners
import io.jumpinggoose.unworthy.utils.getPenetrationVector
import io.jumpinggoose.unworthy.utils.nextFloat
import io.jumpinggoose.unworthy.utils.setOriginBasedPosition
import ktx.math.*
import kotlin.math.abs
import kotlin.random.Random

class Player(
    private val level: Level,
    position: Vector2
) : AnimatedGameObject("Player", "Characters/Player/player.sf", position), IEntity {
    override val isFriendly: Boolean = true
    override var health: Int = 5

    private val speed: Float = 2.5f
    private val speedWhileAttacking: Float = 0.5f
    private val jumpForce: Float = 6.2f
    private val maxFallSpeed: Float = 12f
    private val gravityScale: Float = 1.5f
    private var velocity: Vector2 = Vector2()

    private var horizontalMovement: Float = 0f
    private var isGrounded: Boolean = false
    private var isFacingRight: Boolean = true
    private var pressedAttack: Boolean = false
    private var deltaPosition: Vector2 = Vector2()

    private var isAttacking: Boolean = false
    private var lastAttackAnimation: String = ""
    private var attackComboTimeframe: Float = 1f
    private var attackComboTimer: Float = 0f
    private var attackCooldown: Float = 0.2f
    private var attackCooldownTimer: Float = 0f
    private val attackSprite: AnimatedSprite
    private var attackEffectOffset: Vector2 = Vector2(130f, 105f)

    private var invincibilityDuration: Float = 1.5f
    private var invincibilityTimer: Float = 0f

    private var idleAnimationTimer: Float = 0f

    private val startPosition: Vector2 = position.cpy()
    private val random = Random(id.hashCode())

    var deathCount: Int = 0
    var killCount: Int = 0

    init {
        layer = Constants.LAYER_ENTITIES
        sprite.setOrigin(131.84f, 152f)
        sprite.setFrameRate(12)
        attackSprite = AnimatedSprite("Characters/Player/attack_effects.sf", framesPerSecond = 12)
    }

    override fun initialize() {
        level.cameraController.offset = Vector2(if (isFacingRight) 1f else -1f, 0f)
        level.cameraController.setFollowTarget(this)
    }

    fun handleInput() {
        pressedAttack = false
        if (isGrounded) {
            if (level.attackButton.wasTouched) {
                pressedAttack = true
            } else if (level.jumpButton.wasTouched) {
                velocity.add(Vector2.Y * jumpForce)
            }
        }
        horizontalMovement = 0f
        val horizontalAxisInput = level.analogControl.horizontalAxis
        if (abs(horizontalAxisInput) > 0.05f) {
            horizontalMovement = horizontalAxisInput
        }
    }

    override fun update(delta: Float) {
        if (isDead) {
            super.update(delta)
            return
        }
        handleInput()

        if (invincibilityTimer > 0) invincibilityTimer -= delta
        if (attackCooldownTimer > 0) attackCooldownTimer -= delta
        if (attackComboTimer > 0) attackComboTimer -= delta else lastAttackAnimation = ""

        val previousPosition = position.cpy()

        // Check if player is grounded
        var playerHitbox = getBoundingRectangle(position)
        val playerBottom = Rectangle(playerHitbox.x, playerHitbox.y - 3f, playerHitbox.width, 3f)
        isGrounded = level.terrain.collidesWith(playerBottom)

        // Apply gravity
        if (!isGrounded) {
            Gdx.app.debug("Player", "Not grounded, applying gravity")
            velocity.add(-Vector2.Y * Constants.GRAVITY * gravityScale * delta)
        } else if (velocity.y < 0) {
            Gdx.app.debug("Player", "Player is grounded but falling, resetting vertical velocity")
            velocity.y = 0f
        }

        // Apply horizontal movement and clamp vertical speed
        velocity.set(
            horizontalMovement * (if (pressedAttack || isAttacking) speedWhileAttacking else speed),
            MathUtils.clamp(velocity.y, -maxFallSpeed, jumpForce)
        )

        position = position + (velocity * Constants.PIXELS_PER_UNIT * delta)

        if (this.collidesWith(level.killTriggers)) {
            level.flashVignetteRed()
            deathCount++
            health = 0
            level.restart()
            return
        }

        var penetrationVector = level.terrain.getPenetrationVector(this)
        // If there is a collision, resolve it
        if (!penetrationVector.isZero) {
            Gdx.app.debug("Player", "Terrain collision detected, applying penetration vector: $penetrationVector")
            position = position + penetrationVector
            // Kill velocity in the direction of the collision
            velocity.set(
                if (penetrationVector.x == 0f) velocity.x else 0f,
                if (penetrationVector.y == 0f) velocity.y else 0f
            )
        }

        // Prevent player from going out of bounds
        playerHitbox = getBoundingRectangle(position)
        position = Vector2(
            MathUtils.clamp(position.x, playerHitbox.width / 2, level.width - playerHitbox.width / 2),
            MathUtils.clamp(position.y, -level.height, level.height - playerHitbox.height / 2)
        )
        playerHitbox = getBoundingRectangle(position)
        penetrationVector = level.boundaries.getPenetrationVector(playerHitbox)
        if (!penetrationVector.isZero) {
            Gdx.app.debug("Player", "Level boundary collision detected, applying penetration vector: $penetrationVector")
            position = position + penetrationVector
            velocity.set(
                if (penetrationVector.x == 0f) velocity.x else 0f,
                if (penetrationVector.y == 0f) velocity.y else 0f
            )
        }

        deltaPosition = position - previousPosition

        if (pressedAttack) performAttack()
        attackSprite.setOriginBasedPosition(globalPosition + attackEffectOffset)
        attackSprite.update(delta)

        updateFacingDirection()
        updateAnimation(delta)
        updateCamera(delta)

        super.update(delta)
    }

    private fun performAttack() {
        if (attackCooldownTimer > 0) {
            pressedAttack = false
            return
        }
        attackComboTimer = attackComboTimeframe
        attackCooldownTimer = attackCooldown
        isAttacking = true
        attackSprite.setTextureRegion(3)
        attackSprite.play("attack")
        level.enemies.forEach { enemy ->
            if (!enemy.isDead && enemy.collidesWith(attackBounds)) {
                enemy.takeDamage(this, 1, 0.75f)
            }
        }
    }

    private val attackBounds: Rectangle
        get() = attackSprite.getBoundingRectangle(globalPosition + attackEffectOffset, Vector2(0.75f, 0.75f))

    private fun updateFacingDirection() {
        if (isFacingRight && horizontalMovement < 0f || !isFacingRight && horizontalMovement > 0f) {
            setFacingDirection(!isFacingRight)
        }
    }

    private fun setFacingDirection(isFacingRight: Boolean) {
        if (this.isFacingRight == isFacingRight) return
        this.isFacingRight = isFacingRight
        sprite.isFlippedX = !isFacingRight
        attackSprite.isFlippedX = !isFacingRight
        attackEffectOffset.x = -attackEffectOffset.x
        sprite.setOrigin(sprite.width - sprite.originX, sprite.originY)
    }

    private fun updateAnimation(delta: Float) {
        val isMovingHorizontally = abs(deltaPosition.x) > 0.01f
        val isMovingVertically = abs(deltaPosition.y) > 0.01f

        if (pressedAttack) {
            val nextAnimation = when {
                sprite.currentAnimation == Animation.ATTACK_1 && sprite.isComplete -> Animation.ATTACK_2
                sprite.currentAnimation == Animation.ATTACK_2 && sprite.isComplete -> Animation.ATTACK_1
                else -> if (lastAttackAnimation == Animation.ATTACK_1) Animation.ATTACK_2 else Animation.ATTACK_1
            }
            lastAttackAnimation = nextAnimation
            playAnimation(nextAnimation) { isAttacking = false }
            return
        }

        if (sprite.currentAnimation != null) {
            when (sprite.currentAnimation) {
                Animation.ATTACK_1, Animation.ATTACK_2 -> if (sprite.isComplete) {
                    playAnimation(Animation.ATTACK_END)
                    return
                }
            }
        }

        if (isAttacking) return

        if (isMovingVertically && !isGrounded) {
            // These animations should only play if the player is not grounded.
            // e.g. if the player is in a moving (vertically) platform, this would be detected as vertical movement,
            // but we don't want to play the jump/fall animations in this case.
            // We don't want to play the same animation again even if it has already completed because these animations
            // aren't designed to loop. Instead, the sprite must remain in the last frame of the animation for as long
            // as the player is still moving vertically.
            if (deltaPosition.y > 0) {
                if (sprite.currentAnimation != Animation.JUMP) playAnimation(Animation.JUMP)
            } else {
                if (sprite.currentAnimation != Animation.FALL) playAnimation(Animation.FALL)
            }
            // We return here because the jump/fall animations are higher priority than the walking and idle animations.
            // e.g. we don't want to play the walk animation while the player is falling.
            return
        }

        if (isMovingHorizontally) {
            if (!isGrounded) return
            when (sprite.currentAnimation) {
                null, Animation.IDLE, Animation.FALL, Animation.ATTACK_END -> playAnimation(Animation.WALK_START)
                Animation.WALK_START -> if (sprite.isComplete) playAnimation(Animation.WALK_LOOP)
                Animation.WALK_END -> playAnimation(if (sprite.isComplete) Animation.WALK_START else Animation.WALK_LOOP)
                else -> playAnimation(Animation.WALK_LOOP)
            }
            return
        }

        when (sprite.currentAnimation) {
            null -> playIdleAnimation(delta)
            Animation.WALK_LOOP-> playAnimation(Animation.WALK_END)
            Animation.WALK_START -> if (sprite.isComplete) playIdleAnimation(delta) else playAnimation(Animation.WALK_END)
            Animation.FALL -> if (isGrounded) playAnimation(Animation.LAND)
            else -> if (sprite.isComplete) playIdleAnimation(delta)
        }
    }

    private fun playIdleAnimation(delta: Float) {
        idleAnimationTimer -= delta
        if (idleAnimationTimer <= 0) {
            playAnimation(Animation.IDLE)
            idleAnimationTimer = random.nextFloat(3f, 7.5f)
        } else {
            changeToStandingSprite()
        }
    }

    private fun changeToStandingSprite() {
        sprite.setTextureRegion(0)
    }

    private fun updateCamera(delta: Float) {
        level.cameraController.offset.lerp(
            Vector2(if (isFacingRight) 1f else -1f, 0f),
            (if (isAttacking) 1.5f else 3.0f) * delta
        )
        level.cameraController.update(delta)
    }

    override fun takeDamage(source: GameObject, damage: Int, impactForce: Float) {
        if (invincibilityTimer > 0) return
        health -= damage
        if (isDead) {
            deathCount++
            playAnimation(Animation.DEATH) {
                level.restart()
            }
        } else {
            level.flashVignetteRed()
            playAnimation(Animation.HURT)
        }
        invincibilityTimer = invincibilityDuration
        Gdx.input.vibrate(150, 75, true)
    }

    override fun reset() {
        position = startPosition
        health = 5
        invincibilityTimer = 0f
        attackCooldownTimer = 0f
        attackComboTimer = 0f
        isAttacking = false
        pressedAttack = false
        horizontalMovement = 0f
        isGrounded = false
        setFacingDirection(true)
        lastAttackAnimation = ""
        level.cameraController.offset = Vector2(if (isFacingRight) 1f else -1f, 0f)
        level.cameraController.setFollowTarget(this)
    }

    override fun draw(batch: SpriteBatch) {
        super.draw(batch)
        if (isAttacking && !isDead) {
            attackSprite.draw(batch)
        }
    }

    override fun drawDebug(batch: SpriteBatch) {
        if (isAttacking && !isDead) {
            batch.drawRectangle(attackBounds, Color.RED, 3f)
        }

        val playerHitbox = getBoundingRectangle(position)
        batch.drawRectangle(playerHitbox, Color.LIME, 3f)
        batch.drawRectangle(sprite.getBoundingRectangle(position), Color.CYAN, 3f)
        batch.drawCircle(position, 5f, Color.CYAN, 3f)

        val playerBottom = Rectangle(playerHitbox.x, playerHitbox.y, playerHitbox.width, 3f)
        batch.fillRectangle(playerBottom, Color.RED)
    }

    private fun getBoundingRectangle(position: Vector2): Rectangle {
        val scale = if (isFacingRight) Vector2(1f, 1f) else Vector2(-1f, 1f)
        val corners = sprite.getCorners(position, scale)
        val min = Vector2(corners.minOf { it.x }, corners.minOf { it.y })
        return Rectangle(min.x + 93.09f, min.y, 77.55f, 305f)
    }

    override val bounds: Rectangle
        get() = getBoundingRectangle(globalPosition)

    private object Animation {
        const val IDLE = "idle"
        const val WALK_START = "walkStart"
        const val WALK_LOOP = "walkLoop"
        const val WALK_END = "walkEnd"
        const val JUMP = "jump"
        const val FALL = "fall"
        const val LAND = "land"
        const val ATTACK_1 = "attack1"
        const val ATTACK_2 = "attack2"
        const val ATTACK_END = "attackEnd"
        const val HURT = "hurt"
        const val DEATH = "death"
    }
}
