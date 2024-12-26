package io.jumpinggoose.unworthy.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import io.jumpinggoose.unworthy.UnworthyApp

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(UnworthyApp(), AndroidApplicationConfiguration().apply {
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
