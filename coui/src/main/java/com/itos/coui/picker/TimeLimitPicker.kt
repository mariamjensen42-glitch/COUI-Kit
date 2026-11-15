package com.itos.coui.picker

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.R
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme
import java.text.DateFormatSymbols

/**
 * 带上限的时分选择：小时最多到 [maxHour]，[maxHour] 那一小时的分钟最多到 [maxMinute]。
 *
 * 12 小时制时机分之间出现一个上午/下午按钮，点击按半天切换；
 * [minuteStep] 让分钟按固定步长取值。
 */
@Composable
fun TimeLimitPicker(
    modifier: Modifier = Modifier,
    value: CouiTime,
    onValueChange: (CouiTime) -> Unit,
    hourLabel: String = stringResource(R.string.coui_picker_hour),
    minuteLabel: String = stringResource(R.string.coui_picker_minute),
    is24Hour: Boolean = DateFormat.is24HourFormat(LocalContext.current),
    maxHour: Int = 23,
    maxMinute: Int = 59,
    minuteStep: Int = 1,
    amPmDescription: String = stringResource(R.string.coui_picker_time_limit_am_pm),
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiWheelColors = CouiPickerDefaults.wheelColors(dark),
) {
    require(maxHour in 0..23) { "maxHour must be in 0..23" }
    require(maxMinute in 0..59) { "maxMinute must be in 0..59" }
    require(minuteStep in 1..60) { "minuteStep must be in 1..60" }
    val locale = LocalConfiguration.current.locales[0]
    val initial = value.clampToLimit(maxHour, maxMinute, minuteStep)
    var latest by remember { mutableStateOf(initial) }
    SideEffect { if (latest != initial) latest = initial }
    fun submit(next: CouiTime) {
        val capped = next.clampToLimit(maxHour, maxMinute, minuteStep)
        latest = capped
        onValueChange(capped)
    }

    val periods = remember(locale) { DateFormatSymbols(locale).amPmStrings }
    val minuteValues = 0..(minuteLimit(latest.hour, maxHour, maxMinute) / minuteStep)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier.fillMaxWidth()
                .couiPickerSelectionLines(CouiTheme.colorSchemeFor(dark).outlineVariant)
                .padding(vertical = CouiPickerDefaults.padding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WheelPicker(
                modifier = Modifier.width(CouiPickerDefaults.columnWidth),
                value = if (is24Hour) latest.hour else ((latest.hour + 11) % 12 + 1),
                onValueChange = {
                    val hour = if (is24Hour) it else it % 12 + latest.hour / 12 * 12
                    submit(latest.copy(hour = hour.coerceAtMost(maxHour)))
                },
                valueRange = if (is24Hour) 0..maxHour else 1..12,
                label = hourLabel,
                dark = dark,
                enabled = enabled,
                colors = colors,
                formatter = { String.format(locale, "%d", it) },
            )
            if (!is24Hour) {
                AmPmButton(
                    label = periods[latest.hour / 12],
                    description = amPmDescription,
                    enabled = enabled,
                    dark = dark,
                    onClick = {
                        val next = (latest.hour + 12) % 24
                        submit(latest.copy(hour = next.coerceAtMost(maxHour)))
                    },
                )
            }
            WheelPicker(
                modifier = Modifier.width(CouiPickerDefaults.columnWidth),
                value = latest.minute / minuteStep,
                onValueChange = { submit(latest.copy(minute = it * minuteStep)) },
                valueRange = minuteValues,
                label = minuteLabel,
                dark = dark,
                enabled = enabled,
                repeatIntervalMillis = 100,
                colors = colors,
                formatter = { String.format(locale, "%02d", it * minuteStep) },
            )
        }
    }
}

/**
 * [hour] 那一小时允许的最大分钟：到达 [maxHour] 时取 [maxMinute]，否则取 59。
 */
private fun minuteLimit(hour: Int, maxHour: Int, maxMinute: Int): Int =
    if (hour >= maxHour) maxMinute else 59

private fun CouiTime.clampToLimit(maxHour: Int, maxMinute: Int, minuteStep: Int): CouiTime {
    val hour = this.hour.coerceIn(0, maxHour)
    val limit = minuteLimit(hour, maxHour, maxMinute)
    val minute = (this.minute.coerceIn(0, limit) / minuteStep) * minuteStep
    return CouiTime(hour, minute)
}

@Composable
private fun AmPmButton(
    label: String,
    description: String,
    enabled: Boolean,
    dark: Boolean,
    onClick: () -> Unit,
) {
    val theme = CouiTheme.colorSchemeFor(dark)
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(CouiPickerDefaults.amPmButtonWidth, CouiPickerDefaults.amPmButtonHeight)
            .clip(CouiCapsuleShape)
            .background(theme.container)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CouiText(
            text = label,
            color = theme.onSurface.copy(alpha = if (enabled) 1f else 0.3f),
            fontSize = 14.sp,
        )
    }
}
