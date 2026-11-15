package com.cycling.coui.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.flowlayout.CouiFlowItem
import com.cycling.coui.flowlayout.FlowLayout
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.searchhistory.SearchHistory
import com.cycling.coui.snackbar.CouiSnackbarDefaults
import com.cycling.coui.snackbar.CouiSnackbarState
import com.cycling.coui.tablayout.CouiTabItem
import com.cycling.coui.tablayout.CouiTabLayoutMode
import com.cycling.coui.tablayout.TabLayout
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/** 页签条：胶囊容器里的文字页签，固定收缩与横向滚动两种排布，共用一个受控下标。 */
@Composable
internal fun TabLayoutSample(dark: Boolean) {
    val labels = listOf(
        stringResource(R.string.tab_label_all),
        stringResource(R.string.tab_label_network),
        stringResource(R.string.tab_label_display),
        stringResource(R.string.tab_label_battery),
        stringResource(R.string.tab_label_about),
        stringResource(R.string.tab_label_apps),
    )
    val tabs = labels.map { CouiTabItem(it, it) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Column {
        Group(Modifier.fillMaxWidth(), title = stringResource(R.string.tab_layout_fixed)) {
            TabLayout(
                Modifier.fillMaxWidth(),
                tabs = tabs.take(3),
                selectedIndex = selectedIndex.coerceAtMost(2),
                onSelectedIndexChange = { selectedIndex = it },
                dark = dark,
            )
        }
        Group(Modifier.fillMaxWidth(), title = stringResource(R.string.tab_layout_scrollable)) {
            TabLayout(
                Modifier.fillMaxWidth(),
                tabs = tabs,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { selectedIndex = it },
                mode = CouiTabLayoutMode.Scrollable,
                dark = dark,
            )
        }
    }
}

/** 搜索历史：整块折叠与清空，以及单独使用的换行布局。 */
@Composable
internal fun SearchHistorySample(dark: Boolean) {
    val items = historyItems()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var flowExpanded by rememberSaveable { mutableStateOf(false) }
    var visible by rememberSaveable { mutableStateOf(true) }
    var picked by remember { mutableStateOf("") }
    val clear = stringResource(R.string.search_history_clear)
    val expand = stringResource(R.string.search_history_expand)
    val collapse = stringResource(R.string.search_history_collapse)

    Column {
        if (visible) {
            SearchHistory(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.search_history_section),
                items = items,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onItemClick = { picked = items[it].label },
                onDelete = { visible = false },
                deleteContentDescription = clear,
                maxRowsFolded = 2,
                dark = dark,
                expandContentDescription = expand,
                collapseContentDescription = collapse,
            )
        } else {
            Group(Modifier.fillMaxWidth()) {
                DemoPreference(
                    title = stringResource(R.string.search_history_restore),
                    onClick = { visible = true },
                )
            }
        }
        Group(Modifier.fillMaxWidth(), title = stringResource(R.string.search_history_standalone)) {
            Box(Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp)) {
                FlowLayout(
                    Modifier.fillMaxWidth(),
                    items = items,
                    expanded = flowExpanded,
                    onExpandedChange = { flowExpanded = it },
                    onItemClick = { picked = items[it].label },
                    maxRowsFolded = 2,
                    dark = dark,
                    expandContentDescription = expand,
                    collapseContentDescription = collapse,
                )
            }
            Divider()
            DemoPreference(
                title = stringResource(R.string.search_history_picked),
                value = picked.ifEmpty { "—" },
            )
        }
    }
}

/**
 * Snackbar 的操作区。提示宿主不在这里装配：页面用整页 Box 把 `SnackbarHost` 挂在
 * `Alignment.BottomCenter`，见 DemoSecondaryPage，位置才与页面底部而非列表行对齐。
 */
@Composable
internal fun SnackbarSample(
    dark: Boolean,
    state: CouiSnackbarState,
    onRejected: () -> Unit,
) {
    val message = stringResource(R.string.snackbar_message)
    val longMessage = stringResource(R.string.snackbar_long_message)
    val action = stringResource(R.string.snackbar_action)
    val onSurface = CouiTheme.colorSchemeFor(dark).onSurfaceVariant

    Column {
        Group(Modifier.fillMaxWidth(), title = stringResource(R.string.snackbar_title)) {
            DemoPreference(
                title = stringResource(R.string.snackbar_show_short),
                onClick = { if (!state.show(message)) onRejected() },
            )
            Divider()
            DemoPreference(
                title = stringResource(R.string.snackbar_show_long),
                onClick = {
                    if (!state.show(longMessage, CouiSnackbarDefaults.longMillis)) onRejected()
                },
            )
            Divider()
            DemoPreference(
                title = stringResource(R.string.snackbar_show_indefinite),
                onClick = {
                    if (!state.show(message, CouiSnackbarDefaults.indefiniteMillis, action) { }) {
                        onRejected()
                    }
                },
            )
            Divider()
            DemoPreference(
                title = stringResource(R.string.snackbar_dismiss),
                onClick = { state.dismiss() },
            )
            Divider()
            Text(
                text = stringResource(R.string.snackbar_hint),
                kind = CouiTextKind.Body,
                color = onSurface,
                modifier = Modifier.padding(horizontal = CouiPageDefaults.rowPadding, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun historyItems(): List<CouiFlowItem> = listOf(
    CouiFlowItem("wifi", stringResource(R.string.history_wifi)),
    CouiFlowItem("battery", stringResource(R.string.history_battery)),
    CouiFlowItem("font", stringResource(R.string.history_font)),
    CouiFlowItem("dark", stringResource(R.string.history_dark)),
    CouiFlowItem("ringtone", stringResource(R.string.history_ringtone)),
    CouiFlowItem("wallpaper", stringResource(R.string.history_wallpaper)),
    CouiFlowItem("bluetooth", stringResource(R.string.history_bluetooth)),
    CouiFlowItem("storage", stringResource(R.string.history_storage)),
    CouiFlowItem("update", stringResource(R.string.history_update)),
    CouiFlowItem("privacy", stringResource(R.string.history_privacy)),
)
