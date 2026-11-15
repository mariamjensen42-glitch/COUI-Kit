# ✨状态效果表面

原厂 `appcompat/state/` 是一套状态效果 Drawable：`COUIStateEffectDrawable` 组合图层、`COUIMaskEffectDrawable` 画悬停/焦点/按压蒙层、`COUIStrokeDrawable` 画焦点描边，`StateEffectAnimator` 用弹簧把进度插值到目标色，`COUIViewStateController` 再用 Processor 把状态映射到 padding、尺寸与字号。

移植侧把这套组合收成一个容器 `StateEffectSurface`：宿主给出形状、底色与点击回调，容器自己跟踪交互态并叠图层，三层蒙层与焦点描边复用已有的 `Modifier.couiStateMask` 与 `Modifier.couiFocusOutline`，按压缩放复用 `rememberCouiPressMotion`。

## 最小调用

~~~kotlin
StateEffectSurface(
    modifier = Modifier.fillMaxWidth(),
    onClick = { selected = true },
    selected = selected,
    containerColor = CouiTheme.colorScheme.card,
    focusOutline = true,
) {
    Row(Modifier.padding(16.dp)) { /* 内容 */ }
}
~~~

## 图层顺序

1. `containerColor` 底色，按 `shape` 裁剪。
2. 悬停蒙层。
3. 焦点蒙层；`selected = true` 时并入这一层（对应原厂 `setTouchSelected`）。
4. 按压蒙层，按住期间弹簧涨满后退场。
5. 焦点描边，仅在 `focusOutline = true` 时存在。
6. `content`。

`maskType = CouiMaskType.HoverPress` 去掉第 3 层，用于过滤焦点的材质表面。

## 参数

- `onClick`：提供时整块可点击并暴露 `Role.Button`；为 null 时只做绘制，点击语义由宿主提供。
- `enabled`：关闭时蒙层与描边一起归零，不保留按压尾巴，也不响应点击。
- `selected`：选中态，等效原厂把选中并入焦点通道。
- `shape`：默认 `CouiTheme.shapes.card`，与 `containerColor` 的裁剪一致。
- `maskType` / `focusOutline` / `scaleFeedback`：分别对应原厂的蒙层类型、是否叠加 `COUIStrokeDrawable`、是否 `enableScaleEffect`。
- `colors`：`CouiStateEffectColors(hover, focus, press, outline)`，默认取 `CouiMaskDefaults.colors(dark)` 的三层蒙层色与主题强调色描边。

## 焦点描边

`Modifier.couiFocusOutline` 单独可用：`strokeWidth` 默认 6dp（`default_focus_stroke_radius` 的两倍），`CouiStrokeType.Outer` 用差集裁掉形状外部，因此可见宽度是笔宽的一半；`Inner` 保留内侧。

## 与原厂的差异

1. `StateEffectAnimator` 的弹簧参数只有响应 0.3、回弹 0 一档，与 `Modifier.couiStateMask` 共用；原厂可对单条通道改 `setSpringResponse/setSpringBounce`，移植侧不开放。
2. `COUIViewStateController` 的 Processor 体系（`PaddingProcessor`/`MarginProcessor`/`SizeProcessor`/`TextSizeProcessor` 按状态直接替换数值）未移植：Compose 里宿主用 `animateDpAsState` / `animateFloatAsState` 或状态分支直接表达，比按状态重设布局参数更直接。
3. 原厂的 `setStateLocked`、`setNativeStateEnabled` 与 `DrawableStateManager` 的 `SparseIntArray` 位标志没有对应 API；移植侧用 `selected`、`maskType` 与中断交互源表达同类意图。
4. 原厂蒙层默认圆角是 `min(宽, 高) / 2`（`mRoundStyle`），移植侧轮廓来自 `shape`，需要胶囊时传 `CouiTheme.shapes.capsule`。
5. `COUIMaskRippleDrawable` 的波纹叠加未并入本容器，需要波纹时另加 `Modifier.couiPressRipple`。

## 与三态容器的区别

[三态容器](state-layout.md) 是「加载 / 空 / 失败」的页面级切换；本页的「状态效果」指控件自身在悬停、焦点、选中、按压下的绘制图层，两者没有语义重叠，可同时使用。

## Demo

Demo 的「State effect」页面（`DemoStateEffectSamples.kt`）并排三块卡片，可切换焦点描边、按压缩放与禁用态。

源码见 [StateEffectSurface](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/state/StateEffectSurface.kt) 与 [CouiStateEffectDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/state/CouiStateEffectDefaults.kt)。
