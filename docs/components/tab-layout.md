# 📑页签条

`TabLayout` 用文字页签加一条底部滑动指示器切换页面分类，适合内容分区固定、需要明确当前页签的场景。

## 最小调用

页签、当前下标与选择回调由宿主提供：

~~~kotlin
TabLayout(
    tabs = tabs,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = onSelectedIndexChange,
)
~~~

`dark` 默认跟随 `LocalCouiDarkTheme` 或系统；局部 `CouiTheme` 提供自身模式，配色规则见[外观与预设](../guide/appearance.md)。

## 显示一组页签

下面创建四个页签，并在点击时切换选中项。`tabs`、`selectedIndex` 与 `onSelectedIndexChange` 由页面持有：

~~~kotlin
@Composable
fun Categories(modifier: Modifier = Modifier) {
    val tabs = listOf(
        CouiTabItem("all", "全部"),
        CouiTabItem("network", "网络"),
        CouiTabItem("display", "显示"),
        CouiTabItem("about", "关于"),
    )
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    TabLayout(
        modifier = modifier.fillMaxWidth(),
        tabs = tabs,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it },
    )
}
~~~

每个 `key` 必须唯一并在重排时保持稳定。越界的 `selectedIndex` 先夹紧到有效范围；重复点击当前项仍会回调，由宿主决定是否忽略。`CouiTabItem.enabled = false` 时该项不响应选择，但仍占位移。

## 固定与滚动

`mode` 决定排布：

~~~kotlin
TabLayout(
    tabs = tabs,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = onSelectedIndexChange,
    mode = CouiTabLayoutMode.Scrollable,
)
~~~

`Fixed` 等分整条宽度、不滚动，对应原厂 `MODE_FIXED`；`Scrollable` 按文字宽度排布，选中项用 300ms 滚向中间，对应 `MODE_SCROLLABLE`。滚动位置由组件内部的滚动状态维护，宿主只提交下标。

指示器宽度跟随当前页签文字宽度（两侧各加 `CouiTabLayoutDefaults.indicatorPadding`），不小于 `CouiTabLayoutDefaults.indicatorMinWidth`；切换时长为 `min(300, 相邻距离 × 50 + 150)` 毫秒，文字颜色按同一进度在选中色与未选中色之间插值，选中项切换为 medium 字重。

## 外观与预设

`CouiTabLayoutDefaults` 持有高度、指示器尺寸、文字字号、最小项宽与动画时长，取值来自 `tablayout_*` 与 `coui_tab_layout_*` 资源。需要替换配色时传 `CouiTabLayoutColors`：

~~~kotlin
TabLayout(
    tabs = tabs,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = onSelectedIndexChange,
    colors = CouiTabLayoutDefaults.colors(dark).copy(
        indicator = CouiTheme.colorSchemeFor(dark).accent,
    ),
    dividerVisible = true,
)
~~~

原厂 `Widget.COUI.TabLayout.Middle` 的 `couiTabIndicatorColor` 指向 `?couiColorPrimaryNeutral`，因此默认指示器与选中文字同取前景色，不是强调色。`dividerVisible` 打开时在底部画 1dp 分隔线。

## 平台与装配

组件只用公开 Compose 布局与绘制 API，按库最低系统运行，不依赖背景源、`CouiBackdrop` 或 API 34。`Modifier` 在根布局应用一次，内部默认高度通过 `defaultMinSize` 提供，宿主可用尺寸 Modifier 覆盖。RTL 下由 `Row` 与 `BoxWithConstraints` 的方向处理，指示器按测量位置跟随。

页签带 `Role.Tab` 与 `selected` 语义；整项承担点击，文字不重复添加点击。组件不持有选中状态、不管理页面内容，页签与页面的联动由宿主的 `HorizontalPager`、`ViewPager2` 等容器负责。

## 与原厂的差异

- 原厂 `COUITabLayout` 支持自定义视图、图标、红点角标、TabLayoutMediator 与 ViewPager 自动联动；本组件只有文字页签与受控下标，联动由宿主装配。
- 原厂 `COUISlidingTabStrip` 在 `ArgbEvaluator` 里只对切换的两个页签插值文字色；这里用连续的选中进度对所有页签插值。相邻页签切换结果一致，跨多个页签连续滑动时中间页签的过渡更平滑。
- 原厂指示器是 2dp 矩形，底部分隔线是 1px；这里分隔线按 1dp 还原。
- 原厂的 `COUITabLayout` 还处理 `couiTabIndicatorBackgroundHeight` 背景条、按钮槽位与多种样式分支，本组件未装配。

## Demo 示例

固定与滚动两种模式见 [DemoNextComponents](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoNextComponents.kt)，库入口见 [TabLayout / CouiTabItem](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/tablayout/TabLayout.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/tablayout/CouiTabLayoutDefaults.kt)。
