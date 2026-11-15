package com.itos.coui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.button.Button
import com.itos.coui.button.CouiButtonStyle
import com.itos.coui.button.CouiIconButtonDefaults
import com.itos.coui.button.IconButton
import com.itos.coui.card.Card
import com.itos.coui.demo.ui.Icon
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.CouiAdaptiveLayout
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.CouiWindowSizeClass
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.layout.Item
import com.itos.coui.sidepane.CouiSidePaneSide
import com.itos.coui.sidepane.SidePane
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 图标按钮：三档命中区、带底色的强调形态与禁用态。 */
@Composable
internal fun IconButtonSample(dark: Boolean) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    val colors = CouiTheme.colorSchemeFor(dark)
    Group(title = stringResource(R.string.icon_button_title)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { count++ }, dark = dark) {
                    Icon(CouiIcons.Filled.Add, contentDescription = null, size = 24.dp)
                }
                IconButton(onClick = { count++ }, dark = dark, size = CouiIconButtonDefaults.smallSize) {
                    Icon(CouiIcons.Filled.More, contentDescription = null, size = 20.dp)
                }
                IconButton(onClick = { count++ }, dark = dark, size = CouiIconButtonDefaults.largeSize) {
                    Icon(CouiIcons.Filled.Close, contentDescription = null, size = 24.dp)
                }
                IconButton(onClick = { count++ }, dark = dark,
                    containerColor = colors.accent, contentColor = colors.onAccent) {
                    Icon(CouiIcons.Filled.Add, contentDescription = null, size = 24.dp)
                }
                IconButton(onClick = { count++ }, enabled = false, dark = dark) {
                    Icon(CouiIcons.Filled.Add, contentDescription = null, size = 24.dp)
                }
            }
            Text(text = stringResource(R.string.icon_button_clicks, count), kind = CouiTextKind.ItemSummary,
                color = colors.onSurfaceVariant)
        }
    }
    Group(footer = stringResource(R.string.icon_button_footer)) {
        Item(title = { Text(text = stringResource(R.string.icon_button_row_default), kind = CouiTextKind.ItemTitle) },
            trailing = {
                IconButton(onClick = { count++ }, dark = dark) {
                    Icon(CouiIcons.Filled.More, contentDescription = null, size = 24.dp)
                }
            })
        Divider()
        Item(title = { Text(text = stringResource(R.string.icon_button_row_small), kind = CouiTextKind.ItemTitle) },
            trailing = {
                IconButton(onClick = { count++ }, dark = dark, size = CouiIconButtonDefaults.smallSize) {
                    Icon(CouiIcons.Filled.More, contentDescription = null, size = 20.dp)
                }
            })
    }
}

/** 卡片容器：静态卡、可点卡与禁用卡的区别。 */
@Composable
internal fun CardSample(dark: Boolean) {
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    val colors = CouiTheme.colorSchemeFor(dark)
    Group(title = stringResource(R.string.card_title)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(dark = dark) {
                Text(text = stringResource(R.string.card_static_title), kind = CouiTextKind.ItemTitle,
                    color = colors.onSurface)
                Text(text = stringResource(R.string.card_static_summary), kind = CouiTextKind.ItemSummary,
                    color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
            Card(dark = dark, onClick = { clicks++ }) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.card_click_title), kind = CouiTextKind.ItemTitle,
                            color = colors.onSurface)
                        Text(text = stringResource(R.string.card_click_summary, clicks),
                            kind = CouiTextKind.ItemSummary, color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                    Icon(CouiIcons.Filled.Next, contentDescription = null, size = 20.dp,
                        tint = colors.onSurfaceVariant)
                }
            }
            Card(dark = dark, enabled = false, onClick = {}) {
                Text(text = stringResource(R.string.card_disabled_summary), kind = CouiTextKind.ItemSummary,
                    color = colors.onSurface.copy(alpha = 0.3f))
            }
        }
    }
    Group(footer = stringResource(R.string.card_footer)) {
        Item(title = { Text(text = stringResource(R.string.card_padding), kind = CouiTextKind.ItemTitle) },
            trailing = {
                Text(text = stringResource(R.string.card_padding_value), kind = CouiTextKind.ItemValue,
                    color = colors.onSurfaceVariant)
            })
    }
}

/** 自适应断点：同一份内容在三种宽度下的排布。 */
@Composable
internal fun AdaptiveSample(dark: Boolean) {
    CouiAdaptiveLayout { sizeClass ->
        // 回调内容在 Box 里, 多个分组要自己纵向排列, 否则会叠在同一个起点上。
        Column { AdaptiveSampleContent(dark = dark, sizeClass = sizeClass) }
    }
}

@Composable
private fun AdaptiveSampleContent(dark: Boolean, sizeClass: CouiWindowSizeClass) {
    val colors = CouiTheme.colorSchemeFor(dark)
    Group(title = stringResource(R.string.adaptive_title)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.adaptive_current, sizeClass.name),
                kind = CouiTextKind.ItemTitle, color = colors.onSurface)
            Text(text = stringResource(R.string.adaptive_current_summary), kind = CouiTextKind.ItemSummary,
                color = colors.onSurfaceVariant)
        }
    }
    Group(title = stringResource(R.string.adaptive_breakpoints)) {
        Item(title = { Text(text = "Compact", kind = CouiTextKind.ItemTitle) },
            trailing = {
                Text(text = stringResource(R.string.adaptive_compact_range), kind = CouiTextKind.ItemValue,
                    color = colors.onSurfaceVariant)
            })
        Divider()
        Item(title = { Text(text = "Medium", kind = CouiTextKind.ItemTitle) },
            trailing = {
                Text(text = stringResource(R.string.adaptive_medium_range), kind = CouiTextKind.ItemValue,
                    color = colors.onSurfaceVariant)
            })
        Divider()
        Item(title = { Text(text = "Expanded", kind = CouiTextKind.ItemTitle) },
            trailing = {
                Text(text = stringResource(R.string.adaptive_expanded_range), kind = CouiTextKind.ItemValue,
                    color = colors.onSurfaceVariant)
            })
    }
    // 只有 Expanded 起才并排, 其余档位保持单列。
    Group(title = stringResource(R.string.adaptive_columns)) {
        if (sizeClass == CouiWindowSizeClass.Expanded) {
            Row(Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { AdaptiveCell(stringResource(R.string.adaptive_cell_left), dark) }
                Box(Modifier.weight(1f)) { AdaptiveCell(stringResource(R.string.adaptive_cell_right), dark) }
            }
        } else {
            Column(Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AdaptiveCell(stringResource(R.string.adaptive_cell_left), dark)
                AdaptiveCell(stringResource(R.string.adaptive_cell_right), dark)
            }
        }
    }
}

@Composable
private fun AdaptiveCell(label: String, dark: Boolean) {
    val colors = CouiTheme.colorSchemeFor(dark)
    Card(dark = dark, shape = CouiTheme.shapes.medium, containerColor = colors.container) {
        Text(text = label, kind = CouiTextKind.ItemSummary, color = colors.onSurfaceVariant)
    }
}

/**
 * 侧栏：侧栏要包住整页才有意义。遮罩以组件自身的位置和尺寸为基准，所以这里把整页
 * （折叠顶栏 + 正文）放进 content 槽，而不是把 SidePane 塞进滚动内容里的某个分组。
 */
@Composable
internal fun SidePanePage(dark: Boolean, onBack: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    var side by rememberSaveable { mutableStateOf(CouiSidePaneSide.Start) }
    val colors = CouiTheme.colorSchemeFor(dark)
    // 库不接管返回键: 宿主自己决定展开时先收起侧栏, 而不是直接退出页面。
    BackHandler(open) { open = false }
    SidePane(modifier = Modifier.fillMaxSize(), visible = open, onDismissRequest = { open = false },
        dark = dark, side = side, paneTitle = stringResource(R.string.side_pane_title),
        pane = {
            // 侧栏贴到窗口边缘, 系统栏的留白由内容自己给, 库不替宿主决定。
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = stringResource(R.string.side_pane_title), kind = CouiTextKind.ItemTitle,
                    color = colors.onSurface)
                Text(text = stringResource(R.string.side_pane_summary), kind = CouiTextKind.ItemSummary,
                    color = colors.onSurfaceVariant)
                Button(onClick = { open = false }, label = stringResource(R.string.side_pane_close),
                    style = CouiButtonStyle.Secondary, dark = dark)
            }
        }) {
        DemoSettingsPage(title = stringResource(R.string.catalog_side_pane),
            backDescription = stringResource(R.string.settings_back), onBack = onBack, dark = dark,
            colors = CouiPageDefaults.themeColors(dark)) {
            Group(title = stringResource(R.string.side_pane_title)) {
                Column(Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = stringResource(R.string.side_pane_hint), kind = CouiTextKind.ItemSummary,
                        color = colors.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { side = CouiSidePaneSide.Start; open = true },
                            label = stringResource(R.string.side_pane_start), style = CouiButtonStyle.Secondary,
                            dark = dark)
                        Button(onClick = { side = CouiSidePaneSide.End; open = true },
                            label = stringResource(R.string.side_pane_end), style = CouiButtonStyle.Secondary,
                            dark = dark)
                    }
                }
            }
            Group(footer = stringResource(R.string.side_pane_footer)) {
                Item(title = {
                    Text(text = stringResource(R.string.side_pane_width), kind = CouiTextKind.ItemTitle)
                }, trailing = {
                    Text(text = stringResource(R.string.side_pane_width_value), kind = CouiTextKind.ItemValue,
                        color = colors.onSurfaceVariant)
                })
            }
            Box(Modifier.height(16.dp))
        }
    }
}
