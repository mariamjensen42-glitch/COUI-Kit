# 模糊与材质混色

需要柔化后景时使用模糊，需要调整采样颜色时叠加材质效果。两者的输入仍来自[背景采样源](backdrop.md)。

## 为已有背景源添加材质

下面片段位于 API 33 及以上的 Compose 内容中。backdrop 为已有源，shape 是宿主 Shape，两层 `CouiMaterialLayer` 依次叠在采样结果上。

~~~kotlin
val material = rememberCouiMaterialEffect(
    CouiMaterialLayer(Color(0x66333333), BlendMode.COLOR_DODGE),
    CouiMaterialLayer(Color(0x99F5F5F5), BlendMode.LUMINOSITY),
)
Box(
    Modifier.fillMaxWidth().height(112.dp).drawCouiBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = { renderEffect = material.asComposeRenderEffect() },
    ),
)
~~~

每层是「常量色 + `BlendMode`」，与原厂 `RenderEffect.createColorFilterEffect(BlendModeColorFilter(color, mode), 上一层)` 同构，只用一层 Shader 完成。支持 `SRC_OVER`、`OVERLAY`、`COLOR_DODGE`、`LUMINOSITY`；其余模式在构造 `CouiMaterialLayer` 时直接拒绝。

均匀模糊可直接使用 CouiBackdropEffectScope.blur，示例见[背景采样](backdrop.md)。需要同时模糊和混色时，先处理采样模糊，再处理材质，保留模糊采样外沿 padding；完整 RenderEffect 链见 [DemoVisualEffects](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoVisualEffects.kt)。

## 使用 COUI 17 参数表

`CouiMaterialDefaults.of(type, dark)` 返回该类型的 `CouiMaterialPreset`：模糊半径与两层混色。取值来自 Settings 17.0.0 的 `COUIMaterialBlurEffect` 静态块，深浅色各一套；`TopBar1` 在原厂不区分深浅色，`dark` 对它无效。半径是像素——原厂把表里的数值直接交给 `createBlurEffect`，不做 density 换算。

~~~kotlin
val preset = CouiMaterialDefaults.of(CouiMaterialType.BottomBar, dark)
val effect = rememberCouiMaterialEffect(preset) // NoEffect 或全零参数时返回 null
~~~

`CouiMaterialType` 覆盖框架顶栏、顶栏模糊、搜索栏、底栏、次要与透明按钮、分段按钮和四档 ChipTab。`NoEffect` 以及半径为零且两层颜色全透明的组合下 `isNoEffect` 为真，`rememberCouiMaterialEffect(preset)` 相应返回 `null`，与原厂清空背景渲染效果的分支一致。

状态切换用 `CouiMaterialDefaults.interpolate(start, end, blendModes, progress)`：半径线性插值，颜色按通道插值，两层混合模式固定取 `blendModes` 那一档——原厂 `setViewBlurEffect(view, start, end, blendModeParams, progress)` 的第三个参数不参与插值。

## 选择正确入口

| 目标       | 入口与行为                                              |
|----------|----------------------------------------------------|
| 均匀背景模糊   | drawCouiBackdrop 的 effects / blur，半径使用本地像素         |
| 顶栏渐进模糊   | TopAppBarSurface；完整顶栏共用该背景，低层 drawCouiGradientBackdrop 使用位置可变卷积，`blurFactor` 整体缩放卷积半径 |
| 菜单快速模糊   | rememberCouiMenuBlur / CouiMenuBlur.draw，由工厂管理中间图层 |
| 已有覆盖率模糊链 | CouiCoverageBlurEffect                             |
| 材质混色     | rememberCouiMaterialEffect，两层 `CouiMaterialLayer` 或一个 `CouiMaterialPreset` |
| 按类型取材质   | CouiMaterialDefaults.of / interpolate，参数表见上一节        |

顶栏的渐进 Gaussian、菜单 Fast Kawase 和均匀模糊是不同算法。菜单工厂在旧平台返回 null，由菜单自己的兼容路径处理；不能将它当作同等 Fast Kawase 结果。

## 坐标、资源与平台

低层渐进接口的长度使用像素，位置与源坐标保持一致。材质与模糊是两件事：`CouiMaterialPreset.blurRadiusPx` 由调用方交给模糊入口，材质本身只负责混色。材质工厂随层参数更新；RenderEffect 是参数快照。

`drawCouiGradientBackdrop` 可复用到不同尺寸的节点。每个节点拥有独立的横向、纵向图层和 Shader，参数或 Density 改变后更新本节点的 renderer，保留先横后纵的卷积。渐进 Shader 在节点首次绘制时创建；异常直接暴露，不提供替代模糊。

RuntimeShader 的相关效果要求 API 33，具体分支见[兼容要求](../guide/compatibility.md)。参数表里的 `BlendMode` 常量要求 API 29，材质路径本身在 API 34 的浮动表面和 API 33 的弹窗上使用。Shader 初始化失败按[资源规则](backdrop.md#资源与失败处理)报告。

## 与参数表的有意差异

`DockBar`、`NavigationBar` 与 `FloatingToolbar` 的浅色胶囊仍用 `#A3666666` + `#80F0F0F0`，参数表 `BottomBar` 浅色是 `#B3666666` + `#99F5F5F5`；深色两侧一致。原厂 `BaseBottomFloatingMenuView`、`COUIBottomFloatingAccessoryView` 与 `COUIBottomFloatingToolbarMenuView` 都申请 `TYPE_FRAMEWORK_BOTTOM_BAR`，因此这处浅色差异属于待验收项：未在真机按同一背景对照前，不按参数表改写既有观感。

顶栏按折叠进度缩放模糊：`blurFactor = 1 - collapsedFraction`，与位置可变卷积是两个独立乘子，最终整数半径由「位置 ratio × 强度因子」共同决定。原厂 `AppBarBlurHelper.updateGradientBlurFraction` 把 start/end 两个半径同乘 `1 - fraction`，这里在 Shader 内等效实现；`blurFactor` 只缩半径，不改变 `fadeHeight`、背景延伸量与渐变分布，整层可见度仍由 `backgroundAlpha` 控制。

背景 alpha 跟随滚动：可见 appbar offset 优先，无折叠数据源时回退越过折叠范围后累计的内容滚动距离，阈值 30dp（原厂 `ALPHA_SCROLL_THRESHOLD_DP`）。API 33 以下分支没有等价的半径缩放通道，按兼容要求保持三层固定半径，不视为原厂路径等价。

## 公开入口

[材质与参数表](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/effect/CouiMaterial.kt)、[材质预设](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/effect/CouiMaterialDefaults.kt)、[渐进模糊](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/effect/CouiGradientBlur.kt)、[覆盖率模糊](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/effect/CouiCoverageBlurEffect.kt)、[菜单模糊工厂](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/menu/CouiMenuBlur.kt)。需要完整控件时使用[顶栏](../components/top-app-bar.md)或[菜单](../components/menu.md)。
