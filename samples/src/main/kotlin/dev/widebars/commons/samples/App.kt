package dev.widebars.commons.samples

import com.github.ajalt.reprint.core.Reprint
import dev.widebars.commons.RightApp
import dev.widebars.commons.helpers.PurchaseHelper

class App : RightApp() {
    override fun onCreate() {
        super.onCreate()
        Reprint.initialize(this)
        PurchaseHelper().initPurchaseIfNeed(this, "")
    }
}
