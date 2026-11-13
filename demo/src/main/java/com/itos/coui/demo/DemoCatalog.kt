package com.itos.coui.demo

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.demo.ui.Icon
import com.itos.coui.demo.ui.Text
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.theme.CouiTheme

internal data class DemoCatalogEntry(
    val page: String,
    val title: Int,
    val tone: DemoIconTone,
    val icon: @Composable () -> ImageVector,
)

private val CatalogRowMinHeight = 52.dp

internal val DemoWheelPickerEntries = listOf(
    DemoCatalogEntry("wheel", R.string.picker_number_title, DemoIconTone.Orange) { DemoIcons.Tune },
    DemoCatalogEntry("timepicker", R.string.picker_time_title, DemoIconTone.Blue) { CouiIcons.Filled.Radar },
    DemoCatalogEntry("countdownpicker", R.string.picker_countdown_title, DemoIconTone.Green) { CouiIcons.Filled.Battery },
    DemoCatalogEntry("datepicker", R.string.picker_date_title, DemoIconTone.Yellow) { DemoIcons.Date },
)

internal val DemoComponentGroups = listOf(
    listOf(DemoCatalogEntry("switch", R.string.switch_title, DemoIconTone.Blue) { DemoIcons.Switch },
        DemoCatalogEntry("segment", R.string.segment_title, DemoIconTone.Purple) { CouiIcons.Filled.GridSliders },
        DemoCatalogEntry("sliders", R.string.slider_title, DemoIconTone.Orange) { CouiIcons.Outlined.SliderCustom },
        DemoCatalogEntry("checkbox", R.string.checkbox_title, DemoIconTone.Green) { CouiIcons.Outlined.ListFilter }),
    listOf(DemoCatalogEntry("button", R.string.button_title, DemoIconTone.Blue) { CouiIcons.Filled.Add },
        DemoCatalogEntry("progress", R.string.progress_title, DemoIconTone.Green) { CouiIcons.Filled.Fan },
        DemoCatalogEntry("toast", R.string.toast_title, DemoIconTone.Orange) { CouiIcons.Filled.CloudData },
        DemoCatalogEntry("badge", R.string.badge_title, DemoIconTone.Red) { CouiIcons.Filled.Rings }),
    listOf(DemoCatalogEntry("pageindicator", R.string.pageindicator_title, DemoIconTone.Blue) { CouiIcons.Filled.Album },
        DemoCatalogEntry("emptystate", R.string.emptystate_title, DemoIconTone.Yellow) { CouiIcons.Filled.BookOpen },
        DemoCatalogEntry("textfield", R.string.textfield_title, DemoIconTone.Purple) { CouiIcons.Filled.More },
        DemoCatalogEntry("bottomsheet", R.string.bottomsheet_title, DemoIconTone.Purple) { CouiIcons.Filled.PanelEnterLeft }),
    listOf(DemoCatalogEntry("dialogs", R.string.dialog_title, DemoIconTone.Blue) { DemoIcons.Dialogs },
        DemoCatalogEntry("menu", R.string.menu_title, DemoIconTone.Orange) { CouiIcons.Filled.Grid }),
    listOf(DemoCatalogEntry("appbar", R.string.catalog_appbar, DemoIconTone.Blue) { DemoIcons.Title },
        DemoCatalogEntry("search", R.string.catalog_search, DemoIconTone.Green) { CouiIcons.Outlined.Search },
        DemoCatalogEntry("navigation", R.string.catalog_navigation, DemoIconTone.Blue) { CouiIcons.Filled.CloudLink },
        DemoCatalogEntry("chip", R.string.chip_tab_title, DemoIconTone.Purple) { CouiIcons.Filled.Lab },
        DemoCatalogEntry("fab", R.string.catalog_fab, DemoIconTone.Red) { CouiIcons.Filled.AI }),
    listOf(DemoCatalogEntry("wheel", R.string.catalog_wheel, DemoIconTone.Orange) { DemoIcons.Tune },
        DemoCatalogEntry("calendar", R.string.picker_calendar_title, DemoIconTone.Yellow) { CouiIcons.Filled.Health }),
    listOf(DemoCatalogEntry("layout", R.string.catalog_layout, DemoIconTone.Green) { CouiIcons.Outlined.LayoutSpliced },
        DemoCatalogEntry("manager", R.string.manager_title, DemoIconTone.Green) { CouiIcons.Filled.Health },
        DemoCatalogEntry("cleanup", R.string.cleanup_title, DemoIconTone.Green) { CouiIcons.Filled.Fan }),
)

internal val DemoEffectGroups = listOf(
    listOf(DemoCatalogEntry("effect_shadow", R.string.shadow_title, DemoIconTone.Purple) { CouiIcons.Filled.DarkLight },
        DemoCatalogEntry("effect_fusion", R.string.fusion_title, DemoIconTone.Blue) { CouiIcons.Filled.MultiPhone },
        DemoCatalogEntry("effect_backdrop", R.string.backdrop_title, DemoIconTone.Orange) { CouiIcons.Filled.PanelEnterRight },
        DemoCatalogEntry("effect_material", R.string.material_title, DemoIconTone.Green) { CouiIcons.Filled.Foldable }),
    listOf(DemoCatalogEntry("effect_gradient", R.string.effect_gradient_title, DemoIconTone.Purple) { CouiIcons.Filled.Photo },
        DemoCatalogEntry("effect_lighting", R.string.effect_lighting_title, DemoIconTone.Red) { CouiIcons.Filled.ShieldFlash },
        DemoCatalogEntry("effect_motion", R.string.effect_motion_title, DemoIconTone.Blue) { CouiIcons.Filled.Gamepad },
        DemoCatalogEntry("effect_floating", R.string.effect_floating_title, DemoIconTone.Orange) { CouiIcons.Filled.Camera }),
)

/**
 * 目录图标：原厂功能图标本身不带底色，只有图形（`drawable/common_file_*_icon.xml` 是同色系两档叠层次）。
 * 库内图标是单 path + `android:tint` 的单色矢量，因此这里只做着色，不加任何底。
 */
@Composable
private fun DemoCatalogIcon(entry: DemoCatalogEntry) {
    Icon(entry.icon(), null, Modifier.size(CouiPageDefaults.iconSize), tint = entry.tone.color())
}

@Composable
internal fun DemoCatalog(groups: List<List<DemoCatalogEntry>>, open: (String) -> Unit, enabled: Boolean = true,
    query: String = "") {
    val needle = query.trim()
    var hasMatches = false
    groups.forEach { entries ->
        val matches = entries.mapNotNull { entry ->
            if (catalogSearchMatches(entry, needle)) entry to entry.page
            else if (entry.page == "wheel") {
                DemoWheelPickerEntries.firstOrNull { catalogSearchMatches(it, needle) }?.let { entry to it.page }
            } else null
        }
        if (matches.isNotEmpty()) {
            hasMatches = true
            key(entries.first().page) {
                Group {
                    matches.forEachIndexed { index, (entry, route) -> key(entry.page) {
                        DemoPreference(Modifier.heightIn(min = CatalogRowMinHeight),
                            title = stringResource(entry.title), enabled = enabled, onClick = { open(route) },
                            icon = { DemoCatalogIcon(entry) })
                        if (index != matches.lastIndex) Divider(startIndent =
                            CouiPageDefaults.rowPadding + CouiPageDefaults.iconSize + CouiPageDefaults.iconSpacing)
                    } }
                }
            }
        }
    }
    if (!hasMatches) Text(stringResource(R.string.no_matches), Modifier.padding(32.dp),
        color = CouiTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun catalogSearchMatches(entry: DemoCatalogEntry, needle: String): Boolean =
    stringResource(entry.title).contains(needle, ignoreCase = true) ||
        entry.page.contains(needle, ignoreCase = true) ||
        (entry.page == "checkbox" && "radio".contains(needle, ignoreCase = true)) ||
        stringResource(if (entry.page == "wheel") R.string.intro_numberpicker else catalogDescription(entry.page))
            .contains(needle, ignoreCase = true) ||
        (entry.page == "wheel" && stringResource(R.string.picker_number_title).contains(needle, ignoreCase = true))

internal fun catalogDescription(page: String): Int = when (page) {
    "chip" -> R.string.intro_chip
    "switch" -> R.string.intro_switch
    "sliders" -> R.string.intro_sliders
    "segment" -> R.string.intro_segment
    "checkbox" -> R.string.intro_checkbox
    "button" -> R.string.intro_button
    "progress" -> R.string.intro_progress
    "toast" -> R.string.intro_toast
    "badge" -> R.string.intro_badge
    "pageindicator" -> R.string.intro_pageindicator
    "emptystate" -> R.string.intro_emptystate
    "textfield" -> R.string.intro_textfield
    "bottomsheet" -> R.string.intro_bottomsheet
    "dialogs" -> R.string.intro_dialogs
    "menu" -> R.string.intro_menu
    "appbar" -> R.string.intro_appbar
    "search" -> R.string.intro_search
    "navigation" -> R.string.intro_navigation
    "fab" -> R.string.intro_fab
    "wheel" -> R.string.intro_wheel
    "timepicker" -> R.string.intro_timepicker
    "countdownpicker" -> R.string.intro_countdownpicker
    "datepicker" -> R.string.intro_datepicker
    "calendar" -> R.string.intro_calendar
    "layout" -> R.string.intro_layout
    "effect_backdrop" -> R.string.intro_effect_backdrop
    "effect_material" -> R.string.intro_effect_material
    "effect_shadow" -> R.string.intro_effect_shadow
    "effect_fusion" -> R.string.intro_effect_fusion
    "effect_gradient" -> R.string.intro_effect_gradient
    "effect_lighting" -> R.string.intro_effect_lighting
    "effect_motion" -> R.string.intro_effect_motion
    "effect_floating" -> R.string.intro_effect_floating
    else -> R.string.intro_settings
}
