package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.jumpinggoose.unworthy.core.AssetManager
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.objects.entities.Player

class HPIndicator(id: String, private val player: Player) : GameObject(id), IGameDrawable {
    private val lifeClockSprite = AssetManager.loadTextureSync("UI/life_clock.png")
    private val spacing = 100

    override fun draw(batch: SpriteBatch) {
        for (i in 0 until player.health) {
            batch.draw(
                lifeClockSprite,
                globalPosition.x + i * (lifeClockSprite.width + spacing),
                globalPosition.y - lifeClockSprite.height
            )
        }
    }

    override fun update(delta: Float) {}
}
