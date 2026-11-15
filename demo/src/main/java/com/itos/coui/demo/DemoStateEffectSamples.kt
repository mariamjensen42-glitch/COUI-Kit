package com.itos.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.state.CouiStateEffectDefaults
import com.itos.coui.state.StateEffectSurface
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text
import com.itos.coui.theme.CouiTheme

/** 状态效果表面：悬停、焦点、按压蒙层与焦点描边。 */
@Composable
internal fun StateEffectSample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val colors = CouiStateEffectDefaults.colors(dark)
    var selected by rememberSaveable { mutableStateOf(0) }
    var outline by rememberSaveable { mutableStateOf(true) }
    var scale by rememberSaveable { mutableStateOf(true) }
    var enabled by rememberSaveable { mutableStateOf(true) }

    Column {
        Group(title = stringResource(R.string.state_effect_title)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(0, 1, 2).forEach { index ->
                        StateEffectSurface(
                            modifier = Modifier.weight(1f),
                            onClick = { selected = index },
                            selected = selected == index,
                            containerColor = if (selected == index) scheme.container else scheme.card,
                            focusOutline = outline,
                            scaleFeedback = scale,
                            enabled = enabled,
                            dark = dark,
                            colors = colors,
                        ) {
                            Text(
                                text = stringResource(R.string.state_effect_item, index + 1),
                                kind = CouiTextKind.Body,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.state_effect_hint),
                    kind = CouiTextKind.ItemSummary,
                    color = scheme.onSurfaceVariant,
                )
                Divider()
                DemoSwitchPreference(
                    title = stringResource(R.string.state_effect_outline),
                    checked = outline,
                    onCheckedChange = { outline = it },
                )
                DemoSwitchPreference(
                    title = stringResource(R.string.state_effect_scale),
                    checked = scale,
                    onCheckedChange = { scale = it },
                )
                DemoSwitchPreference(
                    title = stringResource(R.string.state_effect_enabled),
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                )
            }
        }
    }
}
