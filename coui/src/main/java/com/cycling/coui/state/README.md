# State

## Overview
交互状态效果表面包。`StateEffectSurface` 将底色、悬停/焦点/按压蒙层、可选焦点描边与按压缩放组合成一个可点击容器，对应原厂 `COUIStateEffectDrawable` 的状态表现。

## Interface
- `public @Immutable data class CouiStateEffectColors(hover, focus, press, outline)` [CouiStateEffectDefaults.kt:CouiStateEffectColors]
- `public object CouiStateEffectDefaults`：`focusSpringResponse = .3f`、`pressSpringResponse = .3f`、`focusStrokeWidth` 与 `colors(dark)` [CouiStateEffectDefaults.kt:CouiStateEffectDefaults]
- `public @Composable fun StateEffectSurface(modifier, onClick, enabled, selected, containerColor, shape, maskType, focusOutline, scaleFeedback, dark, colors, interactionSource, content)` [StateEffectSurface.kt:StateEffectSurface]

## Invariants
- `interactionSource == null` 时用 `remember` 新建；蒙层、描边与 `clickable` 共用同一实例 [StateEffectSurface.kt:StateEffectSurface]
- 修饰符顺序固定：缩放 `graphicsLayer` → 可选 `couiPressMotion` → `clip(shape)` → `background` → `couiStateMask` → 可选 `couiFocusOutline` → 可选 `clickable` [StateEffectSurface.kt:StateEffectSurface]
- `scaleFeedback = false` 时缩放固定 1f 且不挂 `couiPressMotion`；为 true 时读取 `rememberCouiPressMotion().scale.value` [StateEffectSurface.kt:StateEffectSurface]
- `clickable` 仅在 `onClick != null` 时附加，使用同一交互源、`indication = null`、`Role.Button` 并透传 `enabled` [StateEffectSurface.kt:StateEffectSurface]
- `colors()` 的 hover/focus/press 取自 `CouiMaskDefaults.colors(dark)`，outline 取 `colorSchemeFor(dark).accent` [CouiStateEffectDefaults.kt:CouiStateEffectDefaults.colors]
- `focusStrokeWidth` 直接复用 `CouiMaskDefaults.FocusStrokeWidth`，不另建描边宽度 [CouiStateEffectDefaults.kt:CouiStateEffectDefaults]

## Side Effects
- `MutableInteractionSource`（外部注入或 `remember`）驱动蒙层、焦点描边与点击状态 [StateEffectSurface.kt:StateEffectSurface]
- 动画：`rememberCouiPressMotion` 的弹簧值经 `graphicsLayer` 驱动缩放 [StateEffectSurface.kt:StateEffectSurface]
- 绘制链：`clip`/`background`/`couiStateMask`/`couiFocusOutline` 叠加蒙层与焦点描边 [StateEffectSurface.kt:StateEffectSurface]
- 读取 `CouiTheme.isDark`、`CouiTheme.shapes.card`、`CouiMaskDefaults.colors(dark)` 与 `colorSchemeFor(dark)` [CouiStateEffectDefaults.kt:CouiStateEffectDefaults.colors]

## Verification
- Run: `./gradlew :coui:lintDebug`
