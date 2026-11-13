package com.itos.coui.demo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.badge.Badge
import com.itos.coui.badge.BadgedBox
import com.itos.coui.button.Button
import com.itos.coui.button.CouiButtonStyle
import com.itos.coui.demo.ui.Icon
import com.itos.coui.demo.ui.Text
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.tooltip.Tooltip
import kotlin.math.roundToInt

@Composable
internal fun BadgeSample(dark: Boolean) {
    var count by rememberSaveable { mutableIntStateOf(3) }
    var tipVisible by rememberSaveable { mutableStateOf(false) }
    Group {
        DemoSliderPreference(title = stringResource(R.string.badge_count), value = count.toFloat(),
            onValueChange = { count = it.roundToInt() }, valueText = count.toString(),
            valueRange = 0f..120f, steps = 119, showTickMarks = false)
    }
    Group(title = stringResource(R.string.badge_examples)) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            Badge()
            Badge(count = count)
            BadgedBox(badge = { Badge(count = count) }) {
                Icon(CouiIcons.Filled.Battery, null, Modifier.size(24.dp))
            }
            BadgedBox(badge = { Badge() }) {
                Icon(CouiIcons.Filled.Album, null, Modifier.size(24.dp))
            }
        }
    }
    Group(title = stringResource(R.string.tooltip_examples)) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // 长按由宿主装配，组件本身不安装手势。
            Tooltip(visible = tipVisible, onDismissRequest = { tipVisible = false },
                text = stringResource(R.string.tooltip_text)) {
                Box(Modifier.size(40.dp).pointerInput(Unit) {
                    detectTapGestures(onLongPress = { tipVisible = true })
                }, contentAlignment = Alignment.Center) {
                    Icon(CouiIcons.Filled.Rings, null, Modifier.size(24.dp))
                }
            }
            Text(stringResource(R.string.tooltip_hint), Modifier.weight(1f))
        }
        Divider()
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { tipVisible = true }, label = stringResource(R.string.tooltip_show),
                style = CouiButtonStyle.Primary, dark = dark)
            Button(onClick = { tipVisible = false }, label = stringResource(R.string.tooltip_hide),
                style = CouiButtonStyle.Secondary, dark = dark)
        }
    }
}
