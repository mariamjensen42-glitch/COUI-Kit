# BottomSheet

## Overview
底部面板组件包。`BottomSheet` 以 `Dialog` 承载贴边面板，自绘遮罩与滑入动画，支持跟手拖动、内容滚动联动与返回键关闭；面板内容完全由调用方决定。

## Interface
- `public @Immutable data class CouiBottomSheetColors(container, scrim, handle)` [BottomSheet.kt:CouiBottomSheetColors]
- `public object CouiBottomSheetDefaults`：32dp 圆角/`shape`、阴影、上下边距、把手尺寸、进退场时长、`hideVelocity`、`maxPanelHeight`、`maxHeight(...)` 与 `colors(dark)` [BottomSheet.kt:CouiBottomSheetDefaults]
- `public @Composable fun BottomSheet(modifier, visible, onDismissRequest, dark, colors, content)` [BottomSheet.kt:BottomSheet]
- `internal val enterEasing`/`exitEasing`/`settleSpec`：进退场曲线与松手回弹弹簧 [BottomSheet.kt:CouiBottomSheetDefaults]

## Invariants
- `visible` 受控：遮罩点击与返回键只回调 `onDismissRequest`，面板不自行关闭 [BottomSheet.kt:BottomSheet]
- 退场动画结束后才销毁窗口；退场期间 `visible = false` 仍保留窗口，调用点必须留在组合中，不能用 `if (visible)` 包裹 [BottomSheet.kt:BottomSheet]
- 窗口不启用系统压暗与窗口动画，遮罩与滑入由组件自绘 [BottomSheet.kt:BottomSheet]
- 松手结算：向上速度回展开位；向下速度超过 `hideVelocity` 且大于横向速度、或拖过面板一半时收起 [BottomSheet.kt:settle]
- 内容未到顶部时下拉先交给内容滚动，到顶后由面板接管（`onPreScroll` 顺序）[BottomSheet.kt:nestedScrollConnection]
- 面板四周（含底部）保留 8dp `followHandMargin`，呈现浮起卡片而非贴满屏幕；仅顶部两角圆角，高度上限为 `maxHeight(screenHeight, hasNavigationBar)` [BottomSheet.kt:CouiBottomSheetDefaults]
- 面板仅作为命中目标挡住遮罩，不消费事件，子节点点击与拖动照常生效 [BottomSheet.kt:BottomSheet]
- 遮罩透明度按 `progress` 与面板露出比例共同衰减 [BottomSheet.kt:BottomSheet]

## Side Effects
- 窗口平台 API：`Dialog` + `WindowManager` 设置 `Gravity.BOTTOM`、透明背景、禁用窗口动画、`dimAmount = 0`、清除 DIM/BLUR flags、`SOFT_INPUT_ADJUST_NOTHING` [BottomSheet.kt:BottomSheet]
- 指针输入：遮罩 `detectTapGestures`、面板 `draggable` 跟手、透明 `pointerInput` 挡遮罩 [BottomSheet.kt:BottomSheet]
- 嵌套滚动：自建 `NestedScrollConnection` 消费 `UserInput` [BottomSheet.kt:nestedScrollConnection]
- 窗口 inset：`WindowInsets.ime`、`navigationBars`、`tappableElementIgnoringVisibility` [BottomSheet.kt:BottomSheet]
- 动画：`Animatable(progress)` 与 `animate(...)` 结算，运行于 `rememberCoroutineScope` [BottomSheet.kt:BottomSheet]
- 绘制：`drawBehind` 遮罩、`graphicsLayer` 位移、`shadow` 与 `clip`/`background` [BottomSheet.kt:BottomSheet]

## Verification
- Run: `./gradlew :coui:lintDebug`
