package com.itos.coui.picker

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 农历日期。
 *
 * [month] 为 1 到 12，[isLeap] 表示该月是当年的闰月。
 */
@Immutable
data class CouiLunarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val isLeap: Boolean = false,
)

/**
 * 公历与农历互转。
 *
 * 支持 1900 年到 2100 年，以 1900 年 1 月 31 日（农历 1900 年正月初一）为基准。
 */
object CouiLunarCalendar {

    /** 支持的最早公历年份。 */
    const val StartYear: Int = 1900

    /** 支持的最晚公历年份。 */
    const val EndYear: Int = 2100

    private val BaseDate: LocalDate = LocalDate.of(1900, 1, 31)

    private val lunarInfo: IntArray = intArrayOf(
        19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422,
        42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216,
        27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 92821, 23248, 11104,
        100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938,
        43344, 46423, 27808, 46416, 86869, 19872, 42416, 83315, 21168, 43432, 59728, 27296,
        44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925,
        19152, 42192, 54484, 53840, 54616, 46400, 46752, 103846, 38320, 18864, 43380, 42160,
        45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480,
        23232, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680,
        37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312,
        31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371,
        38608, 19195, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864,
        42359, 42160, 43600, 111189, 27936, 44448, 84835, 37744, 18936, 18800, 25776, 92326,
        59984, 27424, 108228, 43744, 37600, 53987, 51552, 54615, 54432, 55888, 23893, 22176,
        42704, 21972, 21200, 43448, 43344, 46240, 46758, 44368, 21920, 43940, 42416, 21168,
        45683, 26928, 29495, 27296, 44368, 84821, 19296, 42352, 21732, 53600, 59752, 54560,
        55968, 92838, 22224, 19168, 43476, 41680, 53584, 62034, 54560,
    )

    private val chineseNumbers = arrayOf(
        "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二",
    )

    /**
     * 返回公历 [date] 对应的农历日期，超出支持范围或早于基准日时返回 `null`。
     */
    fun solarToLunar(date: LocalDate): CouiLunarDate? {
        var offset = ChronoUnit.DAYS.between(BaseDate, date).toInt()
        if (offset < 0) return null
        var year = StartYear
        while (year <= EndYear) {
            val days = daysOfLunarYear(year)
            if (offset < days) break
            offset -= days
            year++
        }
        if (year > EndYear) return null
        val leap = leapMonth(year)
        var month = 1
        var isLeap = false
        while (month <= 12) {
            val days = if (isLeap) daysOfLeapMonth(year) else daysOfMonth(year, month)
            if (offset < days) break
            offset -= days
            if (leap > 0 && month == leap && !isLeap) {
                isLeap = true
            } else {
                if (isLeap) isLeap = false
                month++
            }
        }
        return CouiLunarDate(year = year, month = month, day = offset + 1, isLeap = isLeap)
    }

    /**
     * 返回农历 [lunar] 对应的公历日期，超出支持范围或该年没有此闰月时返回 `null`。
     */
    fun lunarToSolar(lunar: CouiLunarDate): LocalDate? {
        if (lunar.year !in StartYear..EndYear) return null
        if (lunar.month !in 1..12) return null
        if (lunar.day !in 1..30) return null
        if (lunar.isLeap && leapMonth(lunar.year) != lunar.month) return null
        var days = lunar.day - 1
        for (year in StartYear until lunar.year) {
            days += daysOfLunarYear(year)
        }
        for (month in 1 until lunar.month) {
            days += daysOfMonth(lunar.year, month)
        }
        val leap = leapMonth(lunar.year)
        if (leap in 1 until lunar.month) {
            days += daysOfLeapMonth(lunar.year)
        }
        if (lunar.isLeap) {
            days += daysOfMonth(lunar.year, lunar.month)
        }
        return BaseDate.plusDays(days.toLong())
    }

    /**
     * 返回 [year] 年的闰月月份，没有闰月时为 0。
     */
    fun leapMonth(year: Int): Int {
        if (year !in StartYear..EndYear) return 0
        return lunarInfo[year - StartYear] and 0xF
    }

    /**
     * 返回农历 [year] 年 [month] 月的天数（29 或 30）。
     *
     * [isLeap] 为真时取该月闰月的天数。
     */
    fun daysOfMonth(year: Int, month: Int, isLeap: Boolean = false): Int {
        if (year !in StartYear..EndYear) return 30
        if (month !in 1..12) return 30
        if (isLeap) return daysOfLeapMonth(year)
        val info = lunarInfo[year - StartYear]
        return if ((0x10000 shr month) and info == 0) 29 else 30
    }

    /**
     * 返回农历 [year] 年闰月的天数，没有闰月时为 0。
     */
    fun daysOfLeapMonth(year: Int): Int {
        if (year !in StartYear..EndYear) return 0
        val info = lunarInfo[year - StartYear]
        if (info and 0xF == 0) return 0
        return if (info and 0x10000 != 0) 30 else 29
    }

    /**
     * 返回农历 [year] 年的总天数。
     */
    fun daysOfLunarYear(year: Int): Int {
        if (year !in StartYear..EndYear) return 0
        val info = lunarInfo[year - StartYear]
        var days = 348
        var bit = 0x8000
        while (bit > 0x8) {
            if (info and bit != 0) days++
            bit = bit shr 1
        }
        return days + daysOfLeapMonth(year)
    }

    /**
     * 返回农历日的中文名称，如「初一」「十五」「廿三」「三十」。
     */
    fun dayName(day: Int): String {
        if (day !in 1..30) return ""
        return when (day) {
            10 -> "初十"
            20 -> "二十"
            30 -> "三十"
            else -> {
                val tens = day / 10
                val unit = if (day % 10 == 0) 9 else day % 10 - 1
                arrayOf("初", "十", "廿", "卅")[tens] + chineseNumbers[unit]
            }
        }
    }

    /**
     * 返回农历月的名称，闰月以「闰」开头。
     */
    fun monthName(month: Int, isLeap: Boolean = false): String {
        if (month !in 1..12) return ""
        val name = chineseNumbers[month - 1] + "月"
        return if (isLeap) "闰$name" else name
    }

    /**
     * 返回完整的农历日期文案，如「2024年正月初一」。
     */
    fun dateLabel(lunar: CouiLunarDate): String =
        "${lunar.year}年${monthName(lunar.month, lunar.isLeap)}${dayName(lunar.day)}"
}
