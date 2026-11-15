# ⚪页码指示器

`PageIndicator` 用一排圆点表示当前页在总页数中的位置，只负责绘制；传入 `onDotClick` 时额外响应点按。

## 最小调用

传入总页数与当前页下标即可，下标从 0 开始：

~~~kotlin
PageIndicator(count = pages.size, selectedIndex = currentPage)
~~~

组件不持有内部选中状态。宿主在 `ViewPager2`、`HorizontalPager` 等容器的回调里更新 `selectedIndex`，指示器随之重绘。`count` 小于等于 0 时不占位；越界的 `selectedIndex` 先夹紧到 `0..count - 1`，不会因宿主瞬时状态越界抛错。

## 尺寸与排布

半径不是按距离分档，而是原厂 `COUIPageIndicator` 的连续插值：

- 距离带起点 `{0, -1, -2, -3}`、带宽 `{3, 5, 7, 9}`（点数不少于 6 时首段带宽 3，少于 6 个点时放宽到 5），落在哪一段就用那一段的相邻两档插值；
- 四档半径依次是 `dotSize / 2` 6dp、`medium / 2` 4.5dp、`small / 2` 2.5dp、0，相邻两档之间用 `COUIEaseInterpolator`（三次贝塞尔 0.33, 0, 0.67, 1）过渡，超出最后一段按 0；
- 距离的原点是原厂的 `mMaskOffset`（当前位置减首段带宽），当前点固定在 `d = 3`，所以 6 个点时窗口随当前页滑动，最多显示 6 个点（`CouiPageIndicatorDefaults.maxDots`）。

实际效果：`d ∈ [0, 3]` 是满半径的平段，向右（`d` 更大）依次收到 4.5dp、2.5dp、0，向左（`d` 为负）同样依次收到 4.5dp、2.5dp、0。所以当前点连同它左侧 3 个点是满半径，再往两侧迅速淡出；前 4 页因为 `mMaskOffset` 被夹在 0，平段固定在开头 4 个点上，第 5 页起窗口才开始滑动。点数为 5 及以下时首段带宽放宽到 5，所有点都是满半径，只靠颜色区分当前页。

圆点按半径依次累加位置（相邻净距恒为 `2 × spacing`），整条内容在 `modifier` 给出的区域内水平居中；组件占位始终是 `(dotSize + 2 × spacing) × min(6, count)`，切换当前页不会引起周围布局跳动。把指示器放进 `fillMaxWidth` 的容器时，圆点仍居中而不是贴左。需要改变位置时优先调整外层容器的对齐与留白：

~~~kotlin
PageIndicator(
    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 10.dp),
    count = 5,
    selectedIndex = currentPage,
)
~~~

## 点击

默认不安装手势，指示器是纯绘制节点。传入 `onDotClick` 后，点按圆点会把被点圆点的下标回传给宿主，宿主自行翻页：

~~~kotlin
PageIndicator(
    count = pages.size,
    selectedIndex = currentPage,
    onDotClick = { index -> pager.animateScrollToPage(index) },
)
~~~

判定沿用原厂 `COUIPageIndicator` 的 `onTouchEvent` + `callOnClick` + `c.f(x, y)`：按下到抬起不超过 `ViewConfiguration.getTapTimeout()`（100ms）才算点击，长按或拖动不触发；命中先看落点是否在某个圆点的外接方块内，不在方块内时取水平方向最近的圆点。原厂此时调用 `OnDotClickListener` 并 `setCurrentItem(index)`，回调拿到的是下标而不是坐标。

手势不消费事件，横向滑动仍交给外层 `HorizontalPager` / `ViewPager2`；原厂 `View` 会消费全部触摸事件，这里为了不与宿主的滑动冲突做了让步。

## 布局方向

原厂在 `layoutDirection == RTL` 时翻转绘制矩阵，点的顺序跟着镜像。组件读取 `LocalLayoutDirection`：从右到左时第 0 页在最右、点的排列整体镜像，命中判定仍按绝对坐标换算，点击回调拿到的下标不受方向影响。切换方向会重建手势监听，不会残留旧的命中区域。

## 配色

默认配色跟随 `CouiTheme`，深浅色按 `dark` 解析：未选中点取前景色 26%（日间）/30%（夜间）透明度，选中点取前景色本身，即日间 `#E6000000`、夜间 `#E6FFFFFF`。

需要放在图片或彩色背景上时显式传入颜色；未选中点的可选描边默认透明（纯填充），传入非透明描边色后描边绘制在点内部，不改变外径：

~~~kotlin
PageIndicator(
    count = 5,
    selectedIndex = currentPage,
    colors = CouiPageIndicatorDefaults.colors(dark = true).copy(
        dot = Color(0x4DFFFFFF),
        selectedDot = Color(0xE6FFFFFF),
        stroke = Color(0x33FFFFFF),
    ),
)
~~~

`CouiPageIndicatorDefaults` 中的直径、间距与描边宽度属于固定参数，宿主不复制这些数值。

## 无障碍

指示器是纯绘制节点，不添加语义、不获取焦点。当前页与总页数的播报由宿主的翻页容器负责；传入 `onDotClick` 时圆点本身也没有可访问的点击语义，如果宿主只靠点击圆点翻页，需要在容器上提供等价的状态描述与操作入口。

## 原厂依据

几何与配色来自 ColorOS 17 手机管家内嵌 COUI 资源的 `Widget.COUI.COUIPageIndicator` 与其 `Dark` 分支：

- 直径 `coui_page_indicator_dot_size` 6dp、`coui_page_indicator_dot_size_medium` 4.5dp、`coui_page_indicator_dot_size_small` 2.5dp
- 间距 `coui_page_indicator_dot_spacing` 3dp（每点两侧留白，净距 6dp），描边宽度 `coui_page_indicator_dot_stroke_width` 1dp
- 未选中色 `dotColor = ?couiColorHintNeutral`（`coui_color_hint_neutral` #42000000 / `coui_color_hint_neutral_dark` #4DFFFFFF）
- 选中色 `traceDotColor = coui_color_label_primary`（#E6000000 / `coui_color_label_primary_dark` #E6FFFFFF）

dex 复核（`com.coui.appcompat.indicator.COUIPageIndicator` 的 `$c` 内部类）后的结论：

- 半径在 `c.m(float offset, int index)` 里按 `d = index - mMaskOffset` 查四段距离带（`f16622e` 起点 `{0, -1, -2, -3}`、`f16623f` 带宽 `{3, 5, 7, 9}`），在 `f16624g` 的 `[dotSize/2, medium/2, small/2, 0]` 相邻两档之间插值，正向用 `带右端 - d`、反向用 `d - 带左端`，插值结果乘 2 后夹到上一档，没有命中任何一段时半径 0；带宽首项在 `c.n(boolean)` 里按点数是否小于 6 在 3 和 5 之间切换。
- `mMaskOffset` 由当前位置减首段带宽得到（`c.m()` 前半段），窗口偏移 `mVisibleRectOffset = min(count - 6, max(0, mMaskOffset - 1))`，所以当前点始终落在 `d = 3`，6 个点的窗口随当前页滑动。
- `onMeasure` 用 `(dotSize + 2 × dotSpacing) × min(6, count)`，即每个点两侧各留一个 spacing、最多排版 6 个点；点数少于 6 时宽度按所有点都是 `dotSize` 计算，与首段带宽放宽到 5 的处理一致。
- 点击走整视图的 `onTouchEvent`：`ACTION_DOWN` 记时间，`ACTION_UP` 且间隔不超过 `ViewConfiguration.getTapTimeout()` 时把坐标交给 `c.f(x, y)` 换算下标，再在 `callOnClick()` 里回调 `OnDotClickListener`；`dotClickable` 默认 true，且没有监听者时不做任何事。

## Demo 示例

三点、五点、切换当前页与点击跳页示例见 [DemoPageIndicatorSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoPageIndicatorSamples.kt)，库入口见 [PageIndicator](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/pageindicator/PageIndicator.kt)。
