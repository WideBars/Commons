package dev.widebars.commons.activities

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import dev.widebars.commons.R
import dev.widebars.commons.extensions.baseConfig
import dev.widebars.commons.extensions.isAutoTheme
import dev.widebars.commons.extensions.isSystemInDarkMode
import dev.widebars.commons.extensions.syncGlobalConfig
import dev.widebars.commons.helpers.MyContextWrapper
import dev.widebars.commons.helpers.REQUEST_APP_UNLOCK
import dev.widebars.commons.helpers.isTiramisuPlus

abstract class BaseComposeActivity : ComponentActivity() {

    override fun onResume() {
        super.onResume()
        maybeLaunchAppUnlockActivity(REQUEST_APP_UNLOCK)
    }

    override fun attachBaseContext(newBase: Context) {
        if (newBase.baseConfig.useEnglish && !isTiramisuPlus()) {
            super.attachBaseContext(MyContextWrapper(newBase).wrap(newBase, "en"))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeAutoTheme()
    }

    private fun changeAutoTheme() {
        if (isDestroyed || isFinishing) return
        syncGlobalConfig {
            if (isDestroyed || isFinishing) return@syncGlobalConfig
            baseConfig.apply {
                if (isAutoTheme()) {
                    runOnUiThread {
                        if (isDestroyed || isFinishing) return@runOnUiThread
                        val isUsingSystemDarkTheme = isSystemInDarkMode()
                        textColor =
                            resources.getColor(if (isUsingSystemDarkTheme) R.color.theme_black_text_color else R.color.theme_light_text_color)
                        backgroundColor =
                            resources.getColor(if (isUsingSystemDarkTheme) R.color.theme_black_background_color else R.color.theme_light_background_color)
                    }
                }
            }
        }
    }
}
