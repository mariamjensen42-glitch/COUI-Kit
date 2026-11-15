# 🏷️Chip

`Chip` 是可选中、可关闭的标签，`ChipGroup` 负责按宽度换行与折叠，`ChipGroupEditText` 在标签后跟一个内联输入框。三者都是受控组件：选中值、关闭动作与输入内容都由宿主持有。

纯静态、不参与选择的标签用[标签](tag.md)；分类切换用 [ChipTab](chip-tab.md)。

## 最小调用

~~~kotlin
Chip(text = "推荐")
~~~

默认样式是 `CouiChipStyle.Suggestion`：12sp 文本、28dp 高、左右各 12dp 内留白，容器为 `coui_color_container8` 的中性薄填充，文本取 `coui_color_label_primary`。不传 `onClick` 与 `onSelectedChange` 时不接收点击。

四种样式对应原厂 `Widget.COUI.Chip` 的四个派生样式，尺寸差异都收在 `CouiChipDefaults.metrics(style)` 里：

| 样式 | 高度 | 左右留白 | 图标 | 关闭图标 |
| --- | --- | --- | --- | --- |
| `Default` | 28dp | 0 | — | — |
| `Selection` | 32dp | 12dp | 16dp，图标后 4dp | — |
| `Suggestion` | 28dp | 12dp | 14dp，图标前 12dp、后 4dp | 12dp，两侧 8dp，命中区 24dp |
| `Input` | 32dp | 12dp | 24dp，前后各 4dp | 14dp，前 8dp 后 9dp，命中区 28dp |

宽度按内容计算并夹在 `minWidth`（52dp）与 `maxWidth`（300dp）之间；文本可用宽度上限 200dp，超出按省略号截断。整块宽度按原厂公式产生：`起留白 + 图标区 + 文字前留白 + 文字 + 文字后留白 + 数量 + 关闭区`，文字在剩余空间里居中。

## 选中

传入 `onSelectedChange` 后标签可选中，内部按 `Checkbox` 角色上报；只传 `onClick` 时按 `Button` 角色处理：

~~~kotlin
var selected by remember { mutableStateOf(true) }
Chip(
    text = "已选",
    style = CouiChipStyle.Selection,
    selected = selected,
    onSelectedChange = { selected = it },
)
~~~

底色、内容色在未选中色与选中色之间按原厂 `TintAnimation` 插值——response 0.3、bounce 0 ——并逐通道插值，不做色彩空间转换。禁用时直接取禁用色，不跑染色动画。

## 关闭

`onClose` 非空时出现关闭图标。图标宽度、透明度与文字位移一起按闭合进度过渡：图标从不占位插值到完整宽度，文字同步右移，缩放从 0.3 到 1：

~~~kotlin
Chip(
    text = "可删除",
    onClose = { remove() },
    closeContentDescription = "移除标签",
)
~~~

图标自身有 24dp（`Input` 样式 28dp）的独立命中区，位于背景右端，命中判定不进入整块的点击。关闭图标是独立的语义节点，`closeContentDescription` 为空时不发语义描述——可访问性场景请始终给出。

## 数量与红点

`count` 在文字后追加 13sp 的数量文案，`showRedDot` 在文字右上方叠一个 8dp 红点（偏移 2dp/6dp）：

~~~kotlin
Chip(text = "收件箱", count = "12", showRedDot = true)
~~~

红点只表达状态，不处理点击。

## 标签组

`ChipGroup` 按可用宽度换行，行距与列距默认各 8dp。`singleLine = true` 时不换行，超出部分不参与布局：

~~~kotlin
ChipGroup(Modifier.fillMaxWidth()) {
    labels.forEach { Chip(text = it) }
}
~~~

原厂 `singleSelection` / `selectionRequired` 由 `COUICheckableGroup` 在内部维护；这里交给宿主：选中集合放在宿主状态里，逐个传给 `Chip`。单选时把回调写成「用新值替换整个集合」即可。

## 折叠

`collapsable = true` 时组尾跟一个 28dp 的折叠按钮，折起后只保留 `collapsedMaxRows` 行（默认 3），其余标签不参与布局。行高变化按 response 0.35、bounce 0 的弹簧过渡，超出部分被裁剪：

~~~kotlin
ChipGroup(
    collapsable = true,
    collapsed = collapsed,
    onCollapsedChange = { collapsed = it },
    collapsedMaxRows = 2,
) { tags.forEach { Chip(text = it) } }
~~~

折起时最后一行会为按钮预留宽度：放不下的标签依次移出，直到按钮能落在该行末尾。

## 标签输入组

`ChipGroupEditText` 把输入框排成标签流的最后一项：输入框随文字变宽，剩余宽度不足 10dp 时换到新行。输入内容由宿主持有，键盘确认键触发 `onSubmit`，宿主据此新增标签并清空文本：

~~~kotlin
var text by remember { mutableStateOf("") }
var tags by remember { mutableStateOf(listOf("设计", "动效")) }
ChipGroupEditText(
    text = text,
    onTextChange = { text = it },
    onSubmit = { value -> tags = tags + value; text = "" },
    placeholder = "添加标签",
) {
    tags.forEach { tag -> Chip(text = tag, onClose = { tags = tags - tag }) }
}
~~~

输入行最低高度取 `Input` 样式的 32dp，文本外观 15sp；占位文案只在文本为空时显示。

## 与原厂的差异

- 单选、必选与「至少保留一个」的语义由宿主维护，组件不内置。
- 关闭动画与组高度动画用 Compose 弹簧换算原厂 response（`(2π/response)²`），逐帧曲线与原厂 View 弹簧动画不完全一致。
- 原厂组的宽度也参与动画；这里宽度由父约束决定，只对高度做过渡。
- 原厂 `smoothtyle` 走 `OplusPathAdapter` 平滑圆角，这里统一用传入的 `shape`（默认胶囊）。
- 按下态在原厂是 drawable 的 mask effect；这里用 `couiPressRipple` 与 `couiPressMotion` 表达。
- 指针悬停与键盘焦点态不上报，`hovered` / `focused` 两条染色路径未移植。
- 折行在边界宽度上按可用宽度重算，与原厂逐项判定函数相比可能有 1px 级差异。

## Demo 与源码

示例见 [DemoChipFamilySamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoChipFamilySamples.kt)，公开入口见 [Chip](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/chip/Chip.kt)、[ChipGroup](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/chip/ChipGroup.kt) 与 [CouiChipDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/chip/CouiChipDefaults.kt)。
