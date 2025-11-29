package dev.widebars.commons.compose.extensions

import android.app.Activity
import android.content.Context
import dev.widebars.commons.R
import dev.widebars.commons.extensions.baseConfig
import dev.widebars.commons.extensions.launchAppRatingPage
import dev.widebars.commons.extensions.toast
import dev.widebars.commons.helpers.BaseConfig

val Context.config: BaseConfig get() = BaseConfig.newInstance(applicationContext)

fun Activity.rateStarsRedirectAndThankYou(stars: Int) {
    if (stars == 5) {
        launchAppRatingPage()
    }
    toast(R.string.thank_you)
    baseConfig.wasAppRated = true
}
