package io.jumpinggoose.unworthy.objects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.jumpinggoose.unworthy.core.GameObject
import io.jumpinggoose.unworthy.core.IGameDrawable
import io.jumpinggoose.unworthy.objects.entities.Player
import ktx.assets.toInternalFile

class HPIndicator(id: String, private val player: Player) : GameObject(id), IGameDrawable {
    private val lifeClockSprite = Texture("UI/life_clock.png".toInternalFile()).apply { setFilter(Linear, Linear) }
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
