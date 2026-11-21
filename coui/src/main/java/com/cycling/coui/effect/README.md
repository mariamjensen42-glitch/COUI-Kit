# Effect

## Overview
COUI 视觉基础效果包：提供阴影网格与渲染器、漂浮材质/融球、描边与内光晕、跟手聚光、渐变模糊、材质混色、轮廓 shader，以及按压/悬停/焦点状态反馈。组件包通过 Modifier 扩展或 renderer 装配这些效果；GPU 效果基于 RuntimeShader/RenderEffect（关键入口 @RequiresApi(33)），本包不持有业务状态。

## Interface
- `public val LocalCouiContourGlowEnabled` - CompositionLocal<Boolean>，统一开关描边与内光晕 `[CouiEffectLocals.kt:LocalCouiContourGlowEnabled]`
- `public data class CouiShadowStyle` / `public object CouiShadowDefaults` / `public fun Modifier.couiShadow(...)` - 普通投影配置与绘制 `[CouiShadow.kt:CouiShadowStyle]` `[CouiShadow.kt:CouiShadowDefaults]` `[CouiShadow.kt:Modifier.couiShadow]`
- `public class CouiShadowRenderer(surfaceCount = 1)`、`public enum class CouiShadowOutline` - 按槽位的 ambient/spot 投影渲染器与 caster 轮廓 `[CouiShadowRenderer.kt:CouiShadowRenderer]` `[CouiShadowRenderer.kt:CouiShadowOutline]`
- `public data class CouiFloatingMaterial` / `CouiFloatingShadow`、`public data class CouiFloatingSurface`、`public class CouiFloatingRenderer`（companion `standalone()/pair()`） - 漂浮材质参数、主体几何与渲染器 `[CouiFloatingSurfaces.kt:CouiFloatingMaterial]` `[CouiFloatingSurfaces.kt:CouiFloatingShadow]` `[CouiFloatingGeometry.kt:CouiFloatingSurface]` `[CouiFloatingSurfaces.kt:CouiFloatingRenderer]`
- `public fun Modifier.couiFloatingSurface(...)` / `couiFloatingSurfaces(...)` / `couiFloatingPair(...)` - 单主体、三主体与双主体材质装配 `[CouiFloatingSurfaces.kt:Modifier.couiFloatingSurface]` `[CouiFloatingSurfaces.kt:Modifier.couiFloatingSurfaces]` `[CouiFloatingSurfaces.kt:Modifier.couiFloatingPair]`
- `public enum class CouiEdgeStyle`、`public class CouiEdge` - 描边/内晕 RenderEffect 生成器 `[CouiEdge.kt:CouiEdgeStyle]` `[CouiEdge.kt:CouiEdge]`
- `public enum class SpotlightPreset`、`public sealed interface SpotlightDiagnostic`、`public fun rememberCouiSpotlight(...)` - 跟手聚光预设、诊断事件与 Modifier/绘制回调对 `[CouiSpotlight.kt:SpotlightPreset]` `[CouiSpotlight.kt:SpotlightDiagnostic]` `[CouiSpotlight.kt:rememberCouiSpotlight]`
- `public fun Modifier.drawCouiGradientBackdrop(...)` - 带渐变高度与速度的背板模糊 `[CouiGradientBlur.kt:drawCouiGradientBackdrop]`
- `public data class CouiMaterialLayer` / `CouiMaterialPreset`、`public fun rememberCouiMaterialEffect(...)` - 材质两层混色与 RenderEffect 缓存 `[CouiMaterial.kt:CouiMaterialLayer]` `[CouiMaterial.kt:CouiMaterialPreset]` `[CouiMaterial.kt:rememberCouiMaterialEffect]`
- `public enum class CouiMaterialType`、`public object CouiMaterialDefaults` - COUI 17 材质参数表（13 种类型）与 interpolate `[CouiMaterialDefaults.kt:CouiMaterialType]` `[CouiMaterialDefaults.kt:CouiMaterialDefaults]`
- `public data class CouiMetaballBody`、`public class CouiMetaballRenderer` - 双主体融球绘制 `[CouiMetaballRenderer.kt:CouiMetaballBody]` `[CouiMetaballRenderer.kt:CouiMetaballRenderer]`
- `public enum class CouiContourStyle`、`public class CouiContour` - 轮廓 shader 预设与生成器 `[CouiContour.kt:CouiContourStyle]` `[CouiContour.kt:CouiContour]`
- `public class CouiCoverageBlurEffect` - 覆盖率归一化模糊，API 33+ 包装 normalizeCoverage `[CouiCoverageBlurEffect.kt:CouiCoverageBlurEffect]`
- `public object CouiPressRippleDefaults`、`public fun Modifier.couiPressRipple(...)` - 按压水波纹节奏与绘制 `[CouiPressRipple.kt:CouiPressRippleDefaults]` `[CouiPressRipple.kt:couiPressRipple]`
- `public fun Modifier.couiNavigationFeedback/couiFabFeedback/couiToolbarFeedback/couiButtonFeedback(...)` - 各控件 hover/focus/按压蒙层 `[CouiStateFeedback.kt:couiNavigationFeedback]` `[CouiStateFeedback.kt:couiFabFeedback]` `[CouiStateFeedback.kt:couiToolbarFeedback]` `[CouiStateFeedback.kt:couiButtonFeedback]`
- `public enum class CouiMaskType` / `CouiStrokeType`、`public data class CouiMaskColors`、`public object CouiMaskDefaults`、`public fun Modifier.couiStateMask(...)` / `couiFocusOutline(...)` - 状态蒙层与焦点描边 `[CouiStateMask.kt:CouiMaskType]` `[CouiStateMask.kt:CouiMaskColors]` `[CouiStateMask.kt:CouiMaskDefaults]` `[CouiStateMask.kt:couiStateMask]` `[CouiStateMask.kt:couiFocusOutline]`

## Invariants
- CouiShadowStyle init require：elevation 有限且 >=0、lightZ 严格大于 elevation、半径有限且 >=0；couiShadow 要求 !clipping 或 outline 非 RoundRect `[CouiShadow.kt:CouiShadowStyle]` `[CouiShadow.kt:Modifier.couiShadow]`
- CouiShadowRenderer 按 SDK 分派后端：>=34 用 Mesh，<34 用 RenderNode+RenderEffect，clipShader 仅 >=35；每 slot 固定 index 0 为 ambient、1 为 spot `[CouiShadowRenderer.kt:CouiShadowRenderer.draw]`
- 阴影几何顺序固定为 line/conic 累积、finish()、mesh()；fast 分支要求 isFastShadowGeometry 成功；量化在缩放/平移规范化之后 `[CouiShadowGeometry.kt:floatingShadowGeometry]`
- CouiFloatingSurface.bounds 为未缩放绘制坐标，缩放围绕中心，cornerRadius 用像素、null 表示短边一半 `[CouiFloatingGeometry.kt:CouiFloatingSurface]`
- couiFloatingSurface 的 internal 重载 require renderer.standalone；couiFloatingSurfaces require surfaceCount==3、四列表 size 3、materials.index 为 [1,2,2]；couiFloatingPair require surfaceCount==2、index 为 [1,2] `[CouiFloatingSurfaces.kt:Modifier.couiFloatingSurfaces]` `[CouiFloatingSurfaces.kt:Modifier.couiFloatingPair]`
- CouiEdge init 约束 radiusPx/cornerWeight/innerGlowAlpha/lineAlpha；renderEffect 按 (size, density) 命中缓存，不复用旧尺寸快照 `[CouiEdge.kt:CouiEdge]`
- CouiMaterialLayer 只允许 SRC_OVER/OVERLAY/COLOR_DODGE/LUMINOSITY；materialEffect 两层顺序固定先 first 后 second `[CouiMaterial.kt:CouiMaterialLayer]` `[CouiMaterial.kt:materialEffect]`
- CouiContour 的 floating 必须搭配 Menu 预设；shader() 校验半径、权重与 alpha 范围，floating 描边宽度固定 2.5*density `[CouiContour.kt:CouiContour]`
- 融球 toBallArray 先乘 CouiMetaballCornerScale(1.5) 再夹到半宽/半高，顺序不可反 `[CouiMetaballRenderer.kt:toBallArray]`
- rememberCouiSpotlight 在 SDK < TIRAMISU 时返回 Modifier to null；release 一次熄灭只回调一次 Released `[CouiSpotlight.kt:rememberCouiSpotlight]` `[CouiSpotlight.kt:SpotlightState.release]`
- couiPressRipple 不可与 couiStateMask/couiButtonFeedback 叠加，且同一 interactionSource 只能交给一个点击 owner `[CouiPressRipple.kt:couiPressRipple]`
- CouiStateProgress 内部以 0..10000 表示进度、MinimumPress=7000；couiStateMask 三通道各自独立，disabled 时直接 reset `[CouiStateProgress.kt:CouiStateProgress]` `[CouiStateMask.kt:couiStateMask]`

## Side Effects
- 各渲染器创建 RuntimeShader、RenderEffect、android.graphics.Mesh/MeshSpecification、RenderNode DisplayList，并调用 drawMesh/drawRenderNode/setFloatUniform；阴影渲染器构造 Bitmap ALPHA_8 LUT 与 BitmapShader `[CouiShadowRenderer.kt:CouiShadowRenderer]` `[CouiEdge.kt:CouiEdge]` `[CouiSpotlight.kt:runtimeSpotlight]`
- LocalCouiContourGlowEnabled 是模块级 CompositionLocal 单例，未提供时回退 true `[CouiEffectLocals.kt:LocalCouiContourGlowEnabled]`
- 漂浮材质通过 rememberGraphicsLayer、GraphicsLayer.record/drawLayer、CouiBackdrop 采样与 canvas.nativeCanvas 合成；CouiCoverageBlurEffect 构造平台模糊 RenderEffect `[CouiFloatingSurfaces.kt:floatingSurfaces]` `[CouiCoverageBlurEffect.kt:CouiCoverageBlurEffect]`
- 状态反馈注册 hoverable/pointerInput，订阅 InteractionSource，使用 drawBehind/drawWithCache 绘制；couiButtonFeedback 还注册 ViewTreeObserver.OnWindowFocusChangeListener `[CouiStateFeedback.kt:couiButtonFeedback]` `[CouiStateMask.kt:couiStateMask]`
- 动画由 Compose Animatable/LaunchedEffect、CouiStateProgress/CouiSpring、dynamicanimation SpringAnimation 驱动；ArgbEvaluator 为文件级 private 单例 `[CouiStateProgress.kt:CouiStateProgress]` `[CouiStateFeedback.kt:MaskEvaluator]` `[CouiPressRipple.kt:couiPressRipple]`
- 读取 LocalDensity、LocalView、DisplayMetrics/display.getRealMetrics、Build.VERSION.SDK_INT 与 canvas.isHardwareAccelerated 选择绘制路径 `[CouiFloatingSurfaces.kt:floatingSurfaces]` `[CouiSpotlight.kt:runtimeSpotlight]` `[CouiCoverageBlurEffect.kt:CouiCoverageBlurEffect]`

## Verification
- Run: `./gradlew :coui:lintDebug`
