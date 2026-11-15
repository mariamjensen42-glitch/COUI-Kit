package com.cycling.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.button.Button
import com.cycling.coui.button.ButtonGroup
import com.cycling.coui.button.CouiButtonGroupItem
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.buttonBar.ButtonBar
import com.cycling.coui.buttonBar.CouiButtonBarItem
import com.cycling.coui.demo.ui.Icon
import com.cycling.coui.demo.ui.Text
import com.cycling.coui.icons.CouiIcons
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group

@Composable
internal fun ButtonSample(dark: Boolean) {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    val primaryLabel = stringResource(R.string.button_primary)
    val secondaryLabel = stringResource(R.string.button_secondary)
    val textLabel = stringResource(R.string.button_text)
    Group {
        DemoPreference(title = stringResource(R.string.button_clicks), value = clicks.toString())
        Divider()
        DemoSwitchPreference(title = stringResource(R.string.button_enable), checked = enabled,
            onCheckedChange = { enabled = it })
        Divider()
        DemoSwitchPreference(title = stringResource(R.string.button_loading), checked = loading,
            onCheckedChange = { loading = it })
    }
    Group(title = stringResource(R.string.button_variants)) {
        ButtonRow {
            Button(onClick = { clicks++ }, label = primaryLabel, style = CouiButtonStyle.Primary,
                enabled = enabled, loading = loading, dark = dark)
            Button(onClick = { clicks++ }, label = secondaryLabel, style = CouiButtonStyle.Secondary,
                enabled = enabled, dark = dark)
        }
        ButtonRow {
            Button(onClick = { clicks++ }, label = textLabel, style = CouiButtonStyle.Text,
                enabled = enabled, dark = dark)
            Button(onClick = { clicks++ }, label = secondaryLabel, style = CouiButtonStyle.Secondary,
                enabled = false, dark = dark)
        }
    }
    Group(title = stringResource(R.string.button_content)) {
        ButtonRow {
            Button(onClick = { clicks++ }, style = CouiButtonStyle.Primary, enabled = enabled, dark = dark) {
                Icon(CouiIcons.Filled.Add, null, Modifier.size(18.dp))
                Text(stringResource(R.string.button_with_icon))
            }
        }
    }
    Group(title = stringResource(R.string.button_desc_title)) {
        ButtonRow {
            Button(onClick = { clicks++ }, label = stringResource(R.string.button_desc_label),
                description = stringResource(R.string.button_desc_text), dark = dark, enabled = enabled)
        }
    }
    Group(title = stringResource(R.string.button_group_title)) {
        ButtonGroup(
            modifier = Modifier.padding(vertical = 8.dp),
            items = listOf(
                CouiButtonGroupItem(label = stringResource(R.string.button_group_cancel), onClick = { clicks++ }),
                CouiButtonGroupItem(label = stringResource(R.string.button_group_confirm), onClick = { clicks++ },
                    style = CouiButtonStyle.Primary),
            ),
            dark = dark,
        )
    }
}

/** 按钮条：两个短按钮横排，长文案自动转竖排。 */
@Composable
internal fun ButtonBarSample(dark: Boolean) {
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    Group(title = stringResource(R.string.buttonbar_two)) {
        ButtonBar(
            modifier = Modifier.padding(vertical = 8.dp),
            items = listOf(
                CouiButtonBarItem(text = stringResource(R.string.button_group_cancel), onClick = { clicks-- }),
                CouiButtonBarItem(text = stringResource(R.string.button_group_confirm), onClick = { clicks++ }),
            ),
            dark = dark,
        )
    }
    Group(title = stringResource(R.string.buttonbar_stack)) {
        ButtonBar(
            modifier = Modifier.padding(vertical = 8.dp),
            items = listOf(
                CouiButtonBarItem(text = stringResource(R.string.buttonbar_long_label), onClick = { clicks++ }),
                CouiButtonBarItem(text = stringResource(R.string.buttonbar_longer_label), onClick = { clicks++ },
                    style = CouiButtonStyle.Primary),
            ),
            dark = dark,
        )
    }
    Group {
        DemoPreference(title = stringResource(R.string.button_clicks), value = clicks.toString())
    }
}

@Composable
private fun ButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically, content = content)
}
