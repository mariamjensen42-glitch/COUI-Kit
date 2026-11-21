# Backdrop

## Overview
背景采样与离屏效果包。`rememberCouiBackdrop` 创建采样源，`Modifier.couiBackdrop` 挂到正文节点录制内容，`Modifier.drawCouiBackdrop` 在消费者上应用模糊/自定义效果；`ProvideCouiBackdrop` 向下提供采样源。该链路承载凝光玻璃等背景材质。

## Interface
- `public @Composable fun rememberCouiBackdrop(parent, diagnosticName, drawSource): CouiBackdrop` [CouiBackdrop.kt:rememberCouiBackdrop]
- `public fun Modifier.couiBackdrop(backdrop): Modifier` [CouiBackdrop.kt:couiBackdrop]
- `public class CouiBackdrop`（internal 构造）：内部持有 `GraphicsLayer`，公开 `draw(scope, target)` [CouiBackdrop.kt:CouiBackdrop]
- `public class CouiBackdropEffectScope : Density`：`size`/`padding`/`renderEffect` 与 `blur(radius, edgeTreatment)` [DrawCouiBackdrop.kt:CouiBackdropEffectScope]
- `public @Composable fun Modifier.drawCouiBackdrop(backdrop, shape, effects, onDrawSurface, onDrawBackdrop): Modifier` [DrawCouiBackdrop.kt:drawCouiBackdrop]
- `public @Composable fun ProvideCouiBackdrop(backdrop, content)` [ProvideCouiBackdrop.kt:ProvideCouiBackdrop]
- `internal val LocalCouiBackdrop: ProvidableCompositionLocal<CouiBackdrop?>` [ProvideCouiBackdrop.kt:LocalCouiBackdrop]
- `internal open class DrawCouiBackdropNode`：效果装配与图层录制基类 [DrawCouiBackdrop.kt:DrawCouiBackdropNode]

## Invariants
- 每个源只挂到一个内容节点，该子树不得采样自身；`parent` 用于叠加所属窗口 [CouiBackdrop.kt:rememberCouiBackdrop]
- 消费者必须成对 `retain`/`release`；`release` 以 `activeConsumers > 0` 校验，源及父链随组合成对保留与释放 [CouiBackdrop.kt:retain]
- 源图层固定 `Offscreen` + `clip`；仅当无消费者且 `isolateWhenIdle = false` 时切换为 `Auto`，默认 `isolateWhenIdle = true` 时保持 `Offscreen` [CouiBackdrop.kt:updateCompositingStrategy]
- `draw` 仅在源与目标均 attached、且源已 `recorded` 时绘制；仅跨窗口时驱动 PreDraw 重绘 [CouiBackdrop.kt:draw]
- 坐标换算只支持二维仿射；组件引入三维翻转时需改用完整投影矩阵 [CouiBackdrop.kt:draw]
- `blur(radius <= 0)` 为空操作；`padding = max(padding, ceil(radius))`，并叠加 `BlurEffect` [DrawCouiBackdrop.kt:blur]
- 效果快照仅在 `effectsDirty`、尺寸或 density 变化时重建，滚动/动画期间复用 [DrawCouiBackdrop.kt:DrawCouiBackdropNode]
- `drawCouiBackdrop` 以 `shape` 裁剪采样、`surface` 与子内容，并强制 `Offscreen` [DrawCouiBackdrop.kt:drawCouiBackdrop]
- `ProvideCouiBackdrop` 只提供 CompositionLocal；宿主仍须把 `couiBackdrop` 挂到唯一正文节点并排除消费者 [ProvideCouiBackdrop.kt:ProvideCouiBackdrop]

## Side Effects
- 离屏绘制：`GraphicsLayer.record`/`drawLayer` 与 `CompositingStrategy.Offscreen` [CouiBackdrop.kt:CouiBackdrop]
- 平台 `RenderEffect`/`BlurEffect` 与 `TileMode` 滤镜 [DrawCouiBackdrop.kt:blur]
- 视图观察者：`ViewTreeObserver.OnPreDrawListener` 与 `OnWindowFocusChangeListener` 注册/注销 [CouiBackdrop.kt:BackdropNode]
- CompositionLocal 供给 `LocalCouiBackdrop` [ProvideCouiBackdrop.kt:ProvideCouiBackdrop]
- 诊断接入：`CouiRenderDiagnostics.attachSource`/`detachSource`/`event` [CouiBackdrop.kt:BackdropNode]

## Verification
- Run: `./gradlew :coui:lintDebug`
