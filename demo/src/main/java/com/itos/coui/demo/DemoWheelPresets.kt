package com.itos.coui.demo

import com.itos.coui.picker.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itos.coui.theme.CouiTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CountdownHeight = 228.dp
private val CountdownMargin = 30.dp
private val CountdownSeparatorWidth = 30.dp

/** 时钟标准倒计时：三行、三列、38dp 数字、30dp 独立冒号及渐隐。 */
@Composable
internal fun DemoCountdownPicker(
    modifier: Modifier = Modifier,
    value: DemoDuration,
    onValueChange: (DemoDuration) -> Unit,
    hourLabel: String,
    minuteLabel: String,
    secondLabel: String,
    enabled: Boolean = true,
) {
    val locale = LocalConfiguration.current.locales[0]
    var latest by remember { mutableStateOf(value) }
    SideEffect { if (latest != value) latest = value }
    fun submit(next: DemoDuration) { latest = next; onValueChange(next) }
    val foreground = CouiTheme.colorScheme.onSurface.copy(alpha = .9f)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier.fillMaxWidth().padding(horizontal = CountdownMargin),
            verticalAlignment = Alignment.CenterVertically) {
            val values = listOf(value.hour, value.minute, value.second)
            val labels = listOf(hourLabel, minuteLabel, secondLabel)
            values.forEachIndexed { index, number ->
                WheelPicker(Modifier.weight(1f).height(CountdownHeight), number, { next -> submit(when (index) {
                    0 -> latest.copy(hour = next); 1 -> latest.copy(minute = next); else -> latest.copy(second = next)
                }) }, if (index == 0) 0..23 else 0..59, labels[index], enabled = enabled,
                    textSize = 38.dp, focusTextSize = 38.dp,
                    diffusion = 4.dp, verticalFading = true, clockTypeface = true,
                    repeatIntervalMillis = if (index == 0) 300 else 100,
                    formatter = { String.format(locale, "%02d", it) })
                if (index < 2) BasicText(":", Modifier.width(CountdownSeparatorWidth).padding(bottom = 4.dp),
                    style = TextStyle(fontSize = 38.sp, fontWeight = FontWeight.Bold,
                        color = foreground.copy(alpha = foreground.alpha * if (enabled) 1f else .5f), textAlign = TextAlign.Center))
            }
        }
    }
}

/** 新建倒计时的 MiniTimePickerView：135dp 三列，不使用主页的大号数字与冒号。 */
@Composable
internal fun DemoTimerEditorPicker(
    modifier: Modifier = Modifier,
    value: DemoDuration,
    onValueChange: (DemoDuration) -> Unit,
    labels: List<String>,
) {
    val locale = LocalConfiguration.current.locales[0]
    var latest by remember { mutableStateOf(value) }
    SideEffect { if (latest != value) latest = value }
    fun submit(next: DemoDuration) { latest = next; onValueChange(next) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier.fillMaxWidth().couiPickerSelectionLines(CouiTheme.colorScheme.outlineVariant, 0.dp),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            listOf(value.hour, value.minute, value.second).forEachIndexed { index, number ->
                WheelPicker(Modifier.width(58.dp), number, { next -> submit(when (index) {
                    0 -> latest.copy(hour = next); 1 -> latest.copy(minute = next); else -> latest.copy(second = next)
                }) }, if (index == 0) 0..23 else 0..59, labels[index],
                    repeatIntervalMillis = if (index == 0) 300 else 100,
                    formatter = { String.format(locale, "%02d", it) })
            }
        }
    }
}
