# Agent Note: 材质混色改为 COUI 17 的两层参数表

Status: implemented

## Problem

`rememberCouiMaterialEffect(mix, blend, blendMode)` 只有一个两值枚举 `CouiMaterialBlendMode { Overlay, ColorDodge }`，第一层固定按 luminosity 混色。原厂 `COUIMaterialBlurEffect` 的结构不同：每层是「常量色 + `android.graphics.BlendMode`」，并排两层 `BlendModeColorFilter`，模式覆盖 `SRC_OVER` / `OVERLAY` / `COLOR_DODGE` / `LUMINOSITY`，浅色顶栏第一层就是 `SRC_OVER`，深色 ChipTab 第一层是 `LUMINOSITY`——两值枚举表达不了这些组合。

同时各组件各自复制同一批字面值：`CouiTopAppBarButtonDefaults.systemMaterial`、`CouiSegmentedControlDefaults.material`、`CouiChipTabDefaults.material` 分别内联颜色与模式，没有共同来源，参数更新时会分叉。

本轮 dex 逆向取到了 Settings 17.0.0 的完整参数表（`references/apk/ANALYSIS.md` 第 3.1 节），可以据此统一。

## Decision

[材质](../../../../coui/src/main/java/com/itos/coui/effect/CouiMaterial.kt) 改为两层模型，[参数表](../../../../coui/src/main/java/com/itos/coui/effect/CouiMaterialDefaults.kt) 独立成文件：

- `CouiMaterialLayer(color, blendMode)` 表示一层，`blendMode` 用 `android.graphics.BlendMode`，与原厂 `BlendModeColorFilter` 和既有 `CouiFloatingMaterial` 同一词表，不再维护第二套枚举。
- Shader 用一个 `couiBlend(int mode, float3 src, float3 dst)` 分发四种模式，两层在同一个 RuntimeShader 里顺序执行，不额外申请两个 `RenderEffect`。依据是背景采样不透明时 Skia 的 `Cr = Sa·B(Dc,Sc) + (1-Sa)·Dc` 等价于 `mix(dst, blend(src,dst), src.a)`；`COLOR_DODGE` 保留项目既有写法 `dst / (1 - src)`，不引入 epsilon，与原实现逐字一致。
- `CouiMaterialShaderFunctions` 仍保留旧的 `half4 couiMaterial(half4, float4 mix, float4 blend)`（固定 LUMINOSITY + OVERLAY）。它是共享前缀，被 `CouiEdgeShader`、`CouiContourShader` 与三条 `couiFloating*Shader` 一起拼接，`CouiEdgeColorFunctions.lightEdgeReferenceColor` 与 `CouiEdge` 的 `main` 都在调用；删掉会让这些 RuntimeShader 在运行时抛出 `unknown identifier 'couiMaterial'`。描边链尚未迁移到两层模型，签名和语义都按原样冻结。
- `CouiMaterialPreset(blurRadiusPx, first, second)` 带 `isNoEffect`（半径为零且两层颜色都是 ARGB 0），对应原厂 `isNoEffect`；`rememberCouiMaterialEffect(preset)` 在无效果时返回 `null`，等价于原厂把 `setBackgroundRenderEffect` 置空。
- `CouiMaterialType` 按原厂枚举顺序列出 13 种类型，`CouiMaterialDefaults.of(type, dark)` 返回预设。`TopBar1` 在原厂 `getBlurParamsByType` 里不读 `dark`，这里同样忽略。
- `CouiMaterialDefaults.interpolate(start, end, blendModes, progress)` 还原原厂进度分支：半径线性插值、颜色按通道插值（原厂用 `ArgbEvaluator`）、两层模式固定取 `blendModes`，不随两端变化。
- 半径是像素，不做 density 换算：原厂 `BlurEffectParams.getBlurRadius(context)` 直接返回表里的数值并交给 `createBlurEffect`。
- 接线范围限于取值本就一致的组件：`systemMaterial` 用 `TopBar`，分段选择器用 `SegmentButton`，ChipTab 用 `ChipTab` / `ChipTabSelected` 插值。弹窗材质仍用 `coui_dialog_*` 资源，只把 `blendMode` 的类型换成 `BlendMode`，`mix` 映射为 `LUMINOSITY` 层。
- `DockBar`、`NavigationBar`、`FloatingToolbar` 不改写：它们的浅色胶囊是 `#A3666666` + `#80F0F0F0`，参数表 `BottomBar` 浅色是 `#B3666666` + `#99F5F5F5`，深色两侧一致。原厂三个悬浮底栏入口都申请 `TYPE_FRAMEWORK_BOTTOM_BAR`，差异应按真机对照后再定，本轮不静默改观感。

## Alternatives considered

- 保留 `CouiMaterialBlendMode` 并加两个值：仍与 `CouiFloatingMaterial` 的 `BlendMode` 并存，同一概念两套类型，参数表无法直接喂给两边。
- 照搬原厂两条 `RenderEffect.createColorFilterEffect`：语义一致但要两次链式特效分配，且 Compose 侧 `ColorFilter` 无法表达 `LUMINOSITY`，最终仍要写 Shader。用一层 Shader 同时表达两层，行为等价。
- 把参数表塞进 `CouiFloatingMaterial`：后者的字段还包含描边、光晕、主题染色，与混色参数不同生命周期；塞进去会让 `blurRadius` 等既有字段出现两个来源。
- 直接把底栏三处也切到参数表：缺少真机对照，属于改观感而非改来源，按待验收处理。
- 保留 `rememberCouiMaterialEffect(mix, blend, mode)` 旧签名做兼容重载：只服务库内两个调用点，重构后没有第二个消费者，改为直接迁移。

## Consequences

- 公开 API 破坏性变化：移除 `CouiMaterialBlendMode`，`rememberCouiMaterialEffect` 改为两层或预设入口，`CouiDialogMaterial.blendMode` 类型换成 `android.graphics.BlendMode`。升级说明已写入 CHANGELOG。
- `CouiMaterialDefaults` 的属性初始化会触碰 `BlendMode` 常量，要求 API 29；材质路径本身运行在 API 33 的弹窗与 API 34 的浮动表面上，低版本不进入该对象。
- ChipTab 的颜色来源由 `ArgbEvaluator` 换成 `Color.lerp`，逐通道结果只差量化舍入，观感不变；模式来源由硬编码换成选中档参数。
- 底栏浅色胶囊与参数表的差异保留为待验收项，写在使用文档里，不当作已完成对齐。
- 参数表只有一处维护点，后续按类型扩展组件时改 `CouiMaterialDefaults` 即可。

## Existing-note audit

- [悬浮操作栏](2026-11-12-coui-floating-toolbar.md)：部分重叠，它决定操作栏复用 `systemMaterial` 的值，本篇只换该值的来源，不翻转其结论。
- [顶部栏渐变模糊](../../../../.agents/notes/implemented/bug-fix/2026-10-18-coui-topbar-gradient-blur.md)：不重叠，那片管渐进模糊的卷积位置，本篇管两层混色参数，两者在顶栏同一节点上先后执行。
- [弹窗](../../../../.agents/notes/implemented/feature/2026-10-24-coui-alert-dialog.md)：部分重叠，弹窗材质参数仍来自 `coui_dialog_*`，本篇只改混色模式的类型与层表达。
