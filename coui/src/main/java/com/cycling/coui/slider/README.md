# Slider

## Overview
COUI 滑杆组件包。公开入口 `Slider` 是受控 Composable，逻辑值与绘制位置分离：`CouiSliderMotion` 持有弹簧、拖动/释放/惯性状态机并回调值变化，`CouiSliderRenderer` 用 Android Canvas 绘制轨道、刻度、纹理拇指与进度闪光，`CouiSliderDefaults` 仅提供配色工厂。

## Interface
- `Slider` — public @Composable 受控滑杆：modifier、value、onValueChange、colors、dark、shape、valueRange、steps、showTickMarks、enabled、onValueChangeFinished `[Slider.kt:Slider]`
- `CouiSliderColors` — public @Immutable 六色数据类：activeTrack/inactiveTrack/thumb/activeTick/inactiveTick/thumbShadow `[CouiSliderRenderer.kt:CouiSliderColors]`
- `CouiSliderDefaults` / `CouiSliderDefaults.colors` — public 默认对象与按 primary/dark/enabled 生成配色的普通工厂函数 `[CouiSliderDefaults.kt:CouiSliderDefaults]` `[CouiSliderDefaults.kt:CouiSliderDefaults.colors]`
- `CouiSliderMotion` — internal 状态机：六个 CouiSpring 与 value/length/fraction/visualFraction，暴露 syncValue/resize/press/drag/release/cancelGesture/setFromAction/dispose `[CouiSliderMotion.kt:CouiSliderMotion]`
- `CouiSliderRenderer` — internal Canvas 渲染器：draw/clipTrack/dispose `[CouiSliderRenderer.kt:CouiSliderRenderer]`

## Invariants
- require steps>=0 且 steps<Int.MAX_VALUE，value 与 valueRange 两端有限，endInclusive>=start 且跨度有限 `[Slider.kt:Slider]`
- interactive = enabled && endInclusive > start；非交互时语义置 disabled，setProgress 对非有限值/非交互返回 false，键盘与指针忽略 `[Slider.kt:Slider]`
- 键盘步长 span/(steps+1)（steps>0 或 showTickMarks 时），否则 span/100；RTL 下方向取反，Home/End 直达端点 `[Slider.kt:Slider]`
- 画布宽度变化时 motion.resize(width - 28f*density)；指针 local 坐标扣 pointerOffset、translationX 与 14f*density，RTL 取 length-local `[Slider.kt:Slider]`
- 纵向位移超过 touchSlop 且大于横向时中断并交还父级；isCouiTouchCancelled、多指切换或已消费变更同样中断 `[Slider.kt:Slider]`
- UP 只有 dragging 或坐标仍在组件内才提交，否则 cancelGesture；未完成路径在 finally 中 cancelGesture 并 touch.release(Offset.Zero) `[Slider.kt:Slider]`
- 释放外观：height 运行中且 heightFraction<0.5 时置 pendingRelease，否则 height 归 0；thumbRadius 回 6、texture 回 0、thumbCover 回 100 `[CouiSliderMotion.kt:releaseAppearance]`
- drag 仅在 pressed 且 length>0 时生效，fingerX 夹在 0..length；minimumChange=length/1000 `[CouiSliderMotion.kt:drag]`
- syncValue 在外部值未变或归一化档位相同时直接返回，否则取消手势与位置动画后写入并 snap；resize 同样先取消手势 `[CouiSliderMotion.kt:syncValue]`
- setFraction 将归一化值夹到 0..1；steps>0 或 section 时按 intervals=steps+1 量化；仅值变化才回调；震动码 306 端点、308 section、305 刻度 `[CouiSliderMotion.kt:setFraction]`
- release 拖拽释放先 snap 到 fingerX，|velocity|>=100 才起惯性（夹 -8000..8000），否则立即 onValueChangeFinished `[CouiSliderMotion.kt:release]`
- 惯性每帧 velocity /= 1+15f*frameSeconds，x 越界 0..length 或 |velocity|<0.1 时结束并回调 `[CouiSliderMotion.kt:startFling]`
- section 拖动以 0.1 偏置计算跨越段数，方向反转重建 anchor；dragSnap 时长 100ms `[CouiSliderMotion.kt:dragSection]`
- 渲染几何：轨道起点 14f*density、圆角 10f*density、背景高度 20f+8f*heightFraction、刻度半径 3f*density；RTL 下镜像 `[CouiSliderRenderer.kt:CouiSliderRenderer.draw]`
- 轨道 Path 由宿主 Shape.createOutline 生成，仅 bounds 变化时重建；clipTrack 用背景 Path 裁剪 `[CouiSliderRenderer.kt:updateTrackPath]`
- 默认色：暗色 inactive 0x33FFFFFF、亮色 0x1F000000；禁用时 activeTrack 与 inactiveTrack 同用 inactive `[CouiSliderDefaults.kt:CouiSliderDefaults.colors]`
- showTickMarks 时向 renderer 传 tickCount=steps+1，绘制 tickCount+1 个刻度 `[Slider.kt:Slider]`

## Side Effects
- pointerInput：多指切换、touchSlop 判断与 consume 指针变更 `[Slider.kt:Slider]`
- progressSemantics/setProgress/disabled 语义，focusable 与 onKeyEvent 键盘交互，默认最小尺寸 144dp x 48dp `[Slider.kt:Slider]`
- performHapticFeedback，失败时 section 的 308 回退 302，再按 SDK 回退 GESTURE_END、SEGMENT_TICK/SEGMENT_FREQUENT_TICK 或 CLOCK_TICK `[Slider.kt:performSliderHaptic]`
- Choreographer.postFrameCallback 驱动惯性帧，ValueAnimator 驱动 section 过渡与拖动吸附 `[CouiSliderMotion.kt:startFling]`
- Android Canvas/Paint/Path/LinearGradient/saveLayerAlpha 与 ARGB_8888 Bitmap 绘制，dispose 时 recycle 缓存 Bitmap `[CouiSliderRenderer.kt:CouiSliderRenderer]`
- rememberCouiSpotlight 采样并绘制 OpaqueMedium1 光效，绘制前用 renderer.clipTrack 裁剪 `[Slider.kt:Slider]`
- CouiTouchMotion 触摸回弹与 graphicsLayer 的 translationX/Offscreen 合成 `[Slider.kt:Slider]`

## Verification
- Run: `./gradlew :coui:lintDebug`
