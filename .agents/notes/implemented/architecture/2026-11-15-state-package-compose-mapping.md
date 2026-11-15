# Agent Note: 原厂 state 包到 Compose 状态蒙层映射

Status: implemented

## Problem

`com.coui.appcompat.state` 的 20 个类已逐个读完，需要决定哪些语义进入这套 Compose 库、以什么形状进入。

读之前的状态蒙层散在 [CouiStateFeedback](../../../../coui/src/main/java/com/itos/coui/effect/CouiStateFeedback.kt) 的各个分支里：按钮、导航、FAB、工具栏各自 `rememberMaskProgress` 加自绘圆角矩形，开关的键盘焦点描边又在同一文件里按 `TYPE_OUTER` 手工把双倍笔宽画在形状边界再裁掉内侧。原厂并不这样分层：`COUIMaskEffectDrawable` 用三条 `StateEffectAnimator`（hover / focus / press，弹簧 response 0.3、bounce 0）各自持有 0..10000 进度，绘制时按 `compositeColors(press, compositeColors(focus, hover))` 叠加；`COUIStrokeDrawable` 用 `default_focus_stroke_radius` 的两倍笔宽做 Inner/Outer 两种裁剪；`COUIMaskRippleDrawable` 在涟漪之下先画 hover 再画 focus。

原厂这一套挂在 Drawable/View 宿主上：`StatefulDrawable`/`DrawableStateProxy` 维护状态位和 state lock，`StateEffectAnimator` 依赖宿主 `invalidateSelf`，`Processor` 族按 state 改 padding/margin/size/textSize，`COUIStateEffectDrawable` 再把 refresh/reset 广播给每一层。Compose 里没有对应宿主，照搬会得到一套无人持有、无法失效重绘的框架。

## Decision

新增共享入口 [CouiStateMask](../../../../coui/src/main/java/com/itos/coui/effect/CouiStateMask.kt)，只承载可观察的绘制语义：

- `Modifier.couiStateMask(interactionSource, dark, enabled, type, shape, selected, hoverLocked, press, pressProgress, colors)`：三条独立弹簧通道，绘制顺序 hover → focus → press，与 `getCompositeMaskColor` 的合成顺序一致。`selected` 并入 focus 通道，对应原厂 `mEnableSelectedState` 复用 `mFocusAnimator`；`CouiMaskType.HoverPress` 关掉 focus 与 selected，对应 `mMaskType == 0`（导航项与材质表面过滤 pressed 的场景）。
- `Modifier.couiFocusOutline(interactionSource, shape, color, enabled, strokeWidth, type)`：`CouiStrokeType.Inner` 用 `clipPath(path)` 保留内侧，`Outer` 用 `clipPath(path, ClipOp.Difference)` 保留外侧。笔宽取 6dp，即原厂 `default_focus_stroke_radius` 3dp 的两倍，Outer 裁剪后可见 3dp；Inner/Outer 因此共享同一笔宽语义，内外只由裁剪决定。
- `CouiMaskDefaults`：`MinimumPressProgress = 0.7f`（原厂 `mMinProgressForTouchEnterAnimation`）、`FocusStrokeWidth = 6.dp`、`colors(dark)`（深色 0x26FFFFFF / 0x331A8CFF / 0x33FFFFFF，浅色 0x14000000 / 0x1F0080FF / 0x1F000000）。
- 禁用态在 `SideEffect` 里把三条通道快照归零，不跑弹簧：原厂 disabled 分支就是 `animateToProgress(0, false)`。

[CouiStateFeedback](../../../../coui/src/main/java/com/itos/coui/effect/CouiStateFeedback.kt) 相应收窄：`couiButtonFeedback` 保留列表最低进度（`CouiListPressMask`）、指针会话与窗口失焦监听，绘制改为 `couiStateMask(...).then(observer)`；`couiSwitchFocusOutline` 退化为一行委托；私有 `rememberMaskProgress` 迁入新文件供两处共用。25 个调用点的签名不变。导航、FAB、工具栏、开关轨道的既有自绘未迁移：它们的 -8dp 内缩与混色是各组件自己的特化，没有同源原厂证据。

不移植的部分按平台适配处理，不当作遗漏：`Processor` 族（Padding/Margin/Size/TextSize）与 `COUIViewStateController` 是布局职责，Compose 由调用者自己的 Modifier 表达；`COUIStateEffectDrawable` 的层广播没有 Drawable 图层宿主；`COUIMaskRippleDrawable` 的图标/复选框半径与自定义路径裁剪涟漪仍由 Compose ripple 承担；`DrawableStateManager` 的状态位与 state lock 由 `InteractionSource` 的 hover/focus/press 覆盖，锁位没有宿主需求；`touchType`（按压 vs 选中）未建模。

## state 包 20 个类的处置

| 原厂类 | 处置 | 落点与理由 |
| --- | --- | --- |
| `COUIMaskEffectDrawable` | 移植绘制语义 | `couiStateMask` 三条 `rememberMaskProgress`；顺序叠加、`mMaskType` 0/1、selected 复用 focus、disabled 归零、`mMinProgressForTouchEnterAnimation` |
| `COUIMaskEffectDrawable$MaskEffectType` | 等价替换 | R8 后为空成员枚举，语义取自字段 `mMaskType` → `CouiMaskType` |
| `COUIMaskRippleDrawable` | 部分移植 | hover/focus 蒙层由 `couiStateMask(type = Full)` 承担；图标/复选框半径与自定义路径裁剪涟漪仍用 Compose ripple |
| `COUIStrokeDrawable` | 移植 | `couiFocusOutline`；两倍笔宽、Inner/Outer 裁剪、可见半笔宽 |
| `COUIStateEffectDrawable` | 不移植 | 需要 Drawable 图层宿主做 per-layer 广播；按压缩放已由 `CouiPressMotion` 承担 |
| `StatefulDrawable` | 不移植 | Drawable 宿主抽象，Compose 无对应基类 |
| `RippleStatefulDrawable` | 不移植 | 同上，涟漪本体交给 Compose ripple |
| `DrawableStateProxy` | 等价替换 | 状态查询与设置由 `InteractionSource` 的 hover/focus/press 集合承担 |
| `DrawableStateManager` | 不移植 | 状态位与 `setStateLocked` 没有 Compose 宿主需求；锁位只在原厂绘制回调用到 |
| `IStateEffect` | 不移植 | refresh/reset/enable 三方法由 `CouiStateProgress` 的 active/reset 表达 |
| `StateEffectAnimator` | 移植核心 | 0..10000 进度轴 + ARGB 插值进 `rememberMaskProgress`；弹簧由公共 `CouiSpring` 承载，短按阈值语义留在 `CouiListPressMask` |
| `StateEffectAnimator$StateEffectAnimatorListener` | 等价替换 | 逐帧回调改为 `State<Float>`，绘制阶段读取 |
| `StatefulDrawableListener` | 等价替换 | `onDrawableUpdate` 由重组失效取代 |
| `COUIViewStateController` | 不移植 | 按 state 分发 Processor 的容器，Compose 侧由组件自己的 `if/when` 组合表达 |
| `IViewStateController` | 不移植 | 同上 |
| `Processor` | 不移植 | 抽象处理器，签名要求直接持有 View |
| `PaddingProcessor` | 不移植 | `Modifier.padding` |
| `MarginProcessor` | 不移植 | `Modifier.padding` 与父容器 `Arrangement`；`MarginLayoutParams` 无 Compose 对应 |
| `SizeProcessor` | 不移植 | `Modifier.size`/`weight` |
| `TextSizeProcessor` | 不移植 | `TextStyle` 与 `AnnotatedString` 段落样式 |

## Existing-note audit

- [开关反馈](../bug-fix/2026-10-28-switch-review-feedback.md)：部分重叠，互链。该篇的触摸 pressed 会话、焦点独立配色、外侧裁剪理由保持，本篇只把那处裁剪抽为共享入口。
- [开关组件](../feature/2026-10-27-coui-switch.md)：沿用 `couiSwitchFeedback` 的轨道混色与加载，未改物理。
- [普通弹簧持久通道](2026-10-29-persistent-motion-channels.md)：沿用 `CouiSpring` 承载三通道进度，取消/重定向/结束精度语义不变。
- [Modifier 职责](2026-11-02-modifier-contracts-and-picker-layout.md)：沿用「状态反馈的动画由调用者提供的输入状态驱动，不复制成额外状态机」，新入口同样只消费 `InteractionSource`。
- [公开边界](2026-10-23-public-api-boundary.md)：新入口在库内有 25 个消费者且需宿主复用，按绘制入口公开，不公开通道对象本身。
- [三态容器与状态分发](../feature/2026-11-13-pull-refresh-accordion-avatar-state-stepper.md)：无关，那篇持有整页空态几何。
- 没有同范围的 proposed/rejected 需要取代或删除。

## Alternatives considered

- **保留各组件自绘。** 改动最小，但三处圆角蒙层与一处描边继续各写一份顺序、颜色与笔宽，宿主也无法复用；否决。
- **一比一移植 Drawable/StatefulDrawable/Proxy/Processor。** 形式上最像原厂，但需要一个 Drawable 宿主与 View 状态位，Compose 内只能自建 framework 模拟，且 Processor 的 padding/margin/size 在这里是布局职责；否决。
- **用一条合成色代替三条独立进度。** 静态结果等价，但丢掉逐通道弹簧与「按压未达最小进度」的语义，而原厂就是三条各自弹；否决。
- **一次性把导航、FAB、工具栏也迁到新入口。** 几何内缩与 -8dp 没有同源原厂证据，改动面超出本次对齐范围；改为先迁有原厂对应物的按钮与开关两处。
- **禁用态弹簧回落到 0。** 观感更连续，但原厂 disabled 分支显式传 `false` 停止动画；按原厂走快照归零。

## Consequences

- 新增公开入口 3 个（两个 Modifier、`CouiMaskDefaults`）与类型 3 个（`CouiMaskType`、`CouiStrokeType`、`CouiMaskColors`）；[CouiStateFeedback](../../../../coui/src/main/java/com/itos/coui/effect/CouiStateFeedback.kt) 从 222 行降到 193 行，25 个调用点签名不变。
- 使用说明写入 [光效](../../../../docs/effects/lighting.md)的入口表与「状态蒙层与焦点描边」节。
- 已知差异：Compose ripple 与 `COUIMaskRippleDrawable` 的半径/自定义路径裁剪不同；`touchType` 未建模；`Processor` 族与层广播缺失，需要时由组件自己的布局承担。
- 未验证：本地未执行 Gradle 编译（仓库禁止本地构建）；三通道在 hover/press 快速交替下是否互相拖慢、Outer 差集裁剪在非凸 Shape 上的描边、大 Shape 上 6dp 笔宽的观感都还需要设备验收。
