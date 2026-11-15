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

/** 目录分组：分类标题 + 条目。分组按 docs/components/README.md 的分类划分。 */
internal data class DemoCatalogGroup(val title: Int, val entries: List<DemoCatalogEntry>)

private val CatalogRowMinHeight = 52.dp

internal val DemoWheelPickerEntries = listOf(
    DemoCatalogEntry("wheel", R.string.picker_number_title, DemoIconTone.Orange) { DemoIcons.Tune },
    DemoCatalogEntry("timepicker", R.string.picker_time_title, DemoIconTone.Blue) { CouiIcons.Filled.Radar },
    DemoCatalogEntry("countdownpicker", R.string.picker_countdown_title, DemoIconTone.Green) { CouiIcons.Filled.Battery },
    DemoCatalogEntry("datepicker", R.string.picker_date_title, DemoIconTone.Yellow) { DemoIcons.Date },
    DemoCatalogEntry("lunardatepicker", R.string.picker_lunar_title, DemoIconTone.Purple) { DemoIcons.Date },
    DemoCatalogEntry("timelimitpicker", R.string.picker_time_limit_title, DemoIconTone.Red) { DemoIcons.Tune },
)

internal val DemoComponentGroups = listOf(
    DemoCatalogGroup(R.string.catalog_group_navigation, listOf(
        DemoCatalogEntry("appbar", R.string.catalog_appbar, DemoIconTone.Blue) { DemoIcons.Title },
        DemoCatalogEntry("userfollow", R.string.user_follow_title, DemoIconTone.Yellow) { CouiIcons.Filled.UserCircle },
        DemoCatalogEntry("search", R.string.catalog_search, DemoIconTone.Green) { CouiIcons.Outlined.Search },
        DemoCatalogEntry("navigation", R.string.catalog_navigation, DemoIconTone.Blue) { CouiIcons.Filled.CloudLink },
        DemoCatalogEntry("dockbar", R.string.catalog_dock_bar, DemoIconTone.Orange) { CouiIcons.Filled.PanelEnterRight },
        DemoCatalogEntry("navigationfamily", R.string.nav_family_title, DemoIconTone.Green) { CouiIcons.Filled.Foldable },
        DemoCatalogEntry("fab", R.string.catalog_fab, DemoIconTone.Red) { CouiIcons.Filled.AI },
        DemoCatalogEntry("adaptive", R.string.catalog_adaptive, DemoIconTone.Green) { CouiIcons.Filled.Foldable },
        DemoCatalogEntry("sidepane", R.string.catalog_side_pane, DemoIconTone.Purple) { CouiIcons.Filled.PanelEnterLeft },
        DemoCatalogEntry("floatingtoolbar", R.string.catalog_floating_toolbar, DemoIconTone.Purple) { CouiIcons.Filled.PanelEnterRight },
        DemoCatalogEntry("tablayout", R.string.tab_layout_title, DemoIconTone.Blue) { CouiIcons.Filled.Album })),
    DemoCatalogGroup(R.string.catalog_group_content, listOf(
        DemoCatalogEntry("layout", R.string.catalog_layout, DemoIconTone.Green) { CouiIcons.Outlined.LayoutSpliced },
        DemoCatalogEntry("container", R.string.catalog_card, DemoIconTone.Blue) { CouiIcons.Filled.SafeBox },
        DemoCatalogEntry("cards", R.string.catalog_action_card, DemoIconTone.Blue) { CouiIcons.Filled.PanelEnterLeft },
        DemoCatalogEntry("notification", R.string.catalog_notification_card, DemoIconTone.Yellow) { CouiIcons.Filled.Bell },
        DemoCatalogEntry("quicksetting", R.string.catalog_quick_setting, DemoIconTone.Blue) { CouiIcons.Filled.Grid },
        DemoCatalogEntry("quicksettingedit", R.string.qs_edit_title, DemoIconTone.Blue) { CouiIcons.Filled.GridSliders },
        DemoCatalogEntry("stateeffect", R.string.state_effect_title, DemoIconTone.Purple) { CouiIcons.Filled.ShieldFlash },
        DemoCatalogEntry("thumbnail", R.string.catalog_thumbnail_choice, DemoIconTone.Blue) { CouiIcons.Filled.DarkLight },
        DemoCatalogEntry("choice", R.string.catalog_choice_card, DemoIconTone.Purple) { CouiIcons.Filled.GridSliders },
        DemoCatalogEntry("chart", R.string.catalog_bar_chart, DemoIconTone.Green) { CouiIcons.Filled.Battery },
        DemoCatalogEntry("accordion", R.string.catalog_accordion, DemoIconTone.Orange) { CouiIcons.Filled.BookOpen },
        DemoCatalogEntry("avatar", R.string.catalog_avatar, DemoIconTone.Green) { CouiIcons.Filled.Health },
        DemoCatalogEntry("stepbar", R.string.catalog_step_bar, DemoIconTone.Yellow) { CouiIcons.Filled.Album },
        DemoCatalogEntry("tag", R.string.tag_title, DemoIconTone.Orange) { CouiIcons.Filled.Lab },
        DemoCatalogEntry("richtext", R.string.richtext_title, DemoIconTone.Blue) { CouiIcons.Filled.BookOpen },
        DemoCatalogEntry("scrollbar", R.string.scrollbar_title, DemoIconTone.Purple) { CouiIcons.Filled.Grid })),
    DemoCatalogGroup(R.string.catalog_group_input, listOf(
        DemoCatalogEntry("switch", R.string.switch_title, DemoIconTone.Blue) { DemoIcons.Switch },
        DemoCatalogEntry("checkbox", R.string.checkbox_title, DemoIconTone.Green) { CouiIcons.Outlined.ListFilter },
        DemoCatalogEntry("segment", R.string.segment_title, DemoIconTone.Purple) { CouiIcons.Filled.GridSliders },
        DemoCatalogEntry("sliders", R.string.slider_title, DemoIconTone.Orange) { CouiIcons.Outlined.SliderCustom },
        DemoCatalogEntry("textfield", R.string.catalog_text_field, DemoIconTone.Purple) { CouiIcons.Filled.BookOpen },
        DemoCatalogEntry("textfields", R.string.textfield_family_title, DemoIconTone.Purple) { CouiIcons.Filled.BookOpen },
        DemoCatalogEntry("marquee", R.string.marquee_title, DemoIconTone.Blue) { CouiIcons.Filled.CloudData },
        DemoCatalogEntry("verticalseek", R.string.vertical_seek_title, DemoIconTone.Orange) { CouiIcons.Outlined.SliderCustom },
        DemoCatalogEntry("seekbarfamily", R.string.seekbar_family_title, DemoIconTone.Orange) { CouiIcons.Outlined.SliderCustom },
        DemoCatalogEntry("progressivedrag", R.string.progressive_drag_title, DemoIconTone.Blue) { CouiIcons.Filled.SliderCustom },
        DemoCatalogEntry("chips", R.string.chip_family_title, DemoIconTone.Purple) { CouiIcons.Filled.Lab },
        DemoCatalogEntry("chip", R.string.chip_tab_title, DemoIconTone.Purple) { CouiIcons.Filled.Lab },
        DemoCatalogEntry("stepper", R.string.catalog_stepper, DemoIconTone.Green) { DemoIcons.Switch },
        DemoCatalogEntry("wheel", R.string.catalog_wheel, DemoIconTone.Orange) { DemoIcons.Tune },
        DemoCatalogEntry("calendar", R.string.picker_calendar_title, DemoIconTone.Yellow) { CouiIcons.Filled.Health },
        DemoCatalogEntry("searchhistory", R.string.search_history_title, DemoIconTone.Green) { CouiIcons.Outlined.Search })),
    DemoCatalogGroup(R.string.catalog_group_overlay, listOf(
        DemoCatalogEntry("dialogs", R.string.dialog_title, DemoIconTone.Blue) { DemoIcons.Dialogs },
        DemoCatalogEntry("dialogfamily", R.string.dialog_family_title, DemoIconTone.Blue) { DemoIcons.Dialogs },
        DemoCatalogEntry("bottomsheet", R.string.bottomsheet_title, DemoIconTone.Purple) { CouiIcons.Filled.PanelEnterLeft },
        DemoCatalogEntry("menu", R.string.menu_title, DemoIconTone.Orange) { CouiIcons.Filled.Grid })),
    DemoCatalogGroup(R.string.catalog_group_feedback, listOf(
        DemoCatalogEntry("button", R.string.button_title, DemoIconTone.Blue) { CouiIcons.Filled.Add },
        DemoCatalogEntry("buttonbar", R.string.buttonbar_title, DemoIconTone.Orange) { DemoIcons.Dialogs },
        DemoCatalogEntry("floatmenu", R.string.catalog_floatmenu, DemoIconTone.Green) { CouiIcons.Filled.CloudData },
        DemoCatalogEntry("iconbutton", R.string.catalog_icon_button, DemoIconTone.Purple) { CouiIcons.Filled.More },
        DemoCatalogEntry("progress", R.string.progress_title, DemoIconTone.Green) { CouiIcons.Filled.Fan },
        DemoCatalogEntry("toast", R.string.toast_title, DemoIconTone.Orange) { CouiIcons.Filled.CloudData },
        DemoCatalogEntry("snackbar", R.string.snackbar_title, DemoIconTone.Purple) { CouiIcons.Filled.CloudData },
        DemoCatalogEntry("badge", R.string.badge_title, DemoIconTone.Red) { CouiIcons.Filled.Rings },
        DemoCatalogEntry("pageindicator", R.string.pageindicator_title, DemoIconTone.Blue) { CouiIcons.Filled.Album },
        DemoCatalogEntry("emptystate", R.string.emptystate_title, DemoIconTone.Yellow) { CouiIcons.Filled.BookOpen },
        DemoCatalogEntry("statelayout", R.string.catalog_state_layout, DemoIconTone.Purple) { CouiIcons.Filled.Fan },
        DemoCatalogEntry("refresh", R.string.catalog_pull_refresh, DemoIconTone.Blue) { CouiIcons.Filled.CloudData },
        DemoCatalogEntry("toptips", R.string.top_tips_title, DemoIconTone.Yellow) { CouiIcons.Filled.Bell })),
    DemoCatalogGroup(R.string.catalog_group_pages, listOf(
        DemoCatalogEntry("manager", R.string.manager_title, DemoIconTone.Green) { CouiIcons.Filled.Health },
        DemoCatalogEntry("cleanup", R.string.cleanup_title, DemoIconTone.Green) { CouiIcons.Filled.Fan })),
)

internal val DemoEffectGroups = listOf(
    DemoCatalogGroup(R.string.catalog_group_effect_material, listOf(
        DemoCatalogEntry("effect_shadow", R.string.shadow_title, DemoIconTone.Purple) { CouiIcons.Filled.DarkLight },
        DemoCatalogEntry("effect_fusion", R.string.fusion_title, DemoIconTone.Blue) { CouiIcons.Filled.MultiPhone },
        DemoCatalogEntry("effect_backdrop", R.string.backdrop_title, DemoIconTone.Orange) { CouiIcons.Filled.PanelEnterRight },
        DemoCatalogEntry("effect_material", R.string.material_title, DemoIconTone.Green) { CouiIcons.Filled.Foldable })),
    DemoCatalogGroup(R.string.catalog_group_effect_motion, listOf(
        DemoCatalogEntry("effect_gradient", R.string.effect_gradient_title, DemoIconTone.Purple) { CouiIcons.Filled.Photo },
        DemoCatalogEntry("effect_lighting", R.string.effect_lighting_title, DemoIconTone.Red) { CouiIcons.Filled.ShieldFlash },
        DemoCatalogEntry("effect_motion", R.string.effect_motion_title, DemoIconTone.Blue) { CouiIcons.Filled.Gamepad },
        DemoCatalogEntry("effect_floating", R.string.effect_floating_title, DemoIconTone.Orange) { CouiIcons.Filled.Camera })),
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
internal fun DemoCatalog(groups: List<DemoCatalogGroup>, open: (String) -> Unit, enabled: Boolean = true,
    query: String = "") {
    val needle = query.trim()
    var hasMatches = false
    groups.forEach { group ->
        val entries = group.entries
        val matches = entries.mapNotNull { entry ->
            if (catalogSearchMatches(entry, needle)) entry to entry.page
            else if (entry.page == "wheel") {
                DemoWheelPickerEntries.firstOrNull { catalogSearchMatches(it, needle) }?.let { entry to it.page }
            } else null
        }
        if (matches.isNotEmpty()) {
            hasMatches = true
            key(group.title) {
                Group(title = stringResource(group.title)) {
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
    "quicksettingedit" -> R.string.qs_edit_intro
    "stateeffect" -> R.string.state_effect_intro
    "userfollow" -> R.string.user_follow_intro
    "chips" -> R.string.intro_chip_family
    "chip" -> R.string.intro_chip
    "switch" -> R.string.intro_switch
    "sliders" -> R.string.intro_sliders
    "segment" -> R.string.intro_segment
    "checkbox" -> R.string.intro_checkbox
    "button" -> R.string.intro_button
    "buttonbar" -> R.string.intro_buttonbar
    "floatmenu" -> R.string.intro_floatmenu
    "progress" -> R.string.intro_progress
    "toast" -> R.string.intro_toast
    "marquee" -> R.string.intro_marquee
    "verticalseek" -> R.string.intro_vertical_seek,
    "seekbarfamily" -> R.string.intro_seekbar_family
    "progressivedrag" -> R.string.progressive_drag_hint
    "toptips" -> R.string.intro_top_tips
    "badge" -> R.string.intro_badge
    "tag" -> R.string.intro_tag
    "richtext" -> R.string.intro_richtext
    "scrollbar" -> R.string.intro_scrollbar
    "pageindicator" -> R.string.intro_pageindicator
    "emptystate" -> R.string.intro_emptystate
    "textfield" -> R.string.intro_text_field
    "textfields" -> R.string.intro_textfield_family
    "bottomsheet" -> R.string.intro_bottomsheet
    "dialogs" -> R.string.intro_dialogs
    "dialogfamily" -> R.string.intro_dialog_family
    "menu" -> R.string.intro_menu
    "appbar" -> R.string.intro_appbar
    "search" -> R.string.intro_search
    "navigation" -> R.string.intro_navigation
    "fab" -> R.string.intro_fab
    "floatingtoolbar" -> R.string.intro_floating_toolbar
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
    "cards" -> R.string.intro_action_card
    "choice" -> R.string.intro_choice_card
    "chart" -> R.string.intro_bar_chart
    "dockbar" -> R.string.intro_dock_bar
    "navigationfamily" -> R.string.intro_nav_family
    "refresh" -> R.string.intro_pull_refresh
    "accordion" -> R.string.intro_accordion
    "avatar" -> R.string.intro_avatar
    "statelayout" -> R.string.intro_state_layout
    "stepbar" -> R.string.intro_step_bar
    "stepper" -> R.string.intro_stepper
    "iconbutton" -> R.string.intro_icon_button
    "container" -> R.string.intro_card
    "adaptive" -> R.string.intro_adaptive
    "sidepane" -> R.string.intro_side_pane
    else -> R.string.intro_settings
}
