package ru.tech.imageresizershrinker.presentation

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.adjustFontSize
import ru.tech.imageresizershrinker.core.ui.widget.activity.SettingsStateEntryPoint
import ru.tech.imageresizershrinker.presentation.crash_screen.CrashActivity

@AndroidEntryPoint
open class M3Activity : AppCompatActivity() {

    private lateinit var settingsState: SettingsState

    override fun attachBaseContext(newBase: Context) {
        settingsState = runBlocking {
            EntryPointAccessors.fromApplication(newBase, SettingsStateEntryPoint::class.java)
                .getSettingsStateUseCase()
        }
        val newOverride = Configuration(newBase.resources?.configuration)
        settingsState.fontScale?.let { newOverride.fontScale = it }
        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        adjustFontSize(settingsState.fontScale)
        enableEdgeToEdge()
        GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
        GlobalExceptionHandler.initialize(
            applicationContext = applicationContext,
            activityToBeLaunched = CrashActivity::class.java,
        )
    }

    override fun recreate() {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            kotlinx.coroutines.delay(10L)
            super.recreate()
        }
    }

}