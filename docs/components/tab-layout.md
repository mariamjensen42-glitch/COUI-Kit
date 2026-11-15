# 📑页签条

`TabLayout` 用一条包裹内容的胶囊容器把文字页签分段，选中项是一枚跟随切换滑动的胶囊填充，适合内容分区固定、需要明确当前页签的场景。

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

两种模式都先按文字算自然宽度：每段 `max(CouiTabLayoutDefaults.tabMinWidth, 文字宽 + 2 × CouiTabLayoutDefaults.labelPadding)`，文字本身不超过 `tabMaxWidth`，容器再在自然总宽上左右各加 `containerPadding`。

- `Fixed`：容器宽度取 `min(自然总宽, 可用宽度)`，超出可用宽度时把内层可用宽度按原厂分段控件的水位算法分配——短标签保住自然宽，剩余宽度在其余页签间均分；容器始终不滚动。
- `Scrollable`：容器保留自然总宽，内容可横向滚动；选中项用 300ms 滚向视口中间，滚动位置由组件内部维护，宿主只提交下标。

`Fixed` 的容器包裹内容后由宿主宽度居中；宿主约束不可用时退回内容宽度。

## 外观与预设

`CouiTabLayoutDefaults` 持有高度、字号、容器留白、单段最小宽度、文字两侧留白、文字上限与动画时长，几何取自分段控件的 `coui_segment_*` 资源与 `tablayout_*` 文字样式：容器高 40dp、上下左右内缩 4dp、单段最小宽 52dp、文字两侧各 12dp、字号 16sp。选中胶囊占整段宽度、高为容器高度减去上下留白，圆角取自身半高。数值以 KDoc 与 `CouiTabLayoutDefaults` 为准。

切换时胶囊的中心与宽度按 `min(300, |下标差| × 50 + 150)` 毫秒补间，文字颜色用同一进度在选中色与未选中色之间插值，选中项切换为 medium 字重。

配色由 `CouiTabLayoutColors` 提供，容器与选中胶囊默认直接取 `CouiSegmentedControlDefaults` 的原厂颜色，让两种控件外观一致：

~~~kotlin
TabLayout(
    tabs = tabs,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = onSelectedIndexChange,
    colors = CouiTabLayoutDefaults.colors(dark).copy(
        container = CouiTheme.colorSchemeFor(dark).surfaceContainer,
    ),
)
~~~

需要完全自定义时传自己的 `CouiTabLayoutColors`：`container` 画整条容器，`indicator` 画选中胶囊，`selectedText` / `unselectedText` 是文字两端的插值色。

## 平台与装配

组件只用公开 Compose 布局与绘制 API，按库最低系统运行，不依赖背景源、`CouiBackdrop` 或 API 34。`Modifier` 在根布局应用一次，高度由 `defaultMinSize` 提供；胶囊与选中填充都画在内容坐标系里，随横向滚动一起位移。RTL 下由 `placeRelative` 镜像项位置，`SegmentGeometry` 同步镜像中心。

页签带 `Role.Tab` 与 `selected` 语义；整项承担点击，文字不重复添加点击。组件不持有选中状态、不管理页面内容，页签与页面的联动由宿主的 `HorizontalPager`、`ViewPager2` 等容器负责。与分段控件不同，这里只有点击切换，没有拖动滑选。

## 与原厂的差异

- 外观按项目指定设计改为胶囊分段：原厂 `COUITabLayout` 是文字加底部 2dp 滑动下划线，这里改为包裹内容的胶囊容器加整段选中填充，因此不绘制指示器贴底与 1dp 分隔线，也没有 `dividerVisible`。胶囊几何与配色复用分段控件。
- 原厂 `COUITabLayout` 支持自定义视图、图标、红点角标、TabLayoutMediator 与 ViewPager 自动联动；本组件只有文字页签与受控下标，联动由宿主装配。
- 原厂 `COUISlidingTabStrip.onMeasure` 按 `tabMinDivider` / `tabMinMargin` / `coui_tab_layout_content_min_width` 分配间距；这里按每段的自然宽度和 `coui_segment_*` 水位算法分配，宽度结果与胶囊外观一致，但不是同一套公式。
- 原厂 `COUISlidingTabStrip` 在 `ArgbEvaluator` 里只对切换的两个页签插值文字色；这里用连续的选中进度对所有页签插值。相邻页签切换结果一致，跨多个页签时中间页签的过渡更平滑。
- 原厂按 `COUIResponsiveUtils` 在 small / medium / large 屏之间切换外边距与间距，这里固定取手机（small）值。
- 原厂的 `couiTabIndicatorBackgroundHeight` 背景条、`couiTabAutoResize` 与多种样式分支未装配。

## Demo 示例

固定与滚动两种模式见 [DemoNextComponents](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoNextComponents.kt)，库入口见 [TabLayout / CouiTabItem](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/tablayout/TabLayout.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/tablayout/CouiTabLayoutDefaults.kt)。
