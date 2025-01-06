package io.jumpinggoose.unworthy.core.animation

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Json
import io.jumpinggoose.unworthy.core.AssetManager
import ktx.assets.toInternalFile
import ktx.json.fromJson

/** Loads a spritesheet created with SpriteFactory.
 *
 * @param assetName The name of the asset to load.
 */
class Spritesheet(assetName: String) : Disposable {
    private val textureRegions: List<TextureRegion>
    val cycles: Map<String, Animation<TextureRegion>>

    init {
        val json = Json()
        val animationData = json.fromJson<AnimationData>(assetName.toInternalFile())
        val texture = AssetManager.loadTextureSync(animationData.textureAtlas.texture)
        val textureRegions = TextureRegion.split(texture, animationData.textureAtlas.regionWidth, animationData.textureAtlas.regionHeight)
        this.textureRegions = textureRegions.flatten()

        val animations = mutableMapOf<String, Animation<TextureRegion>>()
        for ((name, cycle) in animationData.cycles) {
            val keyFrames = Array<TextureRegion>(cycle.frames.size)
            for (frameIndex in cycle.frames) {
                val row = frameIndex / textureRegions[0].size
                val col = frameIndex % textureRegions[0].size
                keyFrames.add(textureRegions[row][col])
            }
            val playMode = when {
                cycle.isLooping && cycle.isReversed -> Animation.PlayMode.LOOP_REVERSED
                cycle.isLooping -> Animation.PlayMode.LOOP
                cycle.isPingPong -> Animation.PlayMode.LOOP_PINGPONG
                cycle.isReversed -> Animation.PlayMode.REVERSED
                else -> Animation.PlayMode.NORMAL
            }
            animations[name] = Animation(cycle.frameDuration, keyFrames, playMode)
        }
        this.cycles = animations.toMap()
    }

    fun getRegion(index: Int): TextureRegion {
        return textureRegions[index]
    }

    override fun dispose() {
        for (region in textureRegions) {
            region.texture.dispose()
        }
    }
}
