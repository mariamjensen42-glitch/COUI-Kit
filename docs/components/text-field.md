# ✏️输入框

`TextField` 是受控输入框，外观对应原厂 `Widget.COUI.EditText.HintAnim.Rectangle` 分支：10dp 圆角描边容器，默认不显示提示文字；传入 `placeholder` 时提示在框内浮动，聚焦或有内容后上浮为小标题。适合设置页、表单和独立输入区。

## 最小调用

宿主保存文本，并在回调中更新；组件不持有草稿：

~~~kotlin
var name by rememberSaveable { mutableStateOf("") }

TextField(
    value = name,
    onValueChange = { name = it },
)
~~~

`value` 是唯一事实来源。输入法组合区、光标位置和选区由 `BasicTextField` 管理；需要读取或恢复选区时，自行组合 `BasicTextField`。组件不做格式校验、长度限制或去空格，这些由宿主在 `onValueChange` 中决定。

省略外观参数时，深浅色由可选的 `CouiTheme` 提供，未设置时跟随系统深浅色和 COUI 预设。通用规则见[外观与预设](../guide/appearance.md)。

## 外观

容器是 10dp 圆角的描边矩形（`coui_textinput_corner_radius`），无填充，最小高度 62dp（`coui_edit_text_height_min`）。内留白取 `Widget.COUI.EditText.HintAnim.Rectangle` 的 `paddingLeft` 16dp、`paddingRight` 10dp、`paddingBottom` 12dp，以及原厂 `getModePaddingTop()` 算出的 `rectModePaddingTop + 文字行高/2` = 20.4dp；内容在框内垂直居中。

矩形模式在原厂由 `COUIEditText` 自绘：`GradientDrawable` 风格的 `setStroke(宽, 色)` 加 `setCornerRadii(四角)`，不设填充色。`drawable/coui_edittext_default_background*` 那组 9patch 是**下划线**分支用的（只有右列与底行有像素，常态 1px、聚焦 4px 粗线），不是矩形底。

描边颜色与宽度随状态变化：

| 状态 | 宽度 | 颜色 |
| --- | --- | --- |
| 常态 | 0.33dp（`coui_textinput_stroke_width`） | `couiColorDivider` |
| 聚焦 | 1dp（`coui_textinput_focus_stroke_width`） | `couiColorPrimary`（主题强调色） |
| 错误 | 1dp | `couiColorError` |
| 禁用 | 0.33dp | `couiColorDisabledNeutral` |

## 浮动提示与标题

`placeholder` 与 `title` 都是可选的。省略 `placeholder`（或传空串）时框内不显示提示文字；传入非空文字后才绘制框内浮动标签。

| 参数 | 位置 | 行为 |
| --- | --- | --- |
| `placeholder` | 框内 | 可选浮动标签：空内容且未聚焦时与输入文字同位，聚焦或有内容后上浮到框内左上并缩到 10sp（`coui_textinput_hint_text_size`） |
| `title` | 框外上方 | 可选固定标题，始终显示，与浮动标签互不影响 |

~~~kotlin
TextField(
    value = name,
    onValueChange = { name = it },
    placeholder = "请输入名称",
    title = "设备名称",
)
~~~

上浮的位移与缩放由 `animateFloatAsState` 驱动，时长 200ms、`COUIMoveEaseInterpolator`（PathInterpolator 0.3, 0, 0.1, 1），取自 `COUIEditText` 的提示动画。标签位移只在绘制层更新；输入文字在标签上浮后让出 10.5dp，保证两者不重叠。颜色按同一进度在提示色与小标题色之间插值。

## 错误态

`error` 非空时，描边改为错误色并在框外下方显示一行说明，间距 4dp（`coui_input_edit_text_error_padding_bottom`）；输入本身不被拦截，`onValueChange` 照常回调：

~~~kotlin
TextField(
    value = name,
    onValueChange = { name = it },
    error = if (name.isBlank()) "名称不能为空" else null,
)
~~~

错误色优先于聚焦色：同时聚焦且出错时，描边仍是错误色。

## 禁用

`enabled = false` 时停止输入、焦点和光标，文字、标签与描边改用禁用色，框外标题同步变淡；`value` 与回调仍由宿主持有：

~~~kotlin
TextField(
    value = name,
    onValueChange = { name = it },
    title = "设备名称",
    enabled = false,
)
~~~

## 配色与预设

`CouiTextFieldDefaults.colors(dark)` 返回 `CouiTextFieldColors`，可通过 `copy` 覆盖单个颜色：

~~~kotlin
TextField(
    value = name,
    onValueChange = { name = it },
    dark = dark,
    colors = CouiTextFieldDefaults.colors(dark).copy(error = errorColor),
)
~~~

| 颜色 | 原厂来源 | 日间 | 夜间 |
| --- | --- | --- | --- |
| `text` / `title` | `couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| `placeholder` | `couiColorLabelSecondary` | `#8A000000` | `#8AFFFFFF` |
| `floatingLabel` | `couiColorSecondNeutral` | `#8A000000` | `#8AFFFFFF` |
| `line` | `couiColorDivider` | `#1F000000` | `#33FFFFFF` |
| `focusedLine` | `couiColorPrimary` | 主题强调色 | 主题强调色 |
| `error` | `couiColorError` | `#DB382C` | `#FF6C61` |
| `disabledText` / `disabledLine` | `couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |

聚焦色与光标取当前主题的强调色，原厂此处引用 `couiColorPrimary`（`Theme.COUI` 中映射为 `coui_color_primary_green`）；宿主可用 `colors` 显式传回固定值。尺寸、字号与动画集中在 `CouiTextFieldDefaults`：文字 16sp、浮动标签 10sp、圆角 10dp、最小高度 62dp。

## Demo 示例

不带提示、带标题、错误和禁用示例见 [DemoTextFieldSamples](../../demo/src/main/java/com/itos/coui/demo/DemoTextFieldSamples.kt)，库入口与配色见 [TextField / CouiTextFieldDefaults](../../coui/src/main/java/com/itos/coui/textfield/TextField.kt)。
