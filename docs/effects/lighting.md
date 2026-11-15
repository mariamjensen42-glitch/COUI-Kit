# 轮廓、跟手光与状态反馈

已有 COUI 组件会装配对应光效。单独绘制自定义表面时，可使用低层轮廓、背景感知描边与触摸观察接口。

## 不透明表面的跟手光

下面函数在 API 33 及以上使用。宿主传入不透明底色、Shape、深浅色、点击回调和内容；先画底色，再画光，最后画内容。

~~~kotlin
@RequiresApi(33)
@Composable
fun LitSurface(
    modifier: Modifier = Modifier,
    color: Color,
    shape: Shape,
    dark: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val interactions = remember { MutableInteractionSource() }
    val (lightInput, drawLight) = rememberCouiSpotlight(
        interactive = true,
        preset = SpotlightPreset.OpaqueMedium1,
        dark = dark,
    )
    Box(
        modifier.background(color, shape).clip(shape).then(lightInput)
            .drawBehind { drawLight?.invoke(this) }
            .clickable(
                interactionSource = interactions,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    ) { content() }
}
~~~

这是自定义不透明表面的组合。半透明浮动组件应沿用其[浮动表面](floating-surfaces.md)的合成位置；将 Overlay 先混入空的透明图层再整体 SrcOver，会改变结果。

## 光效入口

| 入口 | 输入与行为 |
| --- | --- |
| CouiContour | 真实表面颜色、尺寸、density、像素半径与角权重；输出已包含底色，不再叠一次 |
| CouiEdge / CouiEdgeStyle | 原始模糊图、材质两次混色、几何与轮廓开关；取得与尺寸匹配的 RenderEffect |
| rememberCouiSpotlight | 返回触摸 Modifier 与可空绘制回调，不接管业务点击或滚动 |
| SpotlightPreset | 跟手光预设；半径和光源高度使用设备像素，不按 dp 再次换算 |
| couiStateMask | 跟随 Shape 的悬停、焦点（含选中）与按压蒙层；类型、颜色和按压驱动可覆盖 |
| couiFocusOutline | 沿 Shape 的焦点描边，Inner 保留内侧、Outer 保留外侧 |
| CouiMaskType / CouiStrokeType / CouiMaskColors / CouiMaskDefaults | 蒙层类型、描边类型、三层目标色与原厂固定参数 |
| couiNavigationFeedback / couiFabFeedback / couiToolbarFeedback / couiButtonFeedback | 配合同一 InteractionSource 的组件状态反馈 |

跟手光的可选覆盖项为 null 时继承预设。inputTarget 负责外部触点映射与会话，返回 null 取消；cancelOnConsumedMove 用于父容器接管后取消本次光效，不能在同次触摸中重新点亮。

轮廓总开关与高级样式同步方式见[外观与预设](../guide/appearance.md)。CANCEL、点击、按压和绘制分别处理，不能把所有状态合并成一个 running 开关。

## 状态蒙层与焦点描边

`couiStateMask` 对应原厂 COUIMaskEffectDrawable 的三条通道：悬停、焦点与按压各自持有 0..10000 的弹簧进度，同一帧按该顺序叠加，等价于原厂 `getCompositeMaskColor` 的 press over (focus over hover)。按压从透明插值到目标色，短按在未达 0.7 时先补满再退场。

`CouiMaskType.HoverPress` 关掉焦点通道，对应原厂 mMaskType 0（导航项与材质表面过滤 pressed 时使用）；`CouiMaskType.Full` 打开焦点与选中。`selected` 复用焦点通道，等同原厂 mEnableSelectedState。几何由 Shape 决定：矩形直接填充，圆角与自定义路径按轮廓 Path 绘制，等价于原厂 maskRect / maskPath 分支。

`couiFocusOutline` 对应 COUIStrokeDrawable：默认 6dp 笔宽（原厂 3dp 半径按像素取整后乘二）、Outer 差集裁剪，因此可见宽度为 3dp，颜色取 couiColorFocusOutline。Inner 用于内容需要保留在描边外侧的表面。

```kotlin
val interactions = remember { MutableInteractionSource() }
Box(
    Modifier
        .clip(shape)
        .couiStateMask(interactions, dark = dark, shape = shape, selected = selected)
        .couiFocusOutline(interactions, shape, CouiMaskDefaults.colors(dark).focus)
        .clickable(interactions, indication = null, onClick = onClick),
) { content() }
```

按压由列表最低进度等外部信号驱动时，传 `press = false` 与 `pressProgress`，不要把同一态再交给 `pressed` 收集，否则两层进度会叠两次。禁用时三层进度直接归零，不回弹，对应原厂 disabled 分支的 reset。

## 资源与入口

轮廓和背景感知效果要求 API 33，旧平台的跟手光工厂没有等价 Shader 回调。尺寸或颜色改变时更新对应 Effect，不复用旧 uniform 快照；详见[平台要求](../guide/compatibility.md)。

[轮廓](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiContour.kt)、[背景感知描边](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiEdge.kt)、[跟手光 / 预设 / 诊断](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiSpotlight.kt)、[状态蒙层](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiStateMask.kt)、[状态反馈](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiStateFeedback.kt)。组合绘制实例见[顶栏按钮](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/topappbar/TopAppBarButton.kt)。

## 独立 Drawable 轮廓

CouiContour 的 style 默认为 Menu。floating = true 选择 FAB 高光分支，不能与其他 style 组合。Dialog 和 Button 分别提供面板与控件的轮廓预设。

shader 的 size、radiusPx、lineWidthPx 使用节点本地像素，density 为正数。Menu / floating 的 cornerWeight 至少为 2；Dialog / Button 允许胶囊权重 1。所有透明度为 0..1。独立覆盖光效传入 Color.Transparent，然后覆盖到实际表面；传入表面颜色时输出已经包含该底色，不能重复叠加。

每个独立节点持有自己的 CouiContour；尺寸、几何或强度变化时更新 shader 参数。外部裁剪 Shape 应与距离场匹配。独立轮廓使用白光 SrcOver；需要后景颜色参与时使用 CouiEdge。

[弹窗](../components/dialog.md)包含面板与操作按钮的轮廓效果，无需单独配置。
