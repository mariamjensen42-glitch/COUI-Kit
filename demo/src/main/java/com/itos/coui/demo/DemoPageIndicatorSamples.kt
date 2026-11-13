package com.itos.coui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itos.coui.layout.Divider
import com.itos.coui.layout.Group
import com.itos.coui.pageindicator.PageIndicator
import kotlin.math.roundToInt

@Composable
internal fun PageIndicatorSample(dark: Boolean) {
    var count by rememberSaveable { mutableIntStateOf(5) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    Group {
        DemoSliderPreference(title = stringResource(R.string.pageindicator_count), value = count.toFloat(),
            valueRange = 3f..5f, steps = 1, valueText = count.toString(),
            onValueChange = { value ->
                count = value.roundToInt()
                selected = selected.coerceAtMost(count - 1)
            })
        Divider()
        DemoSliderPreference(title = stringResource(R.string.pageindicator_selected), value = selected.toFloat(),
            valueRange = 0f..(count - 1).toFloat(), steps = (count - 2).coerceAtLeast(0),
            valueText = "${selected + 1}/$count",
            onValueChange = { selected = it.roundToInt() })
    }
    Group(title = stringResource(R.string.pageindicator_example)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            // 固定三点与跟随点数滑杆的一组，共用同一个当前页。
            PageIndicator(count = 3, selectedIndex = selected.coerceAtMost(2), dark = dark)
            PageIndicator(count = count, selectedIndex = selected, dark = dark)
        }
    }
    // 可点击示例：7 个点，点圆点直接跳页；超过 6 个点时窗口随当前页滑动。
    var clicked by rememberSaveable { mutableIntStateOf(0) }
    Group(title = stringResource(R.string.pageindicator_click_example)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            PageIndicator(count = 7, selectedIndex = clicked, dark = dark, onDotClick = { clicked = it })
        }
    }
}
