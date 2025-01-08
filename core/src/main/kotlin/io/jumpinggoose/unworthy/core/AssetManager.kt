package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.Deferred
import ktx.assets.async.AssetStorage

object AssetManager : Disposable {
    val assetStorage = AssetStorage()

    inline fun <reified T> get(path: String): T = assetStorage.get<T>(path)

    suspend inline fun <reified T> load(
        path: String,
        parameters: AssetLoaderParameters<T>? = null
    ): T = assetStorage.load<T>(path, parameters)

    inline fun <reified T> loadSync(
        path: String,
        parameters: AssetLoaderParameters<T>? = null
    ): T = assetStorage.loadSync<T>(path, parameters)

    suspend fun loadTexture(path: String): Texture {
        return assetStorage.load<Texture>(path, textureParameters)
    }

    fun loadTextureSync(path: String): Texture {
        return assetStorage.loadSync<Texture>(path, textureParameters)
    }

    fun loadTextureAsync(path: String): Deferred<Texture> {
        return assetStorage.loadAsync<Texture>(path, textureParameters)
    }

    private val textureParameters = TextureParameter().apply {
        minFilter = TextureFilter.Linear
        magFilter = TextureFilter.Linear
    }

    override fun dispose() {
        assetStorage.dispose()
    }
}
