package io.jumpinggoose.unworthy.core

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.Deferred
import ktx.assets.async.AssetStorage
import ktx.freetype.async.loadFreeTypeFont
import ktx.freetype.async.registerFreeTypeFontLoaders

object AssetManager : Disposable {
    val assetStorage = AssetStorage()

    init {
        assetStorage.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)
    }

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

    suspend fun loadFreeTypeFont(path: String, size: Int = 100): BitmapFont {
        return assetStorage.loadFreeTypeFont(path) {
            this.size = size
            minFilter = TextureFilter.MipMapLinearLinear
            magFilter = TextureFilter.Linear
            genMipMaps = true
        }
    }

    private val textureParameters = TextureParameter().apply {
        minFilter = TextureFilter.MipMap
        magFilter = TextureFilter.Linear
        genMipMaps = true
    }

    override fun dispose() {
        assetStorage.dispose()
    }
}
