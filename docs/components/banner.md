# 🔁轮播

`Banner` 是逐屏切换的内容容器：一屏一张卡片，左右露出相邻卡片的边，底部居中一排页码圆点；默认自动翻页、首尾无限循环，点圆点跳到对应页。只需要圆点时用[页码指示器](page-indicator.md)。

## 最小调用

```kotlin
val state = rememberBannerState(count = pages.size)
Banner(
    count = pages.size,
    state = state,
    modifier = Modifier.fillMaxWidth().height(160.dp),
) { index ->
    Page(pages[index])
}
```

`Banner` 内部就是 `HorizontalPager`，滚动与拖动手势由它持有；外层不要再包可横向滑动的容器，否则手势会被外层吃掉。高度由宿主的 `modifier` 给出，组件本身不设固定高。

## 参数

| 参数 | 默认值 | 说明 |
| --- | --- | --- |
| `count` | — | 真实页数，不含首尾补页；`0` 时直接不占位 |
| `state` | `rememberBannerState(count)` | 当前页与翻页入口 |
| `autoLoop` | `true` | 到点自动翻下一页；拖动期间暂停 |
| `loopDurationMillis` | `5000` | 两次自动翻页之间停留的时长 |
| `scrollDurationMillis` | `950` | 一次翻页的滚动时长，同时加在停留时长之后 |
| `pageMargin` | `24.dp` | 页与页之间的间距，也是左右留白的下限 |
| `startItemWidth` / `endItemWidth` | `0.dp` | 左侧、右侧额外露出的宽度 |
| `indicator` | `count > 1` | 是否显示底部圆点 |
| `indicatorColors` | `#4DFFFFFF` / `#E6FFFFFF` | 未选中点与选中点的颜色 |
| `onPageChange` | `null` | 当前页变化回调，参数是真实下标 |
| `pageContent` | — | 每页内容，参数是真实下标 |

数值都在 `CouiBannerDefaults` 里，宿主需要改间距时传参，不要复制常量。

## 露边与间距

原厂把"露出相邻卡片"交给三处：pager 的左右 padding、关闭 `clipToPadding`、以及一个往 pager 里加的页间 transformer。这里的对应关系是：

- 左右 padding → `contentPadding`，取值是 `startItemWidth + pageMargin` 与 `endItemWidth + pageMargin`；`HorizontalPager` 默认不裁剪内容，相邻页因此露在两侧；
- 页间间隙 → `pageSpacing = pageMargin`（原厂那个 transformer 的实现类不在已反编译的源码树里，只按"页与页留 `pageMargin`"对齐）；
- `startItemWidth` / `endItemWidth` 只影响首尾两侧多露出的宽度，不影响页与页之间的间隙。

所以默认 24dp 时中间那页两侧各让 24dp，相邻卡片露出 24dp 的边；把 `startItemWidth` 与 `endItemWidth` 都设为 `24.dp`，中间卡片的左右留白变成 48dp。

指示器贴底居中，距底 `10.dp`（`CouiBannerDefaults.indicatorBottomMargin`）；它叠在内容之上，不占 pager 高度。

## 自动轮播

- 节奏是"停留 `loopDurationMillis` + 滚动 `scrollDurationMillis`"：原厂在 `postDelayed(任务, loopDuration × 1000 + 950)` 里把滚动时长一并算进等待，所以每张卡片从出现到下一张开始动是 5.95 秒。
- 翻页动画用 `CubicBezierEasing(0.2f, 0f, 0.1f, 1f)`（原厂 `PathInterpolator(0.2f, 0f, 0.1f, 1f)`），时长 `scrollDurationMillis`。
- 手指按下时暂停，抬手或取消后重新计时；判定用"是否正在被拖动"，所以自动翻页自身造成的滚动不会把计时清零。
- `count <= 1` 或无限循环关闭时不跑；上一帧滚动还没结束时跳过这一拍，不叠加动画。

## 无限循环

沿用原厂的补页方案：pager 的页数是 `count + 2`，第 0 页补的是最后一页、第 `count + 1` 页补的是第一页，真实页从下标 1 开始。滑到补页并停稳后，无动画跳到对应的真实页：

- 停在补页 0 → 无动画跳到真实页最后一页；
- 停在补页 `count + 1` → 无动画跳到真实页第一页。

因为跳转发生在滚动停止之后，视觉上只是继续往同一方向滑。`count <= 1` 时不补页，也不自动轮播，避免两页互为镜像。外面传进来的下标永远是真实下标，滑到补页的那一帧也不会读到越界值。

## 状态与翻页

`rememberBannerState` 返回 `BannerState`，它把补页换算掉：

- `currentIndex`：当前真实页下标；
- `animateScrollToIndex(index)`：带 950ms 贝塞尔动画跳到某一页（点圆点走这条）；
- `scrollToIndex(index)`：无动画跳转。

`count` 或 `infiniteLoop` 变化时状态会重建并回到 `startIndex`：页数变了以后补页数跟着变，沿用旧状态会出现"停在补页上"的错位。宿主自己持有 `BannerState` 时，页数变化请重新 `rememberBannerState`。

用 `rememberSaveable` 保存的是当前页下标而不是 `BannerState`，重建时把保存的下标当 `startIndex` 传回来即可。

## 无障碍

圆点来自[页码指示器](page-indicator.md)：纯绘制节点，不添加语义、不获取焦点，"第几页 / 共几页"由宿主在容器上播报。原厂 `COUIBanner` 也没有把当前页写进语义；`coui_page_indicator_description`（复数资源）在 COUI 的布局里只用于指示器自身的描述。宿主要给整块区域一个可读描述时，在 `modifier` 上挂 `semantics { contentDescription = ... }`。

## 原厂依据

来自 ColorOS 17 手机管家内嵌 COUI 的 `com.coui.appcompat.banner`：

- `COUIBanner`：`couiBannerType` 默认 0（手机形态，pager + 指示器），`couiLoopDuration` 默认 5（秒），`couiStartPosition` 默认 1，`couiLeftItemWidth` / `couiRightItemWidth` 默认 0，`couiPageMargin` 缺省取 `COUIBannerUtil` 的 `dpToPx(24)`，`couiInfiniteLoop` 默认 true。
- `g(left, right, margin)`：`f(left + margin, right + margin)`，而 `f()` 把它写进 `ViewPager2.setPadding` 并 `setClipToPadding(false)` / `setClipChildren(false)`；`margin > 0` 时还把一个 `c3.a(margin)` 加进 `f6233f` 这个 transformer 容器（JADX 把容器写成 `androidx.viewpager2.widget.d`）。`c3.a` 的实现不在已反编译的源码树里，它怎么分摊页间间距没有确认，所以这里只按"页与页之间留 `pageMargin`"实现。
- `h()`：`getHandler().postDelayed(runnable, loopDuration × 1000 + q)`，`q` 默认 950，同一个值喂给 `ViewPager2` 的平滑滚动器；滚动插值器是 `PathInterpolator(0.2f, 0f, 0.1f, 1f)`。
- `dispatchTouchEvent`：`ACTION_DOWN` 时 `removeCallbacks`，`ACTION_UP` / `ACTION_CANCEL` 时重新 `h()`，且只在 `autoLoop && type == 0` 时生效。
- `COUIBannerOnPageChangeCallback`：滚动停稳（`SCROLL_STATE_IDLE`）时把补页下标换回真实页。
- `coui_banner_content_layout.xml`：`ViewPager2` 与 `COUIPageIndicatorKit` 用 `<merge>` 叠放，指示器 `layout_marginBottom` 10dp，`dotColor` 取 `coui_page_indicator_dot_color_in_dark_bg`（#4DFFFFFF）、`traceDotColor` 取 `coui_color_label_primary_dark`（#E6FFFFFF），所以图片上的圆点固定用深底配色。

## Demo 示例

页数与露边宽度的滑条、每页一张卡片、自动轮播见 [DemoBannerSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoBannerSamples.kt)，库入口见 [Banner](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/banner/Banner.kt)。

## 与原厂的差异

- 只移植 `type = 0`。原厂在 `screenWidthDp >= 600` 时自动切到 `type = 1`（`COUIBannerRecyclerView`，左右 24dp 留白的双列列表 + 无指示器、无自动轮播），这里不做宽屏自动切换，宿主在宽屏下自行选择版式或换用普通网格。
- 原厂 `setOffscreenPageLimit(2)` 保留两侧各两页，这里取 `beyondViewportPageCount = 1`，够画出左右相邻的一页。
- 原厂 `e()` / `CouiLoopShowTimeAnimator` 一类的"整条轮播整体位移"辅助动画未移植；旋转与尺寸变化后的位置恢复交由 `PagerState` 自身。
- 原厂翻页可外部注入 `PageTransformer`（缩放、透明度等），这里没有暴露该入口。
