package dev.widebars.commons.extensions

import android.app.Application

fun Application.isRuStoreInstalled(): Boolean {
    return isPackageInstalled("ru.vk.store")
}
