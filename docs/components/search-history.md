# 🔍搜索历史

`SearchHistory` 把标题、清空按钮和可折叠的历史标签装成一块，用于搜索页的历史词区域。

## 最小调用

标题、历史词、展开态与三个回调都由宿主提供：

~~~kotlin
SearchHistory(
    title = title,
    items = items,
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    onItemClick = onItemClick,
    onDelete = onDelete,
    deleteContentDescription = deleteDescription,
)
~~~

`dark` 默认跟随 `LocalCouiDarkTheme` 或系统；局部 `CouiTheme` 提供自身模式，配色规则见[外观与预设](../guide/appearance.md)。

## 显示历史词

下面把历史词交给组件，点击回填查询，清空按钮提交删除：

~~~kotlin
@Composable
fun HistorySection(
    modifier: Modifier = Modifier,
    onPick: (String) -> Unit,
) {
    var items by rememberSaveable {
        mutableStateOf(listOf(
            CouiFlowItem("wifi", "无法连接 Wi-Fi"),
            CouiFlowItem("battery", "电池耗电快"),
            CouiFlowItem("font", "字体大小"),
            CouiFlowItem("dark", "深色模式"),
            CouiFlowItem("ringtone", "来电铃声"),
        ))
    }
    var expanded by rememberSaveable { mutableStateOf(false) }

    SearchHistory(
        modifier = modifier.fillMaxWidth(),
        title = stringResource(R.string.history_title),
        items = items,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        onItemClick = { index -> onPick(items[index].label) },
        onDelete = { items = emptyList() },
        deleteContentDescription = stringResource(R.string.history_clear),
        maxRowsFolded = 2,
        expandContentDescription = stringResource(R.string.history_expand),
        collapseContentDescription = stringResource(R.string.history_collapse),
    )
}
~~~

`R.string.*` 是宿主自己的资源，`onPick` 由页面提供；库不内置业务文案。清空按钮点击后，整块按原厂 300ms 淡出，动画结束才回调 `onDelete`；宿主清空 `items` 后整块不再占位。动画期间重复点击不会提交第二次删除。

`onItemClick` 回传条目在原列表中的下标。展开与收起的换行规则、按钮位置与动画见[换行布局](flow-layout.md)；展开后最多 8 行，折叠行数由 `maxRowsFolded` 指定（原厂默认不折叠，实际页面通常取 2）。

## 外观与预设

`CouiSearchHistoryDefaults` 持有整块内留白（起始/结束 16dp、顶部 16dp、底部 32dp）、标题栏上下 7dp、删除图标 18dp、48dp 命中区、历史区间隔 8dp 与 300ms 淡出时长，取值来自 `coui_component_search_history_*`。

标题排版取 `couiTextAppearanceButton`（14sp medium），配色与标签共用 `CouiFlowLayoutColors`：

~~~kotlin
SearchHistory(
    title = title,
    items = items,
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    onItemClick = onItemClick,
    onDelete = onDelete,
    deleteContentDescription = deleteDescription,
    colors = CouiFlowLayoutDefaults.colors(dark),
    deleteContent = { Icon(deletePainter, contentDescription = null) },
)
~~~

`deleteContent` 可换成宿主图标（示例里的 `Icon` 与 `deletePainter` 由页面自己的图标资源或图标库提供），默认自绘垃圾桶轮廓，因此核心库不需要图标模块。内容槽按 `CouiSearchHistoryDefaults.deleteIconSize` 的方框居中，不改变命中区。

## 平台与装配

组件只用公开 Compose 布局与绘制 API，按库最低系统运行，不依赖背景源或 API 34。`Modifier` 在根布局应用一次，内留白属于组件内部排版；宿主只需给出可用宽度。标题、删除按钮与历史区都在同一列内，删除按钮带按钮角色和宿主提供的描述。

## 与原厂的差异

- 原厂 `COUISearchHistoryView` 是 `ConstraintLayout`，清空按钮用 `AppCompatImageView` 与 `coui_component_icon_bg`；这里用 `Column` + `Row` 装配，自绘删除标记，命中区固定 48dp。
- 原厂在 `onConfigurationChanged` 里重设删除图标的 endMargin；这里由 Compose 的方向与布局自动处理。
- 原厂删除动画结束后由宿主或监听器决定可见性，这里在 `items` 为空时不占位，语义等价但返回条件由宿主持有。
- 标签分支的差异见[换行布局](flow-layout.md#与原厂的差异)。

## Demo 示例

折叠、展开与清空见 [DemoNextComponents](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoNextComponents.kt)，库入口见 [SearchHistory](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/searchhistory/SearchHistory.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/searchhistory/CouiSearchHistoryDefaults.kt)。
