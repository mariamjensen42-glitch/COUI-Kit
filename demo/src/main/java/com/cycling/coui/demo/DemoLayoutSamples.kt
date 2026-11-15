package com.cycling.coui.demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.CouiQuickAction
import com.cycling.coui.layout.CouiQuickActionStyle
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Footer
import com.cycling.coui.layout.Group
import com.cycling.coui.layout.QuickActionGrid
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiColor
import com.cycling.coui.theme.CouiTheme

/**
 * 分组标题、头部操作、卡片内容与说明的组合。
 * `Group` 的 `footer` 自动装配说明；`Footer` 也可脱离分组单独使用。
 */
@Composable
internal fun LayoutSample() {
    var presses by rememberSaveable { mutableIntStateOf(0) }
    val colors = CouiTheme.colorScheme
    Group(title = stringResource(R.string.settings_group_example),
        footer = stringResource(R.string.layout_group_footer)) {
        DemoPreference(title = stringResource(R.string.settings_preview),
            summary = stringResource(R.string.settings_preview_summary),
            value = stringResource(R.string.settings_size_default))
        Divider()
        DemoPreference(title = stringResource(R.string.settings_privacy),
            summary = stringResource(R.string.settings_long_summary))
    }
    Group(title = stringResource(R.string.layout_header_title),
        headerActions = {
            Text(Modifier.clickable { presses++ }, text = stringResource(R.string.settings_header_action),
                kind = CouiTextKind.GroupTitle, color = colors.accent)
        }) {
        DemoPreference(title = stringResource(R.string.settings_header_count, presses))
        Divider()
        DemoPreference(title = stringResource(R.string.layout_item_disabled),
            summary = stringResource(R.string.settings_disabled_summary), enabled = false)
    }
    QuickActionsSample()
    Footer(text = stringResource(R.string.layout_standalone_footer))
}

/**
 * 两列图标快捷卡片。网格自带卡片底色与轮廓，不放进 `Group` 的卡片内，
 * 只借用分组标题的排版。
 */
@Composable
private fun QuickActionsSample() {
    val colors = CouiTheme.colorScheme
    val actions = listOf(
        CouiQuickAction(
            label = stringResource(R.string.quick_action_photos),
            icon = { Icon(CouiIcons.Filled.Photo, null, tint = CouiColor.Day.BLUE) },
            onClick = {},
        ),
        CouiQuickAction(
            label = stringResource(R.string.quick_action_videos),
            icon = { Icon(CouiIcons.Filled.Fan, null, tint = CouiColor.Day.GREEN) },
            onClick = {},
        ),
        CouiQuickAction(
            label = stringResource(R.string.quick_action_apps),
            icon = { Icon(CouiIcons.Filled.Grid, null, tint = CouiColor.Day.ORANGE) },
            onClick = {},
        ),
        CouiQuickAction(
            label = stringResource(R.string.quick_action_health),
            icon = { Icon(CouiIcons.Filled.Health, null, tint = CouiColor.Day.RED) },
            onClick = {},
        ),
    )
    Column(Modifier.padding(top = CouiPageDefaults.categoryTopSpacing)) {
        Text(text = stringResource(R.string.quick_action_title), kind = CouiTextKind.GroupTitle,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp))
        QuickActionGrid(Modifier.padding(horizontal = CouiPageDefaults.horizontalMargin), actions = actions)
    }
    Column(Modifier.padding(top = CouiPageDefaults.categoryTopSpacing)) {
        Text(text = stringResource(R.string.quick_action_capsule_title), kind = CouiTextKind.GroupTitle,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp))
        QuickActionGrid(Modifier.padding(horizontal = CouiPageDefaults.horizontalMargin), actions = actions,
            style = CouiQuickActionStyle.Capsule)
    }
}

