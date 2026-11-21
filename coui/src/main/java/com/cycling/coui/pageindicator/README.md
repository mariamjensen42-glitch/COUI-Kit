# PageIndicator

## Overview
页码指示器组件包。`PageIndicator` 复刻原厂 `COUIPageIndicator` 的连续半径插值与滑动窗口，宿主通过 `selectedIndex` 控制当前页；可选点按回调定位到具体圆点。

## Interface
- `public @Immutable data class CouiPageIndicatorColors(dot, selectedDot, stroke)` [PageIndicator.kt:CouiPageIndicatorColors]
- `public object CouiPageIndicatorDefaults`：三档圆点尺寸、间距、`maxDots = 6`、描边宽度与 `colors(dark)` [PageIndicator.kt:CouiPageIndicatorDefaults]
- `public @Composable fun PageIndicator(modifier, count, selectedIndex, dark, colors, onDotClick)` [PageIndicator.kt:PageIndicator]

## Invariants
- `count <= 0` 直接返回、不占位；越界 `selectedIndex` 先夹取到 `0..count - 1` [PageIndicator.kt:PageIndicator]
- `visible = count.coerceAtMost(maxDots)`：超过 6 个点只按 6 个点排版，窗口随当前页滑动 [PageIndicator.kt:PageIndicator]
- 半径由四段距离带 + `CubicBezierEasing(0.33f, 0f, 0.67f, 1f)` 连续插值，当前点固定 `d = 3` [PageIndicator.kt:radiusAt]
- 每个点占 `dotSize + 2 × spacing`，按半径累加后整体居中；切换当前页不改变组件占位 [PageIndicator.kt:layoutDots]
- 点按仅在按下到抬起不超过 `TapTimeoutMillis = 100L` 时生效；未命中圆点取水平最近点 [PageIndicator.kt:PageIndicator]
- RTL 下镜像圆心与点序，命中判定仍按绝对坐标比较 [PageIndicator.kt:layoutDots]
- 未选中点仅在 `colors.stroke.alpha > 0f` 时绘制内描边 [PageIndicator.kt:PageIndicator]

## Side Effects
- 指针输入：`awaitEachGesture` + `waitForUpOrCancellation`，回调经 `rememberUpdatedState` 读取 [PageIndicator.kt:PageIndicator]
- 绘制：`Canvas` 上 `drawCircle` 填充/描边 [PageIndicator.kt:PageIndicator]
- 读取 `LocalLayoutDirection` 处理 RTL [PageIndicator.kt:PageIndicator]
- 读取 `CouiTheme.isDark` 与 `colorSchemeFor` 派生默认配色 [PageIndicator.kt:CouiPageIndicatorDefaults.colors]

## Verification
- Run: `./gradlew :coui:lintDebug`
