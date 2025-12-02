package dev.widebars.commons.samples.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import dev.widebars.commons.R
import dev.widebars.commons.compose.extensions.MyDevices
import dev.widebars.commons.compose.lists.SimpleScaffold
import dev.widebars.commons.compose.lists.simpleTopAppBarColors
import dev.widebars.commons.compose.lists.topAppBarInsets
import dev.widebars.commons.compose.lists.topAppBarPaddings
import dev.widebars.commons.compose.menus.ActionItem
import dev.widebars.commons.compose.menus.ActionMenu
import dev.widebars.commons.compose.menus.OverflowMode
import dev.widebars.commons.compose.settings.SettingsGroup
import dev.widebars.commons.compose.settings.SettingsHorizontalDivider
import dev.widebars.commons.compose.settings.SettingsPreferenceComponent
import dev.widebars.commons.compose.settings.SettingsPurchaseComponent
import dev.widebars.commons.compose.theme.AppThemeSurface
import dev.widebars.commons.compose.theme.LocalTheme
import dev.widebars.commons.compose.theme.SimpleTheme
import dev.widebars.commons.compose.theme.actionModeColor
import dev.widebars.commons.compose.theme.model.Theme
import dev.widebars.commons.extensions.formatDate
import dev.widebars.commons.helpers.TIME_FORMAT_12
import dev.widebars.commons.samples.dialogs.ThemeColorsDialog
import java.util.Calendar
import java.util.Locale

@Composable
fun MainScreen(
    openColorCustomization: () -> Unit,
    manageBlockedNumbers: () -> Unit,
    showComposeDialogs: () -> Unit,
    openTestButton: () -> Unit,
    showMoreApps: Boolean,
    openAbout: () -> Unit,
    moreAppsFromUs: () -> Unit,
    startPurchaseActivity: () -> Unit,
    startTestActivity: () -> Unit,
    isTopAppBarColorIcon: Boolean = false,
    openDateButton: () -> Unit,
    isDateFormat: String = "d.M.y",
    isTimeFormat: String = TIME_FORMAT_12,
    useShamsi: Boolean = false,
) {
    SimpleScaffold(
        customTopBar = { scrolledColor: Color, _: MutableInteractionSource, scrollBehavior: TopAppBarScrollBehavior, statusBarColor: Int, colorTransitionFraction: Float, contrastColor: Color ->

            val iconColor = if (isTopAppBarColorIcon) MaterialTheme.colorScheme.primary else null
            TopAppBar(
                title = {},
                actions = {
                    val actionMenus = remember {
                        buildActionMenuItems(
                            showMoreApps = showMoreApps,
                            openAbout = openAbout,
                            moreAppsFromUs = moreAppsFromUs
                        )
                    }
                    var isMenuVisible by remember { mutableStateOf(false) }
                    ActionMenu(
                        items = actionMenus,
                        numIcons = 2,
                        isMenuVisible = isMenuVisible,
                        onMenuToggle = { isMenuVisible = it },
                        iconsColor = iconColor ?: scrolledColor
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = simpleTopAppBarColors(statusBarColor, colorTransitionFraction, contrastColor),
                modifier = Modifier.topAppBarPaddings(),
                windowInsets = topAppBarInsets()
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            var shouldShake by remember { mutableStateOf(false) }
            SettingsPurchaseComponent(
                onPurchaseClick = startPurchaseActivity,
                enabledShake = shouldShake,
                onShakeFinished = {
                    shouldShake = false
                }
            )
            SettingsGroup(
                title = { Text(text = "Test settings".uppercase()) }
            ) {
                SettingsPreferenceComponent(
                    label = stringResource(id = R.string.color_customization),
                    showChevron = true,
                    doOnPreferenceClick = openColorCustomization
                )
                SettingsHorizontalDivider(thickness = 2.dp)
                SettingsPreferenceComponent(
                    label = "Manage blocked numbers",
                    showChevron = true,
                    doOnPreferenceClick = manageBlockedNumbers
                )
                SettingsHorizontalDivider(thickness = 2.dp)
                SettingsPreferenceComponent(
                    label = "Compose dialogs",
                    showChevron = true,
                    doOnPreferenceClick = showComposeDialogs
                )
                SettingsHorizontalDivider(thickness = 2.dp)
                val cal = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                val formatDate = cal.formatDate(
                    context = LocalContext.current,
                    dateFormat = isDateFormat,
                    timeFormat = isTimeFormat,
                    useShamsi = useShamsi,
                )
                SettingsPreferenceComponent(
                    label = stringResource(id = R.string.change_date_and_time_format),
                    value = formatDate,
                    doOnPreferenceClick = openDateButton
                )
                SettingsHorizontalDivider(thickness = 2.dp)
                SettingsPreferenceComponent(
                    label = "Purchase",
                    showChevron = true,
                    doOnPreferenceClick = { shouldShake = true }
                )
                SettingsHorizontalDivider(thickness = 2.dp)
                SettingsPreferenceComponent(
                    label = "Activity",
                    showChevron = true,
                    doOnPreferenceClick = startTestActivity
                )
                SettingsHorizontalDivider(thickness = 2.dp)

                var showDialog by remember { mutableStateOf(false) }
                SettingsPreferenceComponent(
                    label = "Current theme colors",
                    showChevron = true,
                    doOnPreferenceClick = { showDialog = true }
                )
                if (showDialog) {
                    ThemeColorsDialog(
                        onDismiss = { showDialog = false }
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = openTestButton
            ) {
                Text("Test button")
            }
        }
    }
}

private fun buildActionMenuItems(
    showMoreApps: Boolean,
    openAbout: () -> Unit,
    moreAppsFromUs: () -> Unit
): ImmutableList<ActionItem> {
    val list = mutableListOf<ActionItem>()
    list += ActionItem(
        R.string.about,
        icon = Icons.Outlined.Info,
        doAction = openAbout,
        overflowMode = OverflowMode.NEVER_OVERFLOW,
    )
    if (showMoreApps) {
        list += ActionItem(
            dev.widebars.strings.R.string.more_apps_from_us_g,
            doAction = moreAppsFromUs,
            overflowMode = OverflowMode.ALWAYS_OVERFLOW,
        )
    }
    return list.toImmutableList()
}

@Composable
@ReadOnlyComposable
private fun actionModeBgColor(): Color =
    if (LocalTheme.current is Theme.SystemDefaultMaterialYou) {
        SimpleTheme.colorScheme.primaryContainer
    } else {
        actionModeColor
    }

@Composable
@MyDevices
private fun MainScreenPreview() {
    AppThemeSurface {
        MainScreen(
            openColorCustomization = {},
            manageBlockedNumbers = {},
            showComposeDialogs = {},
            openTestButton = {},
            showMoreApps = true,
            openAbout = {},
            moreAppsFromUs = {},
            startPurchaseActivity = {},
            startTestActivity = {},
            openDateButton = {},
        )
    }
}
