# 🫧渐进式拖动

`ProgressiveDragState` 对应原厂 `animatekit/progressive` 的手势会话与弹簧编排：一次水平拖动里，
每一排内容按与触发排的行距拿到不同的弹簧响应，靠近手势的排跟得紧、远处的排跟得慢，
抬手后用带速度修正的回落弹簧收敛。

## 最小调用

~~~kotlin
val state = rememberProgressiveDragState()

Column(Modifier.fillMaxWidth().couiProgressiveDrag(state, triggerIndex = 0, span = rows - 1)) {
    repeat(rows) { row ->
        Row(Modifier.graphicsLayer { translationX = state.translation(row) }) { /* 行内容 */ }
    }
}
~~~

- `triggerIndex` 是被按中的排，`span` 是参与动画的最大行距，两者一起决定弹簧步长的封顶方式。
  行分组由宿主给出：原厂按屏幕中线切分行并自行找到触发排，这里不在组件内做命中测试。
- `state.translation(row)` 恒为「父位移 + 该排残差」；同排残差为 0，越远跟随越慢。
- 需要翻页或吸附时用 `releaseTo(targetOffsetX, velocityPxPerSecond)` 指定终点；
  `Modifier.couiProgressiveDrag` 的 `onRelease` 会拿到抬手速度，可直接据此决定目标。

## 阶段

| 阶段 | 含义 |
| --- | --- |
| `Idle` | 无会话，位移与残差都为 0 |
| `Drag` | 跟手拖动，残差向零收敛 |
| `Release` | 抬手回落，父位移与残差同时收敛 |
| `RubberbandDrag` | 越界后的阻尼拖拽 |
| `RubberbandRelease` | 越界后的回弹 |

`CouiProgressivePhase.isRelease` 覆盖两个回落阶段，`isRubberband` 覆盖两个越界阶段。
`state.progress` 把父位移换算到 `setProgressWindow` 给出的区间，可直接驱动联动效果。

## 弹簧参数

`CouiProgressiveDefaults.config` 的字段与默认值取自原厂 `HorizontalProgressiveConfig`：

| 字段 | 默认值 | 作用 |
| --- | --- | --- |
| `dragBaseResponse` | `0.15` | 触发行拖动响应 |
| `dragResponseStep` | `0.04` | 每增加一行加多少响应 |
| `maxDragResponse` | `5.85` | 拖动响应上限，超过时按 `span` 均分剩余额度 |
| `dragBounce` | `0.25` | 拖动回弹 |
| `releaseResponse` | `0.5` | 回落基础响应 |
| `releaseResponseStep` | `0.04` | 每增加一行加多少回落响应 |
| `releaseBounce` | `0.2` | 回落回弹 |
| `maxReleaseVelocityPxPerSecond` | `12000` | 速度归一化上限 |
| `releaseResponseVelocityDelta` | `0.1` | 速度对回落响应的最大缩减 |
| `releaseBounceVelocityDelta` | `0.25` | 速度对回落回弹的最大加成 |
| `dragBlendDuration` / `releaseBlendDuration` | `0.25` | 原厂换参数时的混合时长，秒 |

`dragSpring(distance, span)` 与 `releaseSpring(distance, span, velocity)` 直接给出该行距的
响应、回弹与混合时长；原厂的步长封顶是 `span * step + base > max` 时改用
`(max - base) / span`，这里保持一致。

## 橡皮筋

越界段的位移不用裸位移，而是按原厂 `calculateRubberband` 压成阻尼曲线：

~~~kotlin
val offset = CouiProgressiveDefaults.rubberbandOffset(
    overDragPx = overDrag,
    viewportPx = viewportWidth,
)
state.dragRubberband(offset, offsetX = 0f)
~~~

公式是 `(1 - 1 / (curveRatio * over / maxDistance + 1)) * maxDistance`，
`curveRatio` 默认 `0.55`；`maxDistancePx` 为非正数时取视口的一半。
松手后调 `releaseRubberband()`，残差回零、父位移不变。

## 与原厂的差异

- 原厂 `animatekit/progressive` 是 SystemUI 内部的 View + 协程框架，
  依赖私有 `OplusAnimator` 与 `springtransition` 的 `BoundsTransition`、
  `DrawableStrokeWidthProperty` 等属性动画器；这里换成 `CouiSpring`，
  同一套响应、回弹与行距步长直接映射到 Compose 的弹簧规格。
- 原厂为每个视图维护一条独立属性通道；这里同一行距共用一条弹簧，
  行距数量决定弹簧数量，`span` 之外的排不参与动画。
- 原厂自己按屏幕中线切分排并做命中测试，`triggerIndex` 由内部推算；
  这里由宿主传入，组件不参与布局测量。
- `blendMillis` 暴露在 `CouiProgressiveSpring` 上，`CouiSpring` 在换参数时直接跳到新规格，
  没有做原厂 250ms 的参数混合。
- 原厂 `addGroup` / `GroupView` 的多组联动与 `ProgressiveProgressDriver` 的回调注册没有一一对应，
  进度换算收敛为 `setProgressWindow` + `state.progress`。

## Demo 与源码

- Demo：`demo/src/main/java/com/cycling/coui/demo/DemoProgressiveDragSamples.kt`（目录项「Progressive drag」）。
- 源码：[ProgressiveDragState.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/motion/ProgressiveDragState.kt)、
  [CouiProgressiveDefaults.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/motion/CouiProgressiveDefaults.kt)。

## Demo

完整示例见 [DemoProgressiveDragSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoProgressiveDragSamples.kt)。
