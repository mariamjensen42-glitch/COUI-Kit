# Menu

## Overview
菜单包实现 COUI 的浮层菜单：公开的 `Menu` 以 `CouiMenuItem` 列表驱动，支持两级子菜单，外观经 `CouiMenuStyle` / `CouiMenuDefaults` 通过 `LocalCouiMenuStyle` 下发。内部由 `CouiMenuPopup`、`CouiMenuList`、`CouiMenuWithSubMenu` 装配弹窗、选择状态与子菜单手势；`CouiMenuBackdrop`、`CouiMenuBlur`、`CouiMenuMaterial` 负责对宿主 `CouiBackdrop` 做离屏采样、Fast Kawase 模糊与合成。

## Interface
- `public data class CouiMenuItem<T>`：值、文本、图标、选中、启用、复选、前置分隔与子项。`[Menu.kt:CouiMenuItem]`
- `public @Composable fun <T> Menu(modifier, expanded, onDismissRequest, items, onItemClick, style, offset, properties, anchor, headerText, headerTextAlign, onSpotlightDiagnostic)`。`[Menu.kt:Menu]`
- `public @Stable class CouiMenuAnchor`（`open(onOpen)`，坐标与待处理触摸为内部状态）。`[CouiMenuAnchor.kt:CouiMenuAnchor]`
- `public @Composable fun rememberCouiMenuAnchor()`；`public fun Modifier.couiMenuAnchor(anchor)`。`[CouiMenuAnchor.kt:rememberCouiMenuAnchor]` `[CouiMenuAnchor.kt:couiMenuAnchor]`
- `public @Composable fun rememberCouiWindowBackdrop()`；`public @Composable fun ProvideCouiMenuBackdrop(isolateContent, content)` 两个重载。`[CouiMenuBackdrop.kt:rememberCouiWindowBackdrop]` `[CouiMenuBackdrop.kt:ProvideCouiMenuBackdrop]`
- `public @Composable fun rememberCouiMenuBlur(backdrop, background)`；`public @RequiresApi(33) class CouiMenuBlur`（`draw`）。`[CouiMenuBlur.kt:rememberCouiMenuBlur]` `[CouiMenuBlur.kt:CouiMenuBlur]`
- `public data class CouiMenuColors`、`public data class CouiMenuStyle`、`public object CouiMenuDefaults`（`style(dark)`、`radius`、`shape`、`itemShape`）。`[CouiMenuStyle.kt:CouiMenuColors]` `[CouiMenuStyle.kt:CouiMenuStyle]` `[CouiMenuStyle.kt:CouiMenuDefaults]`
- `public val CouiMenuOpenScaleSpring` / `CouiMenuOpenOpacitySpring`。`[CouiMenuMotion.kt:CouiMenuOpenScaleSpring]` `[CouiMenuMotion.kt:CouiMenuOpenOpacitySpring]`
- `internal` 弹窗与手势装配：`CouiMenuWithSubMenu`、`CouiMenuPopup`、`CouiMenuList`、`CouiMenuPanel`、`couiMenuItemMask`、`CouiMenuMotion`/`CouiMenuMotionPositionProvider`、`CouiMenuPositionProvider`、`CouiMenuScrollBar`/`couiMenuScrollBar`。`[CouiMenuSubMenu.kt:CouiMenuWithSubMenu]` `[Menu.kt:CouiMenuPopup]` `[Menu.kt:CouiMenuList]` `[CouiMenuSubMenu.kt:CouiMenuPanel]` `[CouiMenuItemMask.kt:couiMenuItemMask]` `[CouiMenuMotion.kt:CouiMenuMotion]` `[CouiMenuMotion.kt:CouiMenuMotionPositionProvider]` `[CouiMenuPositionProvider.kt:CouiMenuPositionProvider]` `[CouiMenuScrollBar.kt:CouiMenuScrollBar]` `[CouiMenuScrollBar.kt:couiMenuScrollBar]`
- `internal` 渲染装配：`CouiMenuMaterial`/`CouiMenuMaterialSource`/`couiMenuMaterial`、`CouiMenuShadow`/`rememberCouiMenuShadow`、`rememberCouiMenuEdge`/`CouiMenuEdge`、`couiMenuSpotlight`。`[CouiMenuMaterial.kt:couiMenuMaterial]` `[CouiMenuMaterial.kt:CouiMenuMaterialSource]` `[CouiMenuShadow.kt:rememberCouiMenuShadow]` `[CouiMenuShadow.kt:CouiMenuShadow]` `[CouiMenuEdge.kt:rememberCouiMenuEdge]` `[CouiMenuEdge.kt:CouiMenuEdge]` `[CouiMenuSpotlight.kt:couiMenuSpotlight]`
- `internal val LocalCouiMenuStyle`、`LocalCouiMenuDiagnostic`、`CouiMenuStyle.resolvedRadius` / `CouiMenuStyle.isDark`。`[CouiMenuStyle.kt:LocalCouiMenuStyle]` `[CouiMenuStyle.kt:CouiMenuStyle.resolvedRadius]` `[CouiMenuStyle.kt:CouiMenuStyle.isDark]`

## Invariants
- 仅支持两级菜单：`CouiMenuContent` 对子项仍含子项抛 `IllegalArgumentException`；任一含子项时切换到 `CouiMenuWithSubMenu`。`[Menu.kt:CouiMenuContent]`
- 宽度经 `widthIn` 限制在 `CouiMenuMinWidth` 128.dp 至 `CouiMenuMaxWidth` 280.dp；行高固定 `CouiMenuItemHeight` 40.dp，布局、命中与按下指示器共用。`[Menu.kt:CouiMenuContent]` `[Menu.kt:CouiMenuList]`
- 命中测试先减 `CouiMenuContentVerticalPadding` 与头部高度；头部、`dividerBefore` 带不可选，仅启用行可选；滑动选择仅在 `scrollState.maxValue == 0` 时运行。`[Menu.kt:CouiMenuList]`
- `CouiMenuSelection.handoffPress` 清空 `pressedIndex`、置 `pressedIndicatorIndex = 0` 与 `pressHandoff = true`，指示器据此跳过最小按压并从交接进度淡出。`[Menu.kt:CouiMenuSelection]` `[Menu.kt:CouiMenuPressedIndicator]`
- `CouiMenuPopup` 在 `!expanded && !motion.visible` 时不组合；进入展开时以 `MutatePriority.PreventUserInput` 停止滚动并滚到 0。`[Menu.kt:CouiMenuPopup]`
- `withMenuInput` 重建 `PopupProperties`：始终 `FLAG_WATCH_OUTSIDE_TOUCH`，关闭平台默认宽度与系统手势排除；收起时追加 `FLAG_NOT_FOCUSABLE` / `FLAG_NOT_TOUCHABLE`。`[Menu.kt:withMenuInput]`
- `Menu` 必须提供 `LocalCouiMenuStyle` 与 `LocalCouiMenuDiagnostic`，缺省读取样式会抛异常；`themeTintEnabled = style.themeTint.alpha > 0f`。`[Menu.kt:Menu]` `[CouiMenuStyle.kt:LocalCouiMenuStyle]`
- 展开时 `rememberCouiMenuMotion` 先置 `visible = true`，等待 `motion.path != null` 后再启动动画；收起只在动画结束后清 `visible` / `path`。`[CouiMenuMotion.kt:rememberCouiMenuMotion]`
- 子菜单：`expanded` / `items` 变化即清除主、子选择；过渡顺序固定为停滚、按需归零、`scrollTo(0)`、等待子面板高度、交接按压进度、弹簧动画，关闭动画结束后才重置触摸并恢复主列表。子菜单可见时主列表与弹窗滚动关闭。`[CouiMenuSubMenu.kt:CouiMenuWithSubMenu]`
- `CouiMenuAnchor.open(onOpen)` 把 `pendingTouchPosition` 转入 `clickPosition`、清空待处理触摸后调用 `onOpen()`；无点击位置或坐标未附着时 `positionInWindow` 为 null。`[CouiMenuAnchor.kt:open]` `[CouiMenuAnchor.kt:positionInWindow]`
- 模糊：`rememberCouiKawaseBlur` 低于 API 33 返回 null；`CouiMenuBlur` 要求已附着坐标与正尺寸；10 个离屏 `GraphicsLayer` 中第 0 层每次绘制重新录制，第 1..9 层在捕获尺寸、density、fontScale 与 layoutDirection 均未变时复用。`[CouiMenuBlur.kt:rememberCouiKawaseBlur]` `[CouiMenuBlur.kt:CouiMenuBlur]`
- 材质仅在存在 backdrop 且 `expanded || motion.visible` 时构建；阴影 `drawing` 修饰符置于未变换外层 Box、`surface` 置于面板 Column，不可合并到同一 LayoutNode。`[Menu.kt:CouiMenuContent]` `[CouiMenuShadow.kt:CouiMenuShadow]`

## Side Effects
- 通过 `Menu` 提供 `LocalCouiMenuStyle`、`LocalCouiMenuDiagnostic`；消费 `LocalCouiMenuBackdrop`、`LocalCouiContourGlowEnabled`、`LocalView` / `LocalDensity` / `LocalHapticFeedback`。`[Menu.kt:Menu]` `[CouiMenuStyle.kt:LocalCouiMenuStyle]` `[CouiMenuBackdrop.kt:LocalCouiMenuBackdrop]` `[CouiMenuEdge.kt:rememberCouiMenuEdge]`
- 弹窗托管 `androidx.compose.ui.window.Popup` 与 `WindowManager` 标志；窗口/视图坐标互转使用 `getLocationOnScreen`、`getWindowVisibleDisplayFrame` 与 `localToWindow`。`[Menu.kt:CouiMenuPopup]` `[Menu.kt:withMenuInput]` `[CouiMenuAnchor.kt:visibleWindowBounds]`
- Backdrop 采样：`ProvideCouiMenuBackdrop` 将 `menu-window` 子 Backdrop 挂到 `Modifier.couiBackdrop`；材质以 `RetainWhileComposed` 持有整个组合期。`[CouiMenuBackdrop.kt:ProvideCouiMenuBackdrop]` `[CouiMenuMaterial.kt:couiMenuMaterial]`
- 渲染后端：API 33+ 使用一个降采样与九个模糊 RuntimeShader（10 层 `GraphicsLayer`）；API 33 以下回退 `BlurEffect` 加 `BlendMode.Luminosity` / `Overlay`。`[CouiMenuBlur.kt:CouiMenuBlur]` `[CouiMenuMaterial.kt:couiMenuMaterial]`
- 指针输入：`pointerInput` 在 Initial / Main / Final 通道消费事件，`couiMenuAnchor` 不消费事件；`systemGestureExclusion` 仅在 `properties.excludeFromSystemGesture` 为真时启用。`[Menu.kt:CouiMenuList]` `[CouiMenuAnchor.kt:couiMenuAnchor]` `[Menu.kt:CouiMenuPopup]`
- 无障碍与触觉：`AccessibilityManager.isTouchExplorationEnabled` 抑制滑动选择，复选行使用 `Role.Checkbox`，移动行触发 `HapticFeedbackType.TextHandleMove`。`[Menu.kt:CouiMenuList]` `[Menu.kt:CouiMenuRow]`
- 资源与动画：`painterResource(R.drawable.coui_pop_up_next)` 加载子菜单箭头；`CouiStateProgress`、`animateDpAsState`（stiffness 1754.5962f）与弹簧驱动指示器。`[Menu.kt:CouiMenuRow]` `[Menu.kt:CouiMenuPressedIndicator]`

## Verification
- Run: `./gradlew :coui:lintDebug`
