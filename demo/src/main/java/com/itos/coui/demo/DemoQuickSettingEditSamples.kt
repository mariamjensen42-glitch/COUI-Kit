package com.itos.coui.demo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itos.coui.demo.ui.Icon
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.quicksetting.CouiDeformationSpan
import com.itos.coui.quicksetting.CouiDeformationTileDefaults
import com.itos.coui.quicksetting.DeformationTileGrid
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 编辑态的形变磁贴网格：长按进入编辑态，拖动把手切换 1×1 / 2×1 并触发重排。 */
@Composable
internal fun QuickSettingEditSample(dark: Boolean) {
    val labels = listOf(
        stringResource(R.string.quick_setting_wlan),
        stringResource(R.string.quick_setting_bluetooth),
        stringResource(R.string.quick_setting_flashlight),
        stringResource(R.string.quick_setting_dnd),
        stringResource(R.string.quick_setting_location),
        stringResource(R.string.quick_setting_hotspot),
    )
    val icons = listOf(
        CouiIcons.Filled.CloudLink,
        CouiIcons.Filled.Bluetooth,
        CouiIcons.Filled.CloudData,
        CouiIcons.Filled.Bell,
        CouiIcons.Filled.Location,
        CouiIcons.Filled.Bluetooth,
    )
    var editing by rememberSaveable { mutableStateOf(false) }
    var spans by remember { mutableStateOf(List(labels.size) { CouiDeformationSpan.OneByOne }) }
    val iconColor = CouiTheme.colorSchemeFor(dark).accent

    Column {
        Group(title = stringResource(R.string.qs_edit_title)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DemoSwitchPreference(
                    title = stringResource(R.string.qs_edit_mode),
                    checked = editing,
                    onCheckedChange = { editing = it },
                )
                Divider()
                Box(
                    Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) { detectTapGestures(onLongPress = { editing = true }) },
                ) {
                    DeformationTileGrid(
                        items = labels.indices.toList(),
                        spanOf = { spans[it] },
                        editing = editing,
                        colors = CouiDeformationTileDefaults.colors(dark),
                        onSpanChange = { index, span ->
                            spans = spans.toMutableList().also { it[index] = span }
                        },
                    ) { index, span, _ ->
                        val label = labels[index]
                        if (span == CouiDeformationSpan.TwoByOne) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(icons[index], null, Modifier.size(24.dp), tint = iconColor)
                                Text(
                                    text = label,
                                    kind = CouiTextKind.Body,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        } else {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(icons[index], null, Modifier.size(24.dp), tint = iconColor)
                                Text(
                                    text = label,
                                    kind = CouiTextKind.ItemSummary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.qs_edit_hint),
                    kind = CouiTextKind.ItemSummary,
                    color = CouiTheme.colorSchemeFor(dark).onSurfaceVariant,
                )
            }
        }
    }
}
