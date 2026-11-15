# 🔀换行布局

`FlowLayout` 把一组标签按可用宽度自动换行，可折叠到指定行数，用于搜索历史、标签选择等数量不定的内容。

## 最小调用

条目、展开态与展开回调由宿主提供：

~~~kotlin
FlowLayout(
    items = items,
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    maxRowsFolded = 2,
)
~~~

`dark` 默认跟随 `LocalCouiDarkTheme` 或系统；局部 `CouiTheme` 提供自身模式，配色规则见[外观与预设](../guide/appearance.md)。

## 显示可折叠的标签

下面给出一组历史词，折叠时只显示前两行，点击标签回填查询：

~~~kotlin
@Composable
fun HistoryChips(modifier: Modifier = Modifier, onPick: (String) -> Unit) {
    val items = listOf(
        CouiFlowItem("wifi", "无法连接 Wi-Fi"),
        CouiFlowItem("battery", "电池耗电快"),
        CouiFlowItem("font", "字体大小"),
        CouiFlowItem("dark", "深色模式"),
    )
    var expanded by rememberSaveable { mutableStateOf(false) }

    FlowLayout(
        modifier = modifier.fillMaxWidth(),
        items = items,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        onItemClick = { index -> onPick(items[index].label) },
        maxRowsFolded = 2,
        maxRowsUnfolded = 8,
        expandContentDescription = "展开更多",
        collapseContentDescription = "收起",
    )
}
~~~

`onItemClick` 回传条目在原列表中的下标；省略时标签只展示、不接收点击。应用文案由宿主放入字符串资源，`expandContentDescription` 与 `collapseContentDescription` 供屏幕阅读器使用；省略时按钮没有描述，宿主应在正式页面传入。

## 换行与折叠

换行规则与原厂 `COUIFlowLayout` 一致：逐个测量标签，当前行的「已用宽度 + 间距 + 本项宽度」不超过可用宽度就放同一行，否则另起一行；首个标签独占一行时按可用宽度夹紧。标签宽度取自文字测量值加左右各 12dp，夹在 52dp 到 300dp 之间。

超过 `maxRowsUnfolded` 的条目不再排版。折叠时超出 `maxRowsFolded` 的行仍在布局中，但 alpha 置 0 并被容器高度裁剪；展开时隐藏项延迟 100ms、在 400ms 内淡入，折叠时在 300ms 内淡出，曲线取原厂 `COUIMoveEaseInterpolator`。容器高度随 `expanded` 立即切换，不单独做高度动画。

第 `maxRowsFolded` 行末尾放展开按钮，放不下时该行最后一个标签先退位；折叠按钮放在最后一行末尾，处理相同。按钮内容可用 `expandContent` / `collapseContent` 替换，默认自绘向下/向上的折线，因此核心库不需要图标模块。

## 外观与预设

`CouiFlowLayoutDefaults` 持有间距、标签高度与宽度、动画时长与缓动，取自 `coui_component_search_history_flow_*` 与 `Widget.COUI.Chip.Suggestion`。标签底色按 `?couiColorContainer8` 的口径从当前前景色派生，文字取 `?couiColorLabelPrimary`：

~~~kotlin
FlowLayout(
    items = items,
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    colors = CouiFlowLayoutDefaults.colors(dark).copy(
        chipContainer = CouiTheme.colorSchemeFor(dark).container,
    ),
)
~~~

## 平台与装配

组件只用公开 Compose 布局与绘制 API，按库最低系统运行，不依赖背景源或 API 34。`Modifier` 在根布局应用一次；父级给出无界宽度时按 `CouiFlowLayoutDefaults.fallbackWidth` 排版。空列表不占位。标签带按钮语义，整项承担点击；展开/折叠按钮带按钮角色与宿主提供的描述。

需要标题栏、清空按钮和整块淡出时，用[搜索历史](search-history.md)组合本组件，不要另写一套换行逻辑。

## 与原厂的差异

- 原厂用 `COUIChip` 子视图和 `View.measure` 得到宽度；这里在组合阶段用 `TextMeasurer` 测量文字，标签宽度再由 `widthIn` 与内留白决定，换行判定等价，但标签圆角、按压反馈、关闭图标与 `COUIChangeTextUtil` 字号适配没有移植。
- 原厂展开/折叠按钮是 `COUIPressFeedbackImageView`（带按压反馈与图标资源），这里默认自绘折线；宿主可用内容槽换成自己的图标。
- 原厂 `drawChild` 按容器高度逐个子视图裁剪，这里用 `clipToBounds` 裁剪整列，效果等价但作用域不同。
- 原厂在 `onDetachedFromWindow` 取消运行中的动画；这里动画由 `animateFloatAsState` 管理，随组合退出自动收尾。

## Demo 示例

单独使用与搜索历史组合见 [DemoNextComponents](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoNextComponents.kt)，库入口见 [FlowLayout / CouiFlowItem](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/flowlayout/FlowLayout.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/flowlayout/CouiFlowLayoutDefaults.kt)。
