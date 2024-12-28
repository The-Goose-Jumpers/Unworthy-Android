package io.jumpinggoose.unworthy.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import io.jumpinggoose.unworthy.UnworthyApp
import io.jumpinggoose.unworthy.android.repository.AndroidPlayerDataRepository

class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerDataRepository = AndroidPlayerDataRepository()
        initialize(UnworthyApp(playerDataRepository), AndroidApplicationConfiguration().apply {
            useWakelock = true
            useImmersiveMode = true
            renderUnderCutout = true
            useGyroscope = true
            useAccelerometer = false
            useCompass = false
            useGL30 = true
        })
    }
}
