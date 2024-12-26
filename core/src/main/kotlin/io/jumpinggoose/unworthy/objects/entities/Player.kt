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
import io.jumpinggoose.unworthy.utils.setPosition
import ktx.math.*

class Player(private val level: Level, position: Vector2)
    : AnimatedGameObject("Player", "Characters/Player/player.sf", position), IEntity {
    override val isFriendly: Boolean = true
    override var health: Int = 5

    private val speed: Float = 2.5f
    private val jumpForce: Float = 6.2f
    private val maxFallSpeed: Float = 12f
    private val gravityScale: Float = 1.5f
    private var velocity: Vector2 = Vector2()

    private var horizontalMovement: Float = 1f
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
    private var attackEffectOffset: Vector2 = Vector2(130f, -105f)

    private var invincibilityDuration: Float = 1.5f
    private var invincibilityTimer: Float = 0f

    private var idleAnimationTimer: Float = 0f

    private val startPosition: Vector2 = position.cpy()
    private val random = kotlin.random.Random(id.hashCode())

    init {
        layer = Constants.LAYER_ENTITIES
        sprite.setOrigin(131.84f, 232.5f)
        sprite.setFrameRate(12)
        attackSprite = AnimatedSprite("Characters/Player/attack_effects.sf", framesPerSecond = 12)
    }

    fun initialize() {
        level.cameraController.offset = Vector2(if (isFacingRight) 1f else -1f, 0f)
        level.cameraController.setFollowTarget(this)
    }

    // fun handleInput(inputHelper: InputHelper) {
    //     pressedAttack = false
    //     if (isGrounded) {
    //         if (inputHelper.keyPressed(Keys.F) || inputHelper.mouseLeftButtonPressed() || inputHelper.gamePadButtonPressed(Buttons.X)) {
    //             pressedAttack = true
    //         } else if (inputHelper.keyPressed(Keys.SPACE) || inputHelper.gamePadButtonPressed(Buttons.A)) {
    //             velocity.sub(Vector2.Y * jumpForce)
    //         }
    //     }
    //     horizontalMovement = 0f
    //     if (inputHelper.isKeyDown(Keys.A) || inputHelper.isKeyDown(Keys.LEFT) || inputHelper.isGamePadButtonDown(Buttons.DPAD_LEFT)) {
    //         horizontalMovement -= 1f
    //     }
    //     if (inputHelper.isKeyDown(Keys.D) || inputHelper.isKeyDown(Keys.RIGHT) || inputHelper.isGamePadButtonDown(Buttons.DPAD_RIGHT)) {
    //         horizontalMovement += 1f
    //     }
    //     if (horizontalMovement == 0f && kotlin.math.abs(inputHelper.getGamePadHorizontalAxis()) > 0.05f) {
    //         horizontalMovement = inputHelper.getGamePadHorizontalAxis()
    //     }
    // }

    override fun update(delta: Float) {
        if (isDead) {
            println("Player is dead, skipping update")
            super.update(delta)
            return
        }

        if (invincibilityTimer > 0) invincibilityTimer -= delta
        if (attackCooldownTimer > 0) attackCooldownTimer -= delta
        if (attackComboTimer > 0) attackComboTimer -= delta else lastAttackAnimation = ""

        val previousPosition = position.cpy()

        // Check if player is grounded
        var playerHitbox = getBoundingRectangle(position)
        val playerBottom = Rectangle(playerHitbox.x, playerHitbox.y + playerHitbox.height, playerHitbox.width, 3f)
        isGrounded = level.terrain.collidesWith(playerBottom)

        // Apply gravity
        if (!isGrounded) {
            println("Not grounded, applying gravity")
            velocity.add(-Vector2.Y * Constants.GRAVITY * gravityScale * delta)
        } else if (velocity.y > 0) {
            println("Grounded, resetting vertical velocity")
            velocity.y = 0f
        }

        // Apply horizontal movement and clamp vertical speed
        velocity.set(
            if (!pressedAttack && !isAttacking) horizontalMovement * speed else 0f,
            MathUtils.clamp(velocity.y, -maxFallSpeed, jumpForce)
        )

        position = position + (velocity * Constants.PIXELS_PER_UNIT * delta)

        if (this.collidesWith(level.killTriggers)) {
            level.restart()
            return
        }

        var penetrationVector = level.terrain.getPenetrationVector(this)
        if (!penetrationVector.isZero) {
            println("Terrain collision detected, applying penetration vector: $penetrationVector")
            position = position + penetrationVector
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
            println("Level boundary collision detected, applying penetration vector: $penetrationVector")
            position = position + penetrationVector
            velocity.set(if (penetrationVector.x == 0f) velocity.x else 0f, if (penetrationVector.y == 0f) velocity.y else 0f)
        }

        deltaPosition = position - previousPosition

        if (pressedAttack) performAttack()
        attackSprite.setPosition(globalPosition + attackEffectOffset)
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
                enemy.takeDamage(this, 1, 2f)
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
        sprite.setFlip(!isFacingRight, false)
        attackSprite.setFlip(!isFacingRight, false)
        attackEffectOffset.x = -attackEffectOffset.x
        sprite.setOrigin(sprite.width - sprite.originX, sprite.originY)
    }

    private fun updateAnimation(delta: Float) {
        val isMovingHorizontally = kotlin.math.abs(deltaPosition.x) > 0.01f
        val isMovingVertically = kotlin.math.abs(deltaPosition.y) > 0.01f
        val currentAnimationName = sprite.currentAnimation ?: ""

        if (pressedAttack) {
            val nextAnimation = when {
                sprite.currentAnimation == Animation.ATTACK_1 && sprite.isComplete == true -> Animation.ATTACK_2
                sprite.currentAnimation == Animation.ATTACK_2 && sprite.isComplete == true -> Animation.ATTACK_1
                else -> if (lastAttackAnimation == Animation.ATTACK_1) Animation.ATTACK_2 else Animation.ATTACK_1
            }
            lastAttackAnimation = nextAnimation
            playAnimation(nextAnimation) { isAttacking = false }
            return
        }

        if (sprite.currentAnimation != null) {
            when (currentAnimationName) {
                Animation.ATTACK_1, Animation.ATTACK_2 -> if (sprite.isComplete == true) {
                    playAnimation(Animation.ATTACK_END)
                    return
                }
            }
        }

        if (isAttacking) return

        if (isMovingVertically && !isGrounded) {
            if (deltaPosition.y < 0) {
                if (sprite.currentAnimation != Animation.JUMP) playAnimation(Animation.JUMP)
            } else {
                if (sprite.currentAnimation != Animation.FALL) playAnimation(Animation.FALL)
            }
            return
        }

        if (isMovingHorizontally) {
            if (!isGrounded) return
            when (currentAnimationName) {
                Animation.IDLE, Animation.FALL, Animation.ATTACK_END -> playAnimation(Animation.WALK_START)
                Animation.WALK_START -> if (sprite.isComplete == true) playAnimation(Animation.WALK_LOOP)
                Animation.WALK_END -> playAnimation(if (sprite.isComplete == true) Animation.WALK_START else Animation.WALK_LOOP)
                else -> playAnimation(Animation.WALK_LOOP)
            }
            return
        }

        when (currentAnimationName) {
            Animation.WALK_LOOP-> playAnimation(Animation.WALK_END)
            Animation.WALK_START -> if (sprite.isComplete == true) playIdleAnimation(delta) else playAnimation(Animation.WALK_END)
            Animation.FALL -> if (isGrounded) playAnimation(Animation.LAND)
            else -> if (sprite.isComplete == true) playIdleAnimation(delta)
        }
    }

    private fun playIdleAnimation(delta: Float) {
        idleAnimationTimer -= delta
        if (idleAnimationTimer <= 0) {
            playAnimation(Animation.IDLE)
            idleAnimationTimer = random.nextFloat() * (7.5f - 3f) + 3f
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
        if (health <= 0) {
            playAnimation(Animation.DEATH) { level.restart() }
        } else {
              level.flashVignetteRed()
            playAnimation(Animation.HURT)
        }
        invincibilityTimer = invincibilityDuration
        Gdx.input.vibrate(VibrationType.LIGHT)
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

    fun drawDebug(batch: SpriteBatch) {
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
