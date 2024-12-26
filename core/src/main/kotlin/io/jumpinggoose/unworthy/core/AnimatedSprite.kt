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

    fun play(name: String, onCompleted: (() -> Unit)? = null): Animation<TextureRegion>? {
        if (currentAnimation == null || currentAnimation != name || isComplete) {
            if (name in animations) {
                currentAnimation = name
                this.onCompleted = onCompleted
            }
        }
        return _currentAnimation
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

    fun setTextureRegion(regionIndex: Int) {
        currentAnimation = null
        setRegion(spritesheet.getRegion(regionIndex))
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
        setRegion(currentFrame)

        if (isComplete) return
        onCompleted?.invoke()
        stateTime = when(_currentAnimation!!.playMode) {
            Animation.PlayMode.LOOP,
            Animation.PlayMode.LOOP_REVERSED -> stateTime - _currentAnimation!!.animationDuration
            else -> stateTime
        }
        stateTime = stateTime.coerceAtLeast(0f)
    }
}
