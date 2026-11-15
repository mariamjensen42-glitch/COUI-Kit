# 普通阴影

给卡片或胶囊添加普通投影时，从 Modifier.couiShadow 进入；它不依赖背景源，也不包含材质与 caustic 外光晕。

## 最小普通阴影

以下示例适用于 API 33 及以上的 Compose 内容（mesh 路径在 API 34+，API 33 走 Path + RenderNode + RenderEffect 后端）。宿主须先检查版本；父级须为阴影保留绘制空间，不能沿主体边界裁剪。

```kotlin
@RequiresApi(33)
@Composable
fun ShadowSample(modifier: Modifier = Modifier, dark: Boolean) {
    val radius = 24.dp
    Box(
        modifier.padding(32.dp).size(160.dp, 72.dp)
            .couiShadow(style = CouiShadowDefaults.menu(dark), radius = radius)
            .background(
                color = if (dark) Color.DarkGray else Color.White,
                shape = CouiShape(radius, cornerWeight = 2f),
            ),
    )
}
```

## 自定义绘制

CouiShadowStyle 的长度使用 dp。便捷入口完成本地坐标和像素换算，不改变布局尺寸；父级变换作用于主体和投影整体。radius 省略时使用短边的一半。

`CouiShadowStyle.blurRadius` 为可选显式模糊半径：不传时由 elevation / lightRadius 派生（派生值偏软偏大，是重实现的近似）；传入后可显式收紧，更接近原生投影的紧致观感。原厂 `ShadowUtils` 各级 `blur_r` 恒为 0，实际模糊由平台 `OplusView.setOverrideLightSourceGeometry` 原生投影处理；本库因 Compose 无等价 API，改用 mesh / AGSL 重实现，二者观感存在固有差异，无法做到像素级一致。

同一个 `couiShadow` Modifier 可用于多个节点，每个节点各持有 renderer 和缓存槽。尺寸变化更新本节点的网格，不重建 Shader。阴影画在内容后方；将主体的 clip 放在阴影内侧，外层 padding 用来给阴影留出空间。

CouiShadowOutline 提供公开轮廓选项；普通 RoundRect 不支持 inverse clipping。使用 SmoothCapsule 时，内容形状和投影几何需要按同一主体装配，示例见 [DemoShadowSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoShadowSamples.kt)。

需要窗口坐标、节点 alpha、轴向缩放或多主体槽位时，直接持有 CouiShadowRenderer，并调用其 Canvas 绘制接口；不要把已经缩放的网格再次整体缩放。完整参数见[公开声明](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiShadowRenderer.kt)。

## 平台与入口

普通阴影要求 API 33；mesh 路径在 API 34+，fast 胶囊裁剪的承载分支见[兼容要求](../guide/compatibility.md)。构造失败交给宿主处理，父级裁剪与离屏透明度边界仍会影响可见阴影。

[便捷入口与 Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiShadow.kt)、[低层 renderer / outline](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/effect/CouiShadowRenderer.kt)。原厂网格与移植方法供开发者阅读[复刻指南](../development/replication-guide.md#32-底栏与-fab先分主体再分效果)。
