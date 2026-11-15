# 运动与跟随回弹

需要在宿主自己的绘制中复用 COUI 动效时，使用运动工厂及输入 Modifier。它们观察触摸和更新运动值，业务点击与滚动仍由宿主处理。

## 双主体跟随

下面片段位于 Compose 内容中。revealPx 是宿主可观察的位移状态，使用像素；读取 translation 时再把结果用于当前几何或绘制变换。

~~~kotlin
val density = LocalDensity.current
val subjectSizePx = with(density) { CouiSwipeRevealDefaults.fusedActionSize.toPx() }
val followingDistancePx = with(density) { CouiSwipeRevealDefaults.menuFollowingDistance.roundToPx().toFloat() }
val translation by rememberCouiMetaballFollowingTranslation(
    distancePx = { revealPx },
    subjectSizePx = subjectSizePx,
    followingDistancePx = followingDistancePx,
)
~~~

距离递增时跟随，跨过主体尺寸与跟随距离之和后从当前位置回弹；距离归零时重置。两个尺寸须有限非负，合计为正；距离也须有限。reboundSpec 默认使用所属 Defaults，可显式提供动画规格。

这一入口不接管手势。RTL 与目标坐标由宿主映射，完整[融合绘制](metaball.md)应同时更新背景和遮罩。

## 触摸与按压

| 入口 | 用法 |
| --- | --- |
| rememberCouiTouchMotion / couiTouchMotion | 工厂随节点释放，Modifier 映射触点，宿主读取位移与缩放 |
| CouiTouchMotion | 需要显式 CoroutineScope 生命周期时使用；保持释放与取消路径 |
| rememberCouiPressMotion / couiPressMotion | 工厂随节点释放，Modifier 观察普通按压，宿主读取缩放 |
| CouiPressMotion | 显式作用域构造；与形变缩放的增量相加，不替换形变状态 |

运动对象在主线程创建、调用和释放。显式构造时使用主线程 CoroutineScope；取消该 scope 后弹簧停止，实例不再接收输入。Compose 工厂自动处理节点移除，不需要另建作用域。触点、位移及 IntSize 使用像素，缩放为倍率。

Touch 的四轴和 Press.scale 都是只读 State<Float>。在绘制阶段读取 .value；同一触摸组合中的形变与按压使用 `touchScale + pressScale - 1f`，不要相乘。独立外层的展开/收起缩放按父子变换相乘；自定义动画由宿主单独持有。

下面片段位于 Compose 内容中；Box 内放宿主的按钮内容。点击仍由宿主自己的 clickable 处理：

~~~kotlin
val motion = rememberCouiTouchMotion(expanded = true)
val press = rememberCouiPressMotion()
Box(
    Modifier.graphicsLayer {
        translationX = motion.translationX.value
        translationY = motion.translationY.value
        scaleX = motion.scaleX.value + press.scale.value - 1f
        scaleY = motion.scaleY.value + press.scale.value - 1f
    }.couiTouchMotion(motion, expanded = true).couiPressMotion(press),
) {
    // 宿主按钮内容。
}
~~~

### 主动控制运动

手动驱动 `CouiTouchMotion` 时，使用 `begin` 开始触摸、`move` 更新位置、`release` 以输入速度回弹。`cancel` 停在当前位置，保留速度并清除尚未处理的目标。再次开始触摸时，由新的输入确定目标。需要立即恢复未变形状态时调用 `reset`，它会清除旧动画、速度与触摸会话，并保留坐标绑定。`CouiPressMotion` 使用 `press` 和 `release` 控制按下与释放。

按压输入的 cancelOnConsumedMove 用于父容器消费移动后立即释放，并在本次触摸结束前保持取消。选择、光效、按压与滚动各自有状态 owner，不能用某一路已取消推断其他路径都已收尾。

跟随预设使用 `CouiSwipeRevealDefaults`，宿主无需复制固定参数。

### 按压参数

`rememberCouiPressMotion(params)` 接受 `CouiPressFeedbackParams`：按下与抬手的 response、bounce，以及按压形变的最小可见变化量，对应原厂 `IPressParams` / `PressParams` 的五个字段。目标缩放仍由触控面积与宽高比决定，参数只影响弹簧路径。

| 预置 | 按下 | 抬手 | 来源 |
| --- | --- | --- | --- |
| `CouiPressFeedbackDefaults.oliveLink`（默认） | response 0.25 / bounce 0 | response 0.5 / bounce 0.6 | `touchanimation.param.olivelink.PressParams` 的 jelly 弹簧对 |
| `CouiPressFeedbackDefaults.pressParams` | response 0.15 / bounce 0 | response 0.4 / bounce 0.5 | `pressfeedback.PressParams` |

两组都取 `minimumVisibleChange = 0.001`。原厂 olivelink 的 candy 分支另有 response 0.3 的按下弹簧与 0.0001 的抬手最小变化，移植将其并入同一组参数，因此宽高比很大的按压会比原厂稍慢一档。

### 水波纹

`Modifier.couiPressRipple` 对应 `COUIPressRippleDrawable` 的波纹分支：按下时从触点扩散圆形波纹，抬手或取消时淡出，绘制范围按 `shape` 裁剪（原厂把 `COUIPressMaskDrawable` 当遮罩）。

~~~kotlin
Box(
    Modifier
        .clip(CouiTheme.shapes.capsule)
        .couiPressRipple(interactionSource, padded = true)
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
)
~~~

颜色取 `coui_color_ripple_press_background`（浅色 #99B4B4B4、深色 #80FFFFFF），可用 `color` 覆盖；`padded = true` 对应原厂 `initPadding` 的 12dp / 4dp 外扩，绘制会超出节点边界，需要父容器允许越界。

原厂直接使用平台 `RippleDrawable`，毫秒值由系统常量决定；移植按平台节奏取 20ms 进入延迟、75ms 淡入、150ms 淡出、200–400ms 随距离线性扩散，属于近似而非 1:1。水波纹与 `couiStateMask`（普通表面）、`couiButtonFeedback`（按钮与列表）的蒙层是两条分支，同时使用会叠出两份按压反馈。

## 入口与示例

[跟随](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/motion/CouiMetaballFollowing.kt)、[输入工厂和 Modifier](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/motion/CouiTouchInput.kt)、[TouchMotion](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/motion/CouiTouchMotion.kt)、[PressMotion](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/motion/CouiPressMotion.kt)、[水波纹](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/effect/CouiPressRipple.kt)、[预设](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/swipereveal/CouiSwipeRevealDefaults.kt)。跟随装配见 [DemoRecordSwipeRow](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoRecordSwipeRow.kt)。
