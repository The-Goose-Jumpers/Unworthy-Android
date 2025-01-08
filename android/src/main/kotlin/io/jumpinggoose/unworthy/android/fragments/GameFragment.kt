package io.jumpinggoose.unworthy.android.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import io.jumpinggoose.unworthy.UnworthyApp
import io.jumpinggoose.unworthy.android.repository.AndroidPlayerDataRepository

class GameFragment : AndroidFragmentApplication() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        askNotificationPermission()

        val playerDataRepository = AndroidPlayerDataRepository()

        return initializeForView(UnworthyApp(playerDataRepository), AndroidApplicationConfiguration().apply {
            useWakelock = true
            useImmersiveMode = true
            renderUnderCutout = true
            useGyroscope = true
            useAccelerometer = false
            useCompass = false
            useGL30 = true
        })
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
