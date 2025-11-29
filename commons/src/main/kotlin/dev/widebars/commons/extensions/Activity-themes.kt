package dev.widebars.commons.extensions

import android.app.Activity
import dev.widebars.commons.R

fun Activity.getThemeId() = when {
    isDynamicTheme() -> if (isSystemInDarkMode()) R.style.AppTheme_Base_System else R.style.AppTheme_Base_System_Light
    else -> R.style.AppTheme_Blue_600_core
}
