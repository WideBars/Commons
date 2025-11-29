package dev.widebars.commons.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.widebars.commons.compose.extensions.config
import dev.widebars.commons.compose.extensions.enableEdgeToEdgeSimple
import dev.widebars.commons.compose.screens.FAQScreen
import dev.widebars.commons.compose.theme.AppThemeSurface
import dev.widebars.commons.extensions.*
import dev.widebars.commons.helpers.APP_FAQ
import dev.widebars.commons.models.FAQItem
import kotlinx.collections.immutable.toImmutableList

class FAQActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            val isTopAppBarColorIcon by config.isTopAppBarColorIcon.collectAsStateWithLifecycle(initialValue = config.topAppBarColorIcon)
            val isTopAppBarColorTitle by config.isTopAppBarColorTitle.collectAsStateWithLifecycle(initialValue = config.topAppBarColorTitle)
            AppThemeSurface {
                val faqItems = remember { intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem> }
                FAQScreen(
                    goBack = ::finish,
                    faqItems = faqItems.toImmutableList(),
                    isTopAppBarColorIcon = isTopAppBarColorIcon,
                    isTopAppBarColorTitle = isTopAppBarColorTitle,
                    onCopy = { faqText ->
                        copyToClipboard(faqText)
                    },
                )
            }
        }
    }
}
