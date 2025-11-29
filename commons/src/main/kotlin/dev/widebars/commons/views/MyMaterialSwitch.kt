package dev.widebars.commons.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.materialswitch.MaterialSwitch
import dev.widebars.commons.R
import dev.widebars.commons.extensions.adjustAlpha
import dev.widebars.commons.extensions.baseConfig
import dev.widebars.commons.extensions.getContrastColor

open class MyMaterialSwitch : MaterialSwitch {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        setShowCheckmark(context.baseConfig.showCheckmarksOnSwitches)
    }

    fun setShowCheckmark(showCheckmark: Boolean) {
        if (showCheckmark) {
            setOnCheckedChangeListener { _, isChecked ->
                setThumbIconDrawable(
                    if (isChecked) {
                        AppCompatResources.getDrawable(context, R.drawable.ic_check_vector)
                    } else {
                        null
                    }
                )
            }
        } else {
            setOnCheckedChangeListener(null)
        }
    }

    fun setColors(textColor: Int, accentColor: Int, backgroundColor: Int) {
        val onPrimary = accentColor.getContrastColor()
        val onBackground = backgroundColor.getContrastColor()
        val thumbColor = onBackground.adjustAlpha(0.6f)
        val trackColor = onBackground.adjustAlpha(0.2f)

        setTextColor(textColor)
        trackTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(trackColor, accentColor)
        )

        thumbTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(thumbColor, onPrimary)
        )

        thumbIconTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(thumbColor, accentColor)
        )

        trackDecorationTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(thumbColor, accentColor)
        )
    }
}
