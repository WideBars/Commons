package dev.widebars.commons.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.widebars.commons.extensions.syncGlobalConfig
import dev.widebars.commons.helpers.MyContentProvider

class RightBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == MyContentProvider.ACTION_GLOBAL_CONFIG_UPDATED) {
            context?.syncGlobalConfig()
        }
    }
}
