# Picker

## Overview
COUI 滚轮选择组件包。核心 `WheelPicker` 是受控数值滚轮，`CouiWheelMotion` 负责拖动、惯性与吸附的状态机；`DatePicker`、`LunarDatePicker`、`TimePicker`、`TimeLimitPicker` 在其上装配多列滚轮，`CalendarPicker` 另提供月历网格与年月日滚轮编辑器；`CouiLunarCalendar` 承载公历/农历换算，`CouiPickerDefaults`/`CouiCalendarDefaults` 提供尺寸与配色。

## Interface
- `WheelPicker` — public @Composable 受控数值滚轮：拖动、长按连发、键盘与语义进度，Canvas 原生绘制 `[WheelPicker.kt:WheelPicker]`
- `CouiWheelColors` / `CouiPickerDefaults` — public 滚轮双色与固定尺寸对象；含 `wheelColors` 工厂 `[WheelPicker.kt:CouiWheelColors]` `[WheelPicker.kt:CouiPickerDefaults]`
- `CouiWheelMotion` — internal 运动核心：持有 value/offset/busy/step，暴露 display/sync/reconcile/begin/beginDrag/drag/end/tap/releaseTap/settle/resize/reset/stop/dispose `[CouiWheelMotion.kt:CouiWheelMotion]`
- `couiPickerSelectionLines` — public Modifier 扩展，在绘制区中心上下各画一条选择线 `[CouiPickerSelectionLines.kt:couiPickerSelectionLines]`
- `DatePicker` — public 公历年月日三列滚轮 `[DatePicker.kt:DatePicker]`
- `LunarDatePicker` — public 农历年月日三列滚轮，对外以公历 LocalDate 表示 `[LunarDatePicker.kt:LunarDatePicker]`
- `CouiTime` / `TimePicker` — public 不可变时分数据类与时分滚轮 `[TimePicker.kt:CouiTime]` `[TimePicker.kt:TimePicker]`
- `TimeLimitPicker` — public 带小时/分钟上限的时分选择 `[TimeLimitPicker.kt:TimeLimitPicker]`
- `CalendarPicker` / `CouiCalendarDefaults` / `CouiCalendarColors` — public 月历选择器、默认值与颜色槽 `[CalendarPicker.kt:CalendarPicker]` `[CouiCalendarDefaults.kt:CouiCalendarDefaults]` `[CouiCalendarDefaults.kt:CouiCalendarColors]`
- `CouiCalendarPageEasing` / `CouiCalendarExitEasing` / `CouiCalendarFlingBehavior` / `CouiCalendarSelectionMotion` — internal 月历动画常量与选中运动 owner `[CouiCalendarMotion.kt:CouiCalendarPageEasing]` `[CouiCalendarMotion.kt:CouiCalendarExitEasing]` `[CouiCalendarMotion.kt:CouiCalendarFlingBehavior]` `[CouiCalendarMotion.kt:CouiCalendarSelectionMotion]`
- `CouiLunarDate` / `CouiLunarCalendar` — public 农历日期与互转单例；StartYear=1900、EndYear=2100，含 solarToLunar/lunarToSolar/leapMonth/daysOfMonth/daysOfLeapMonth/daysOfLunarYear/dayName/monthName/dateLabel `[CouiLunarCalendar.kt:CouiLunarDate]` `[CouiLunarCalendar.kt:CouiLunarCalendar]`

## Invariants
- WheelPicker require 非空 valueRange 且 value 在范围内、textSize/focusTextSize>0、repeatIntervalMillis>0、diffusion>=0 `[WheelPicker.kt:WheelPicker]`
- 每次组合后 SideEffect 以 sync(value) 对齐；enabled=false 时 reset(value)；motion.onChange 转发最新 onValueChange 并触发触感 `[WheelPicker.kt:WheelPicker]`
- reconcile 仅在 !disposed && !busy && !touching 且 value 不同时回写宿主值并清零 offset `[CouiWheelMotion.kt:reconcile]`
- wrap 生效条件为调用方传入 wrap 且 count>3；display(relative) 必须常量时间完成，不能在 draw 中遍历 IntRange `[CouiWheelMotion.kt:CouiWheelMotion]` `[CouiWheelMotion.kt:display]`
- end 仅在 |velocity|>750 时走惯性（速度夹 ±5000 后乘 1.6，时长乘 1.5），否则 settle；settle 以 -offset 取整为目标并可能补一个 pitch `[CouiWheelMotion.kt:end]` `[CouiWheelMotion.kt:settle]`
- resize 先 reset(controlled) 清空旧像素位移再切换 step，不触发业务回调 `[CouiWheelMotion.kt:resize]`
- 绘制只遍历 relative -2..2 并经 motion.display 映射；超宽文本改左对齐并保留开头 `[WheelPicker.kt:WheelPicker]`
- setProgress 语义先 motion.stop()，再经 callback 直接提交夹取后的目标，不经过 motion.onChange `[WheelPicker.kt:WheelPicker]`
- 指针位置在上/下 1/3 决定方向 -1/1 并长按连发；拖动超 touchSlop 后取消连发并 beginDrag，UP/CANCEL 按手势时序 end/releaseTap `[WheelPicker.kt:WheelPicker]`
- DatePicker require(minDate<=maxDate)，value 与每次提交均 coerceIn；显示基于 current、提交基于 latest；月/日列仅整范围开放时 wrap `[DatePicker.kt:DatePicker]`
- LunarDatePicker require(minDate<=maxDate)，默认 1900-01-31..2100-12-31；solarToLunar 返回 null 时整个 Composable 不渲染 `[LunarDatePicker.kt:LunarDatePicker]`
- 农历月列表按 1..12 排列且闰月紧跟同号常规月；年/月切换仅在同月存在闰月时保留 isLeap，并按当月天数收窄 day `[LunarDatePicker.kt:LunarDatePicker]`
- TimeLimitPicker require maxHour 0..23、maxMinute 0..59、minuteStep 1..60；初始值与每次提交经 clampToLimit，分钟按 minuteStep 向下取整 `[TimeLimitPicker.kt:TimeLimitPicker]` `[TimeLimitPicker.kt:clampToLimit]`
- TimePicker require minuteStep 1..60、dateCount>0、date 非空时 onDateChange 非空；CouiTime require hour 0..23、minute 0..59 `[TimePicker.kt:TimePicker]` `[TimePicker.kt:CouiTime]`
- CalendarPicker require(minDate<=maxDate && value in range)；翻月箭头只改 headerPage 与 pager，不提交 value `[CalendarPicker.kt:CalendarPicker]` `[CalendarPicker.kt:CalendarArrow]`
- 日格 selected/old 仅在 inMonth 成立；颜色优先级为 selected > !inMonth > today > withinRange > 范围外；范围外禁用 `[CalendarPicker.kt:CalendarDayCell]`
- 编辑器所有提交经 coerceIn(minDate,maxDate)；滚轮顺序取 getBestDateTimePattern 中 y/M/d 顺序，RTL 下反转并强制 Ltr `[CalendarPicker.kt:CalendarDateEditor]`
- CouiCalendarSelectionMotion.update 以 generation 递增并 cancel 旧 job，状态在同一 Snapshot.withMutableSnapshot 提交；dispose 取消协程 `[CouiCalendarMotion.kt:CouiCalendarSelectionMotion.update]`
- 公历转农历以 1900-01-31 为基准，offset<0 或超 EndYear 返回 null；lunarInfo 必须覆盖 1900..2100 且查询前由 StartYear..EndYear 守卫 `[CouiLunarCalendar.kt:solarToLunar]` `[CouiLunarCalendar.kt:lunarInfo]`

## Side Effects
- android.widget.Scroller + PathInterpolator 产生动画，CoroutineScope.launch 与 withFrameNanos 逐帧结算 `[CouiWheelMotion.kt:animate]`
- 触感：LocalView.performHapticFeedback 按 308、302、CLOCK_TICK 顺序回退 `[WheelPicker.kt:WheelPicker]`
- android.graphics.Paint/Typeface/nativeCanvas 直接绘制文本；Offscreen + BlendMode.DstIn 垂直渐变遮罩 `[WheelPicker.kt:WheelPicker]`
- pointerInput 手势（awaitEachGesture/VelocityTracker/isCouiTouchCancelled）、键盘 onKeyEvent 与 progressSemantics 无障碍语义 `[WheelPicker.kt:WheelPicker]`
- stringResource 读取 coui_picker_calendar_year/month/day；getBestDateTimePattern、DateFormatSymbols、DateFormat.is24HourFormat 读取系统 locale 与时制 `[DatePicker.kt:DatePicker]` `[TimeLimitPicker.kt:TimeLimitPicker]` `[TimePicker.kt:TimePicker]`
- LocalDate.now() 作为 today/startDate 默认值；java.time API 执行公历/农历换算 `[CalendarPicker.kt:CalendarPicker]` `[TimePicker.kt:TimePicker]` `[CouiLunarCalendar.kt:solarToLunar]`
- CompositionLocalProvider 提供 LocalLayoutDirection=Ltr 包裹滚轮行 `[CalendarPicker.kt:CalendarDateEditor]` `[TimePicker.kt:TimePicker]`
- Modifier.drawBehind 画选择线；CouiLunarCalendar 单例持有年历表常量 `[CouiPickerSelectionLines.kt:couiPickerSelectionLines]` `[CouiLunarCalendar.kt:lunarInfo]`
- DisposableEffect 离开组合时调用 motion.dispose() `[WheelPicker.kt:WheelPicker]`

## Verification
- Run: `./gradlew :coui:lintDebug`
