package com.cycling.coui.picker

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.R
import com.cycling.coui.theme.CouiTheme
import java.text.DateFormatSymbols
import java.time.LocalDate

/**
 * 年月日三列滚轮，列顺序跟随系统 locale 的日期模式。
 *
 * [value] 会被夹到 [minDate]..[maxDate]；年月改变后日期按当月天数收窄，
 * 选择范围达到整年或整月时对应列可循环。
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    yearLabel: String = stringResource(R.string.coui_picker_calendar_year),
    monthLabel: String = stringResource(R.string.coui_picker_calendar_month),
    dayLabel: String = stringResource(R.string.coui_picker_calendar_day),
    minDate: LocalDate = LocalDate.of(1900, 1, 1),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    showDay: Boolean = true,
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiWheelColors = CouiPickerDefaults.wheelColors(dark),
) {
    require(minDate <= maxDate)
    val current = value.coerceIn(minDate, maxDate)
    val locale = LocalConfiguration.current.locales[0]
    var latest by remember { mutableStateOf(current) }
    SideEffect { if (latest != current) latest = current }
    val order = remember(locale, showDay) {
        DateFormat.getBestDateTimePattern(locale, "yyyyMMMMdd")
            .filter { it in "yMd" }.toList().distinct().filter { showDay || it != 'd' }
    }
    val months = remember(locale) { DateFormatSymbols(locale).shortMonths }
    val chinese = locale.language == "zh"
    val minYear = minDate.year
    val maxYear = maxDate.year
    val minMonth = if (current.year == minDate.year) minDate.monthValue else 1
    val maxMonth = if (current.year == maxDate.year) maxDate.monthValue else 12
    val monthLength = current.lengthOfMonth()
    val minDay = if (current.year == minDate.year && current.monthValue == minDate.monthValue) {
        minDate.dayOfMonth
    } else {
        1
    }
    val maxDay = if (current.year == maxDate.year && current.monthValue == maxDate.monthValue) {
        minOf(maxDate.dayOfMonth, monthLength)
    } else {
        monthLength
    }
    fun submit(date: LocalDate) {
        val next = date.coerceIn(minDate, maxDate)
        latest = next
        onValueChange(next)
    }
    Row(
        modifier = modifier.fillMaxWidth()
            .couiPickerSelectionLines(CouiTheme.colorSchemeFor(dark).outlineVariant)
            .padding(vertical = CouiPickerDefaults.padding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        order.forEach { kind ->
            when (kind) {
                'y' -> WheelPicker(
                    modifier = Modifier.width(CouiPickerDefaults.dateYearWidth),
                    value = current.year,
                    onValueChange = { submit(latest.withYear(it)) },
                    valueRange = minYear..maxYear,
                    label = yearLabel,
                    dark = dark,
                    enabled = enabled,
                    colors = colors,
                    formatter = { if (chinese) "$it$yearLabel" else String.format(locale, "%d", it) },
                )

                'M' -> WheelPicker(
                    modifier = Modifier.width(CouiPickerDefaults.dateMonthWidth),
                    value = current.monthValue,
                    onValueChange = { submit(latest.withMonth(it)) },
                    valueRange = minMonth..maxMonth,
                    label = monthLabel,
                    dark = dark,
                    enabled = enabled,
                    wrap = minMonth == 1 && maxMonth == 12,
                    colors = colors,
                    formatter = { if (chinese) "$it$monthLabel" else months[it - 1] },
                )

                'd' -> WheelPicker(
                    modifier = Modifier.width(CouiPickerDefaults.columnWidth),
                    value = current.dayOfMonth,
                    onValueChange = { submit(latest.withDayOfMonth(it.coerceAtMost(latest.lengthOfMonth()))) },
                    valueRange = minDay..maxDay,
                    label = dayLabel,
                    dark = dark,
                    enabled = enabled,
                    wrap = minDay == 1 && maxDay == monthLength,
                    colors = colors,
                    formatter = { if (chinese) "$it$dayLabel" else String.format(locale, "%02d", it) },
                )
            }
        }
    }
}
