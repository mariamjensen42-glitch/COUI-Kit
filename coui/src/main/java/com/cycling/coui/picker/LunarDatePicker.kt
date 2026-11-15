package com.cycling.coui.picker

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.R
import com.cycling.coui.theme.CouiTheme
import java.time.LocalDate

/**
 * 农历年月日三列滚轮，对外仍以公历 [LocalDate] 表示。
 *
 * 月列包含当年的闰月（以「闰」开头），日列按当月天数收窄，中文文案来自
 * [CouiLunarCalendar]。超出 1900 年 1 月 31 日到 2100 年 12 月 31 日时不渲染任何内容，
 * 宿主需要显示农历字段本身时用 [CouiLunarCalendar.solarToLunar]。
 */
@Composable
fun LunarDatePicker(
    modifier: Modifier = Modifier,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    yearLabel: String = stringResource(R.string.coui_picker_calendar_year),
    monthLabel: String = stringResource(R.string.coui_picker_calendar_month),
    dayLabel: String = stringResource(R.string.coui_picker_calendar_day),
    minDate: LocalDate = LocalDate.of(1900, 1, 31),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiWheelColors = CouiPickerDefaults.wheelColors(dark),
) {
    require(minDate <= maxDate)
    val lunar = CouiLunarCalendar.solarToLunar(value.coerceIn(minDate, maxDate)) ?: return
    var latest by remember { mutableStateOf(lunar) }
    SideEffect { if (latest != lunar) latest = lunar }
    fun submit(next: CouiLunarDate) {
        val solar = CouiLunarCalendar.lunarToSolar(next) ?: return
        onValueChange(solar.coerceIn(minDate, maxDate))
    }

    val months = remember(latest.year) {
        buildList {
            val leap = CouiLunarCalendar.leapMonth(latest.year)
            for (month in 1..12) {
                add(month to false)
                if (leap == month) add(month to true)
            }
        }
    }
    val monthIndex = months.indexOf(latest.month to latest.isLeap).coerceAtLeast(0)
    val monthDays = CouiLunarCalendar.daysOfMonth(latest.year, latest.month, latest.isLeap)
    Row(
        modifier = modifier.fillMaxWidth()
            .couiPickerSelectionLines(CouiTheme.colorSchemeFor(dark).outlineVariant)
            .padding(vertical = CouiPickerDefaults.padding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelPicker(
            modifier = Modifier.width(CouiPickerDefaults.dateYearWidth),
            value = latest.year,
            onValueChange = { year ->
                val leapAvailable = CouiLunarCalendar.leapMonth(year) == latest.month
                val isLeap = latest.isLeap && leapAvailable
                val days = CouiLunarCalendar.daysOfMonth(year, latest.month, isLeap)
                submit(latest.copy(year = year, isLeap = isLeap, day = latest.day.coerceAtMost(days)))
            },
            valueRange = CouiLunarCalendar.StartYear..CouiLunarCalendar.EndYear,
            label = yearLabel,
            dark = dark,
            enabled = enabled,
            colors = colors,
            formatter = { "${it}$yearLabel" },
        )
        WheelPicker(
            modifier = Modifier.width(CouiPickerDefaults.dateMonthWidth),
            value = monthIndex,
            onValueChange = { index ->
                val (month, isLeap) = months[index]
                val days = CouiLunarCalendar.daysOfMonth(latest.year, month, isLeap)
                submit(latest.copy(month = month, isLeap = isLeap, day = latest.day.coerceAtMost(days)))
            },
            valueRange = months.indices,
            label = monthLabel,
            dark = dark,
            enabled = enabled,
            colors = colors,
            formatter = { index -> CouiLunarCalendar.monthName(months[index].first, months[index].second) },
        )
        WheelPicker(
            modifier = Modifier.width(CouiPickerDefaults.columnWidth),
            value = latest.day,
            onValueChange = { submit(latest.copy(day = it)) },
            valueRange = 1..monthDays,
            label = dayLabel,
            dark = dark,
            enabled = enabled,
            colors = colors,
            formatter = { CouiLunarCalendar.dayName(it) },
        )
    }
}
