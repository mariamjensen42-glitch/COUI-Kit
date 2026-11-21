package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.accordion.Accordion
import com.cycling.coui.avatar.Avatar
import com.cycling.coui.avatar.CouiAvatarDefaults
import com.cycling.coui.button.Button
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.icon.Icon
import com.cycling.coui.emptystate.EmptyState
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.layout.Item
import com.cycling.coui.pulltorefresh.PullToRefresh
import com.cycling.coui.statelayout.CouiLoadState
import com.cycling.coui.statelayout.StateLayout
import com.cycling.coui.stepper.StepBar
import com.cycling.coui.stepper.StepRing
import com.cycling.coui.stepper.Stepper
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 下拉刷新：刷新态由宿主维护，示例延迟 1.2 秒结束。 */
@Composable
internal fun PullToRefreshSample(dark: Boolean) {
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // 刷新容器自己吃下拉位移，内部只用普通滚动，避免两层位移叠加。
    PullToRefresh(modifier = Modifier.fillMaxSize(), refreshing = refreshing, dark = dark,
        onRefresh = {
            refreshing = true
            scope.launch {
                delay(1200)
                refreshing = false
            }
        }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = CouiPageDefaults.horizontalMargin)) {
            Group(title = stringResource(R.string.pull_refresh_hint)) {
                repeat(12) { index ->
                    Item(title = {
                        Text(text = stringResource(R.string.pull_refresh_row, index + 1),
                            kind = CouiTextKind.ItemTitle)
                    })
                    if (index != 11) Divider()
                }
            }
            Box(Modifier.height(16.dp))
        }
    }
}

/** 折叠面板：受控展开，放在 Group 卡片里就是设置页的「高级设置」。 */
@Composable
internal fun AccordionSample(dark: Boolean) {
    var advanced by rememberSaveable { mutableStateOf(false) }
    var network by rememberSaveable { mutableStateOf(true) }
    Group(title = stringResource(R.string.accordion_title)) {
        Accordion(title = stringResource(R.string.accordion_advanced),
            summary = stringResource(R.string.accordion_advanced_summary),
            expanded = advanced, onExpandedChange = { advanced = it }, dark = dark) {
            Item(title = { Text(text = stringResource(R.string.accordion_row_animation), kind = CouiTextKind.ItemTitle) })
            Divider(startIndent = CouiPageDefaults.rowPadding)
            Item(title = { Text(text = stringResource(R.string.accordion_row_log), kind = CouiTextKind.ItemTitle) })
        }
        Divider()
        Accordion(title = stringResource(R.string.accordion_network),
            summary = stringResource(R.string.accordion_network_summary),
            expanded = network, onExpandedChange = { network = it }, dark = dark) {
            Item(title = { Text(text = stringResource(R.string.accordion_row_proxy), kind = CouiTextKind.ItemTitle) })
        }
    }
}

/** 头像：尺寸与轮廓由参数控制，内容可以是图片、图标或首字母。 */
@Composable
internal fun AvatarSample(dark: Boolean) {
    val scheme = CouiTheme.colorScheme
    Group(title = stringResource(R.string.avatar_title)) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Avatar(containerColor = scheme.accent) {
                Text(text = stringResource(R.string.avatar_initial), kind = CouiTextKind.ItemTitle,
                    color = scheme.onAccent)
            }
            Avatar(size = CouiAvatarDefaults.largeSize,
                containerColor = scheme.container) {
                Icon(CouiIcons.Filled.Health, null, Modifier.size(CouiPageDefaults.iconSize),
                    tint = scheme.accent)
            }
            Avatar(size = CouiAvatarDefaults.smallSize,
                containerColor = scheme.container) {
                Icon(CouiIcons.Filled.AI, null, Modifier.size(16.dp), tint = scheme.accent)
            }
        }
    }
}

/** 三态容器：加载 / 空 / 失败 / 就绪 四态切换；空态由容器按原厂整页规则摆位。 */
@Composable
internal fun StateLayoutSample(dark: Boolean) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val states = listOf(CouiLoadState.Loading, CouiLoadState.Empty,
        CouiLoadState.Failed(stringResource(R.string.state_layout_error_message)), CouiLoadState.Ready)
    Group(title = stringResource(R.string.state_layout_title)) {
        Box(Modifier.fillMaxWidth().height(200.dp)) {
            StateLayout(modifier = Modifier.fillMaxSize(), state = states[index],
                empty = {
                    EmptyState(title = stringResource(R.string.state_layout_empty), dark = dark,
                        icon = { Icon(CouiIcons.Outlined.Search, null, Modifier.size(48.dp)) })
                },
                failed = { message ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = message ?: "", kind = CouiTextKind.Body,
                            color = CouiTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = { index = 0 }, label = stringResource(R.string.state_layout_retry),
                            style = CouiButtonStyle.Text, dark = dark)
                    }
                }) {
                Text(text = stringResource(R.string.state_layout_ready), kind = CouiTextKind.ItemTitle)
            }
        }
        Divider()
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { index = (index + 1) % states.size },
                label = stringResource(R.string.state_layout_next), style = CouiButtonStyle.Secondary, dark = dark)
        }
    }
}

/** 步骤条：当前步骤由宿主推进。 */
@Composable
internal fun StepBarSample(dark: Boolean) {
    var current by rememberSaveable { mutableIntStateOf(1) }
    val steps = listOf(stringResource(R.string.step_bar_step_scan),
        stringResource(R.string.step_bar_step_clean),
        stringResource(R.string.step_bar_step_done))
    Group(title = stringResource(R.string.step_bar_title)) {
        Column(Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StepBar(steps = steps, currentIndex = current, dark = dark)
            // 环形徽标：只表达「第几步 / 共几步」，可与横向步骤条并排使用。
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                repeat(steps.size) { index ->
                    StepRing(step = index + 1, total = steps.size, dark = dark)
                }
                StepRing(step = steps.size, total = steps.size, dark = dark, size = 32.dp)
                Text(text = stringResource(R.string.step_ring_summary, current + 1, steps.size),
                    kind = CouiTextKind.ItemSummary)
            }
            Button(onClick = { current = (current + 1) % steps.size },
                label = stringResource(R.string.step_bar_next), style = CouiButtonStyle.Text, dark = dark)
        }
    }
}

/** 数值步进器：数值由宿主持有，单击或长按提交夹紧后的新值。 */
@Composable
internal fun NumberStepperSample(dark: Boolean) {
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    Group(title = stringResource(R.string.stepper_title)) {
        Row(Modifier.fillMaxWidth().padding(CouiPageDefaults.rowPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.list_item_stepper), kind = CouiTextKind.ItemTitle)
            Stepper(value = quantity, onValueChange = { quantity = it }, valueRange = 0..99, dark = dark)
        }
    }
}
