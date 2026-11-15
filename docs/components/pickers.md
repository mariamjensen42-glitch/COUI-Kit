# 📅选择器

库公开六种选择器：`WheelPicker`（整数滚轮）、`CalendarPicker`（月历）、`DatePicker`（年月日三列）、`TimePicker`（时分，可带日期列）、`TimeLimitPicker`（带上限的时分）和 `LunarDatePicker`（农历年月日），以及公历农历互转的 `CouiLunarCalendar`。它们都由页面提供当前值和更新回调，可直接嵌入页面，也可以放进现有的 `Dialog`。

滚轮是一列上下滚动的数字，中间为较大的当前值，上下显示相邻数字；单列本身没有时间冒号和整组选择线，多列组合时由父级共享绘制。月历包含月份标题、左右翻月按钮、七列日期网格和选中圆圈；网格首日跟随系统语言的周起始日，简体中文为周一。点击标题切换到内置的年/月/日三列滚轮，不需要单独创建三个 `WheelPicker`。

## 最小调用

滚轮需要当前整数、范围、回调和无障碍标签：

~~~kotlin
WheelPicker(
    value = minute,
    onValueChange = { minute = it },
    valueRange = 0..59,
    label = "分钟",
)
~~~

月历只需要当前日期和回调；年月日文案及按钮的无障碍说明由库按系统语言提供：

~~~kotlin
CalendarPicker(
    value = selectedDate,
    onValueChange = onDateChange,
)
~~~

上述变量由页面提供。`value` 是已接受的值；页面必须在 `onValueChange` 中更新它。所有选择器的 `dark` 和 `colors` 默认从 `CouiTheme` 解析，不需要额外主题依赖。主题规则见[外观与预设](../guide/appearance.md)。

## 日期三列

`DatePicker` 的列顺序跟随系统语言的日期模式，中文为「年月日」，其他语言可能是「月日年」或「日月年」：

~~~kotlin
var date by rememberSaveable { mutableLongStateOf(LocalDate.now().toEpochDay()) }

DatePicker(
    modifier = Modifier.fillMaxWidth(),
    value = LocalDate.ofEpochDay(date),
    onValueChange = { date = it.toEpochDay() },
)
~~~

中文列文为「2026年」「11月」「15日」，其他语言使用月份缩写和两位日。`showDay = false` 只选年月。

`value` 会被夹到 `minDate`..`maxDate`，宿主不必事先夹紧；年月改变后日期按当月天数收窄。可选范围覆盖整年或整月时，对应列的滚轮可循环。

## 时分两列

`TimePicker` 的受控值是 `CouiTime(hour, minute)`：

~~~kotlin
var time by rememberSaveable { mutableIntStateOf(9 * 3600 + 30 * 60) }

TimePicker(
    value = CouiTime(time / 3600, time / 60 % 60),
    onValueChange = { time = it.hour * 3600 + it.minute * 60 },
)
~~~

`is24Hour` 省略时取系统的 24 小时制设置；12 小时制追加一列 AM/PM，位置跟随系统语言的时制模式（AM/PM 在前或在后）。`minuteStep` 让分钟按固定步长取值，`minuteStep = 5` 时候选为 0、5、…、55。

`date` 非空时追加日期列，从 `startDate` 起连续 `dateCount` 天，首项显示「今天」（`todayLabel`），其余为「M月d日 周X」或「周X, MMM d」。日期列提交走 `onDateChange`，与时分的回调分开：

~~~kotlin
TimePicker(
    value = CouiTime(time / 3600, time / 60 % 60),
    onValueChange = { time = it.hour * 3600 + it.minute * 60 },
    date = date,
    onDateChange = { date = it },
    dateCount = 30,
)
~~~

`fillColumns = true` 让各列等分可用宽度，默认按 `CouiPickerDefaults.columnWidth` 固定宽度居中排列。

## 带上限的时分

`TimeLimitPicker` 在时分上加一个上限：小时最多到 `maxHour`，`maxHour` 那一小时的分钟最多到 `maxMinute`：

~~~kotlin
TimeLimitPicker(
    value = CouiTime(limit / 3600, limit / 60 % 60),
    onValueChange = { limit = it.hour * 3600 + it.minute * 60 },
    maxHour = 12,
    maxMinute = 30,
)
~~~

提交时按上限夹紧，因此上例不会出现 12:31 或 13:00。12 小时制时，时与分之间显示上午/下午按钮，点击按半天切换并同样受上限约束。

## 农历日期

`LunarDatePicker` 对外仍以 `LocalDate` 表示，滚动的是农历年月日：

~~~kotlin
LunarDatePicker(
    modifier = Modifier.fillMaxWidth(),
    value = LocalDate.ofEpochDay(date),
    onValueChange = { date = it.toEpochDay() },
)
~~~

月列包含当年的闰月，写作「闰二月」；日列写作「初一」到「三十」，天数按农历月为 29 或 30。支持范围是 1900-01-31 到 2100-12-31，日期超出范围时不渲染任何内容。

需要自己展示农历字段时用 `CouiLunarCalendar`：

~~~kotlin
val lunar = CouiLunarCalendar.solarToLunar(LocalDate.now())
val label = lunar?.let(CouiLunarCalendar::dateLabel)
val back = lunar?.let(CouiLunarCalendar::lunarToSolar)
~~~

`CouiLunarCalendar` 提供 `solarToLunar`、`lunarToSolar`、`leapMonth`、`daysOfMonth`、`monthName`、`dayName` 和 `dateLabel`，覆盖 `StartYear`(1900) 到 `EndYear`(2100)。`CouiLunarDate` 的 `isLeap` 表示闰月；当年没有该闰月或超出支持范围时 `lunarToSolar` 返回 `null`。

## 组合小时和分钟滚轮

需要自己排列列宽、冒号或配色时，可以直接用 `WheelPicker` 组合：

~~~kotlin
@Composable
fun TimeWheelExample() {
    var hour by rememberSaveable { mutableIntStateOf(9) }
    var minute by rememberSaveable { mutableIntStateOf(30) }

    Row {
        WheelPicker(
            modifier = Modifier.width(62.dp),
            value = hour,
            onValueChange = { hour = it },
            valueRange = 0..23,
            label = "小时",
            formatter = { "%02d".format(it) },
        )
        WheelPicker(
            modifier = Modifier.width(62.dp),
            value = minute,
            onValueChange = { minute = it },
            valueRange = 0..59,
            label = "分钟",
            repeatIntervalMillis = 100,
            formatter = { "%02d".format(it) },
        )
    }
}
~~~

`formatter` 只改变显示文字，不改变整数值。`label` 是页面必须提供的无障碍说明，不会显示在数字旁。`repeatIntervalMillis` 控制长按连续调整的间隔。

默认 `wrap = true`，但范围只有三个或更少数值时不会循环；需要固定边界时显式设置 `wrap = false`。例如 `0..59` 循环时，59 后面是 0，0 前面是 59；关闭循环后在两端停止。`wrap` 不会改变 `valueRange` 本身，也不会自动联动其他列。

## 显示月历和日期编辑器

月历支持翻月、点击日期，以及点击顶部日期标题切换到年/月/日滚轮：

~~~kotlin
@Composable
fun CalendarExample(modifier: Modifier = Modifier) {
    var epochDay by rememberSaveable {
        mutableLongStateOf(LocalDate.now().toEpochDay())
    }
    val selectedDate = LocalDate.ofEpochDay(epochDay)

    CalendarPicker(
        modifier = modifier.fillMaxWidth(),
        value = selectedDate,
        onValueChange = { epochDay = it.toEpochDay() },
    )
}
~~~

`LocalDate` 不直接放入 `rememberSaveable`；此例通过 epoch day 保存日期。库在 `coui` 模块内提供英文和简体中文默认文案，并随系统语言选择；如需要特殊措辞，可覆盖 `previousDescription` 等可选参数。无需把 Demo 的字符串资源复制到业务项目。

左右翻月箭头只改变浏览月份，不提交日期。点击具体日期或滚动日期编辑器才调用 `onValueChange`。顶部标题右侧的图标随模式切换：月历态向下，滚轮态向上。关闭编辑器后回到宿主已接受的日期。

## 设置日期范围

`DatePicker` 与 `CalendarPicker` 都接受 `minDate` 和 `maxDate`：

~~~kotlin
DatePicker(
    value = selectedDate,
    onValueChange = onDateChange,
    minDate = LocalDate.of(2026, 11, 1),
    maxDate = LocalDate.of(2026, 11, 30),
)
~~~

`minDate` 和 `maxDate` 都是 **`LocalDate`（年月日）**，不接受时分秒；省略时 `DatePicker` 默认 1900-01-01 到 2100-12-31，`CalendarPicker` 同默认值。`minDate` 不得晚于 `maxDate`。`DatePicker` 会把越界的 `value` 夹进范围；`CalendarPicker` 要求 `value` 已在范围内，否则抛出异常，动态收紧范围时宿主应先将已选日期夹紧。

`today` 仅决定“今天”的显示样式，不会自动更改 `value`；省略时取设备当前日期。需要允许选择未来 7 天时，可以写 `minDate = LocalDate.now()`、`maxDate = LocalDate.now().plusDays(7)`，并确保传入的 `value` 也在这个范围内。

## 在弹窗内选择日期

直接使用已有的 `Dialog` 和 `content` 槽位。弹窗持有草稿，用户确认后才保存：

~~~kotlin
@Composable
fun DatePickerDialog(
    visible: Boolean,
    value: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    var draftDay by rememberSaveable(value) {
        mutableLongStateOf(value.toEpochDay())
    }
    Dialog(
        onDismissRequest = onDismiss,
        title = "选择日期",
        confirmAction = CouiDialogAction("确定", {
            onConfirm(LocalDate.ofEpochDay(draftDay))
            onDismiss()
        }),
        dismissAction = CouiDialogAction("取消", onDismiss),
    ) {
        CalendarPicker(
            value = LocalDate.ofEpochDay(draftDay),
            onValueChange = { draftDay = it.toEpochDay() },
        )
    }
}
~~~

点击取消、外部区域或返回键时，示例不会提交草稿。需要主操作强调色、居中窗口或背景材质时，直接使用[弹窗文档](dialog.md)中的 `CouiDialogAction`、`placement` 和 `material`；选择器本身不创建窗口或背景采样源。

## 自定义滚轮外观

多列布局可使用 `CouiPickerDefaults` 的 `columnWidth`、`padding`、`dateYearWidth` 和 `dateMonthWidth` 预设。

高度通过 `Modifier.height()` 设置；省略时使用 `CouiPickerDefaults.height`。父级约束和 Modifier 顺序决定实际视口，绘制、命中和运动步长使用实测高度。`textSize`、`focusTextSize` 和 `diffusion` 使用 `Dp`，字号不再次按系统 `sp` 放大。下面沿用前面的 `minute` 状态：

~~~kotlin
WheelPicker(
    modifier = Modifier.height(160.dp),
    value = minute,
    onValueChange = { minute = it },
    valueRange = 0..59,
    label = "分钟",
    textSize = 16.dp,
    focusTextSize = 22.dp,
    diffusion = 4.dp,
    verticalFading = true,
    colors = CouiPickerDefaults.wheelColors().copy(
        focus = accent,
    ),
)
~~~

这里的 `accent` 由宿主提供。`verticalFading` 开启上下渐隐，额外使用离屏合成；无须渐隐时保留默认 `false`。`clockTypeface` 使用时钟专用字体配置，显示效果可能受设备字体支持情况影响。

`diffusion` 参与滚轮的运动步长和选中带，不能用外部 padding 替代。

## 整组选择线

时间或日期列需要贯穿整组的选择线时，在父级 Row 使用共享绘制：

~~~kotlin
Row(
    Modifier.fillMaxWidth()
        .couiPickerSelectionLines(dividerColor)
        .padding(vertical = CouiPickerDefaults.padding),
) {
    // 放入前述小时、分钟滚轮。
}
~~~

此片段位于 Compose 内容中，`dividerColor` 由宿主主题提供。选择线绘制在内容后方，不改变高度、裁剪和输入。对称的上下留白可放内侧，非对称留白放在选择线外侧。倒计时的大字与 diffusion 配置不适用这组标准选择线。`horizontalInset` 控制横向内缩，须为有限非负 Dp。

`DatePicker`、`TimePicker`、`TimeLimitPicker` 和 `LunarDatePicker` 已经内置这层选择线，内缩量与 `padding` 固定，不需要宿主再包一层。

## 颜色与主题

省略 `dark` 时使用 `CouiTheme.isDark`；省略 `colors` 时，滚轮通过 `CouiPickerDefaults.wheelColors(dark)`、月历通过 `CouiCalendarDefaults.colors(dark)` 创建颜色。显式传入的颜色优先。

`CouiWheelColors` 包含 `normal`（普通项）和 `focus`（选中项）；`CouiCalendarColors` 包含 `foreground`、`secondary`、`weekday`、`hint`、`highlight`、`divider` 和 `onAccent`。月历另有独立的 `accent` 参数，用于选中圆及强调色元素。自定义 `accent` 时，应确保 `onAccent` 文字与选中圆有足够对比度。

主题接入方式见[外观与预设](../guide/appearance.md)。

## 与原厂的差异

- 原厂 `COUIDatePicker` 可在滚轮与内置 `CalendarView` 之间切换；这里把两者拆成 `DatePicker` 和 `CalendarPicker`，切换由宿主组合。
- 原厂 `COUINumberPicker` 的振动、语音播报和 `setRowNumber`、`setTextVisibility` 等 View 专有开关未复刻。
- 原厂农历工具还提供节气与节日（`SOLAR_TERM_DAYS`、`getLunarFestival`、`getGregFestival`），这里只保留公历农历互转与日期文案。
- 原厂时间列的日期文案用 `DateUtils` 的 flag 组合，这里按「M月d日 周X」或「周X, MMM d」组合，首项替换为可覆盖的 `todayLabel`。
- 原厂 `COUITimeLimitPicker` 的上限通过 `setCurrentData` 分两段夹紧；这里在提交时统一夹紧小时与分钟，行为等价。

## 交互与限制

滚轮支持点击邻项、拖动、惯性滑动、长按、键盘方向键和无障碍调整。`valueRange` 必须非空且包含 `value`。`enabled = false` 会停止滚轮交互。`repeatIntervalMillis` 和两个字号必须大于零，`diffusion` 不能为负值；`minuteStep` 须在 1 到 60 之间，`dateCount` 须为正数。

月历会按范围约束日期；顶部月份箭头只浏览，不提交日期。月历不提供整体 `enabled` 参数。滚轮没有 `onValueChangeFinished`，如需“确认后才保存”，请由页面或弹窗持有草稿。

选择器本身不需要背景源；只有宿主额外使用材质弹窗时，才按[弹窗背景材质](dialog.md#使用背景材质)要求配置。系统与构建门槛见[兼容要求](../guide/compatibility.md)。

## Demo 与源码

Demo 提供数字、时分、时限、倒计时、年月日、农历及月历示例。

| 组合                          | 示例                                                                                    |
|-----------------------------|---------------------------------------------------------------------------------------|
| 倒计时三列、标题、项目材质与确认/取消         | [DemoWheelPresets](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoWheelPresets.kt) / [DemoPickerDialog](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoPickerDialog.kt) |
| 页面与弹窗共用内容、草稿及确认提交           | [DemoPickerSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoPickerSamples.kt) |

公开 API 见 [WheelPicker / CouiWheelColors / CouiPickerDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/WheelPicker.kt)、[CalendarPicker](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/CalendarPicker.kt)、[DatePicker](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/DatePicker.kt)、[TimePicker / CouiTime](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/TimePicker.kt)、[TimeLimitPicker](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/TimeLimitPicker.kt)、[LunarDatePicker](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/LunarDatePicker.kt)与 [CouiLunarCalendar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/picker/CouiLunarCalendar.kt)。
