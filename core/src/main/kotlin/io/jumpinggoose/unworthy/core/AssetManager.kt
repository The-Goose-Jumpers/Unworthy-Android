package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.utils.Disposable
import ktx.assets.async.AssetStorage

object AssetManager : Disposable {
    val assetStorage = AssetStorage()

    suspend inline fun <reified T> load(
        path: String,
        parameters: AssetLoaderParameters<T>? = null
    ): T = assetStorage.load<T>(path, parameters)

    inline fun <reified T> loadSync(
        path: String,
        parameters: AssetLoaderParameters<T>? = null
    ): T = assetStorage.loadSync<T>(path, parameters)

    fun loadTextureSync(path: String): Texture {
        return assetStorage.loadSync<Texture>(path, TextureParameter().apply {
            minFilter = TextureFilter.Linear
            magFilter = TextureFilter.Linear
        })
    }

    override fun dispose() {
        assetStorage.dispose()
    }
}
