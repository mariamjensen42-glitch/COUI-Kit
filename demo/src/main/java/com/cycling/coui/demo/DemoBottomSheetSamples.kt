package com.cycling.coui.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cycling.coui.bottomsheet.BottomSheet
import com.cycling.coui.layout.Divider
import com.cycling.coui.layout.Group
import com.cycling.coui.layout.Item
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/** 受控可见性：触发项置 true，遮罩、返回键与选项点击都只把宿主状态置 false。 */
@Composable
internal fun BottomSheetSample(dark: Boolean) {
    var visible by rememberSaveable { mutableStateOf(false) }
    val options = listOf(R.string.bottomsheet_option_first, R.string.bottomsheet_option_second,
        R.string.bottomsheet_option_third)
    Group {
        DemoPreference(title = stringResource(R.string.bottomsheet_open), onClick = { visible = true })
    }
    BottomSheet(visible = visible, onDismissRequest = { visible = false }, dark = dark) {
        Text(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 8.dp),
            text = stringResource(R.string.bottomsheet_header), kind = CouiTextKind.TopBar,
            color = CouiTheme.colorSchemeFor(dark).onSurface, textAlign = TextAlign.Start)
        Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
            options.forEachIndexed { index, label ->
                Item(onClick = { visible = false }) {
                    Text(text = stringResource(label), kind = CouiTextKind.ItemTitle)
                }
                if (index < options.lastIndex) Divider(startIndent = 0.dp, endIndent = 0.dp)
            }
        }
    }
}
