package dev.widebars.commons.extensions

import android.content.Context
import dev.widebars.commons.models.FileDirItem

fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}
