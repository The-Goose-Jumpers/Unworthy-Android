package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.jumpinggoose.unworthy.core.animation.Spritesheet

class AnimatedSprite(
    private val spritesheet: Spritesheet,
    playAnimation: String? = null,
    framesPerSecond: Int? = null
) : Sprite(spritesheet.getRegion(0)) {

    constructor(assetName: String, playAnimation: String? = null, framesPerSecond: Int? = null)
        : this(Spritesheet(assetName), playAnimation, framesPerSecond)

    private val animations: Map<String, Animation<TextureRegion>> = spritesheet.cycles

    private var isPaused: Boolean = false
    val isComplete: Boolean
        get() = _currentAnimation?.isAnimationFinished(stateTime) == true

    private var frameDuration: Float? = framesPerSecond?.let { 1f / it }
    private var stateTime: Float = 0f

    var currentAnimation: String? = null
        private set(value) {
            field = value
            if (value != null) {
                stateTime = 0f
                if (frameDuration != null) {
                    _currentAnimation?.frameDuration = frameDuration!!
                }
            }
        }

    private val _currentAnimation: Animation<TextureRegion>?
        get() = animations[currentAnimation]

    private var onCompleted: (() -> Unit)? = null

    init {
        playAnimation?.let { play(it) }
    }

    fun play(name: String, onCompleted: (() -> Unit)? = null) {
        if (currentAnimation == name) {
            val animationPlayMode = _currentAnimation!!.playMode
            if ((animationPlayMode == Animation.PlayMode.NORMAL || animationPlayMode == Animation.PlayMode.REVERSED) && isComplete) {
                stateTime = 0f
                this.onCompleted = onCompleted
            }
        } else {
            if (name in animations) {
                currentAnimation = name
                this.onCompleted = onCompleted
            }
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun stop() {
        isPaused = true
        stateTime = 0f
    }

    var isFlippedX: Boolean = false
        set (value) {
            field = value
            setFlip(value, isFlippedY)
        }

    var isFlippedY: Boolean = false
        set (value) {
            field = value
            setFlip(isFlippedX, value)
        }

    fun setFrame(frame: TextureRegion) {
        setRegion(frame)
        setFlip(isFlippedX, isFlippedY)
    }

    fun setTextureRegion(regionIndex: Int) {
        currentAnimation = null
        setFrame(spritesheet.getRegion(regionIndex))
    }

    fun setFrameRate(framesPerSecond: Int) {
        frameDuration = 1f / framesPerSecond
        _currentAnimation?.frameDuration = frameDuration!!
    }

    val currentFrame: TextureRegion?
        get() = _currentAnimation?.getKeyFrame(stateTime)

    fun update(delta: Float) {
        if (_currentAnimation == null) return
        if (isPaused) return

        stateTime += delta
        setFrame(currentFrame!!)

        if (isComplete) {
            val onCompleted = this.onCompleted ?: return
            this.onCompleted = null
            onCompleted()
        }
    }
}
