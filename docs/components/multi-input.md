# 多行输入卡

`MultiInputCard` 是固定的多行输入卡片：152dp 卡片、左右 32dp 内留白、空态最多 3 行提示、正文最多 5 行，右下角可选字数，位于 `com.itos.coui.textfield`。

单行与可浮动标签的输入用[输入框](text-field.md)，分格数字输入用[验证码输入](code-input.md)。

## 最小调用

~~~kotlin
var note by remember { mutableStateOf("") }

MultiInputCard(
    value = note,
    onValueChange = { note = it },
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    hint = "说点什么",
    maxLength = 60,
)
~~~

`value` 与 `onValueChange` 是受控入口。给了 `maxLength` 时超出部分由组件截断，宿主拿到的值不会超过上限。

## 提示与行数

| 状态 | 行数 | 省略 |
| --- | --- | --- |
| 空值，有 `hint` | 最多 3 行 | 超出以省略号收尾 |
| 有内容 | 最多 5 行 | 不省略，超出后内部滚动 |

提示只在值为空时出现，与焦点无关：聚焦但未输入时提示仍在。

## 字数

`maxLength` 为空时不显示字数，卡片只留正文；给了 `maxLength` 时右下角显示 `n/max`，并在正文与字数之间保留 `CouiMultiInputDefaults.counterSpacing` 的高度。

达到上限后字数在 250ms 内切到错误色，保持 1 秒再回落到常态色；继续输入会被截断到上限，因此不会长于上限。删除到上限以下时字数立即回落，不留残余错误色。

## 配色与度量

~~~kotlin
MultiInputCard(
    value = note,
    onValueChange = { note = it },
    colors = CouiMultiInputDefaults.colors(dark = true),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
)
~~~

`CouiMultiInputDefaults.colors(dark)` 给六个角色：`container`（卡片色）、`text`、`hint`（日间 26%、夜间 30% 正文色）、`counter`、`cursor`（强调色）、`error`（日间 `#DB382C`、夜间 `#FF6C61`）、`disabled`。度量在 `CouiMultiInputDefaults` 上：`minHeight` 152dp、`contentPadding` 32/32/13/13dp、`counterSpacing` 12dp、`hintMaxLines` 3、`maxLines` 5、`counterColorDurationMillis` 250、`counterResetDelayMillis` 1000。

点击卡片任意位置聚焦输入区，包括正文两侧的留白；`enabled = false` 时停止输入与聚焦，文字改用禁用色。

## 与原厂的差异

- 原厂在有字数时把 `EditText` 的底部内留白改成字数高度、无字数时上下留白相等；这里字数在正文之后单独占位，`contentPadding` 保持不变。
- 原厂计数达到上限后从 `postDelayed` 回调里恢复提示色，并在文本变化时取消挂起任务；这里用 `LaunchedEffect(text.length)` 的取消语义达到同样效果。
- 原厂在内容高于容器时禁止父级拦截滚动；这里依赖 Compose 的滚动嵌套规则，卡片本身不拦截父级手势。

## Demo 与源码

示例见 [DemoTextFieldFamilySamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoTextFieldFamilySamples.kt)，公开入口见 [MultiInputCard](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/textfield/MultiInputCard.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/textfield/CouiMultiInputDefaults.kt)。
