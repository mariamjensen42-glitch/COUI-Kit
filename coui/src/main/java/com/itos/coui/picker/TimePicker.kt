package com.itos.coui.picker

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.itos.coui.R
import com.itos.coui.theme.CouiTheme
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 时与分；秒由调用方按需补充。 */
@Immutable
data class CouiTime(val hour: Int, val minute: Int) {
    init {
        require(hour in 0..23) { "hour must be in 0..23" }
        require(minute in 0..59) { "minute must be in 0..59" }
    }
}

/**
 * 时分滚轮，12 小时制时附带 AM/PM 列，列顺序跟随系统 locale 的时制模式。
 *
 * [minuteStep] 让分钟按固定步长取值，[date] 非空时在最前或最后追加日期列。
 * 日期列从 [startDate] 起连续 [dateCount] 天，首项显示 [todayLabel]。
 */
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    value: CouiTime,
    onValueChange: (CouiTime) -> Unit,
    hourLabel: String = stringResource(R.string.coui_picker_hour),
    minuteLabel: String = stringResource(R.string.coui_picker_minute),
    is24Hour: Boolean = DateFormat.is24HourFormat(LocalContext.current),
    minuteStep: Int = 1,
    date: LocalDate? = null,
    onDateChange: ((LocalDate) -> Unit)? = null,
    startDate: LocalDate = LocalDate.now(),
    dateCount: Int = CouiPickerDefaults.timeDateCount,
    dateLabel: String = stringResource(R.string.coui_picker_date),
    todayLabel: String = stringResource(R.string.coui_picker_today),
    enabled: Boolean = true,
    fillColumns: Boolean = false,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiWheelColors = CouiPickerDefaults.wheelColors(dark),
) {
    require(minuteStep in 1..60) { "minuteStep must be in 1..60" }
    require(dateCount > 0) { "dateCount must be positive" }
    require(date == null || onDateChange != null) { "onDateChange is required when date is set" }
    val locale = LocalConfiguration.current.locales[0]
    var latest by remember { mutableStateOf(value) }
    SideEffect { if (latest != value) latest = value }
    fun submit(next: CouiTime) {
        latest = next
        onValueChange(next)
    }

    val periods = remember(locale) { DateFormatSymbols(locale).amPmStrings }
    val periodFirst = remember(locale) { DateFormat.getBestDateTimePattern(locale, "hm").startsWith("a") }
    val minuteCount = 59 / minuteStep
    val minuteValues = 0..minuteCount
    val dateFormatter = remember(locale, todayLabel) { dateColumnFormatter(locale, todayLabel) }
    val period: @Composable () -> Unit = {
        WheelPicker(
            modifier = Modifier.width(CouiPickerDefaults.columnWidth),
            value = latest.hour / 12,
            onValueChange = { submit(latest.copy(hour = latest.hour % 12 + it * 12)) },
            valueRange = 0..1,
            label = hourLabel,
            dark = dark,
            enabled = enabled,
            wrap = false,
            textSize = 18.dp,
            colors = colors,
            formatter = { periods[it] },
        )
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier.fillMaxWidth()
                .couiPickerSelectionLines(CouiTheme.colorSchemeFor(dark).outlineVariant)
                .padding(vertical = CouiPickerDefaults.padding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!is24Hour && periodFirst) period()
            if (date != null) {
                WheelPicker(
                    modifier = pickerColumn(fillColumns),
                    value = date.toEpochDay().let { day -> (day - startDate.toEpochDay()).toInt().coerceIn(0, dateCount - 1) },
                    onValueChange = { onDateChange?.invoke(startDate.plusDays(it.toLong())) },
                    valueRange = 0 until dateCount,
                    label = dateLabel,
                    dark = dark,
                    enabled = enabled,
                    colors = colors,
                    formatter = { dateFormatter(startDate.plusDays(it.toLong())) },
                )
            }
            WheelPicker(
                modifier = pickerColumn(fillColumns),
                value = if (is24Hour) latest.hour else ((latest.hour + 11) % 12 + 1),
                onValueChange = {
                    submit(
                        latest.copy(
                            hour = if (is24Hour) it else it % 12 + latest.hour / 12 * 12,
                        ),
                    )
                },
                valueRange = if (is24Hour) 0..23 else 1..12,
                label = hourLabel,
                dark = dark,
                enabled = enabled,
                colors = colors,
                formatter = { String.format(locale, "%d", it) },
            )
            WheelPicker(
                modifier = pickerColumn(fillColumns),
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
            if (!is24Hour && !periodFirst) period()
        }
    }
}

private fun RowScope.pickerColumn(fillColumns: Boolean): Modifier =
    if (fillColumns) Modifier.weight(1f) else Modifier.width(CouiPickerDefaults.columnWidth)

/**
 * 日期列的文案：今天用 [todayLabel]，中文为「M月d日 周X」，其他语言为「周X, MMM d」。
 */
private fun dateColumnFormatter(locale: Locale, todayLabel: String): (LocalDate) -> String {
    val weekday = DateTimeFormatter.ofPattern("EEE", locale)
    val patterns = if (locale.language == "zh") {
        DateTimeFormatter.ofPattern("M月d日", locale)
    } else {
        DateTimeFormatter.ofPattern("MMM d", locale)
    }
    return { date ->
        if (date == LocalDate.now()) {
            todayLabel
        } else {
            val body = patterns.format(date)
            val week = weekday.format(date)
            if (locale.language == "zh") "$body $week" else "$week, $body"
        }
    }
}
