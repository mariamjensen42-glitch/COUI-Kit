# Switch

## Overview
COUI 开关组件包。`Switch` 是 Settings 17 尺寸的公开受控 Composable，`checked` 由宿主持有；`CouiSwitchMotion` 同步受控值并编排切换弹簧与拖动，`CouiSwitchDrag` 负责双锚点拖动/吸附会话，`CouiSwitchLoadingMotion`/`CouiSwitchLoadingGlyph` 提供加载动画与像素图标，`CouiSwitchDefaults` 提供尺寸、颜色与手势阈值。

## Interface
- `Switch` — public @Composable：checked、onCheckedChange、onDragCheckedChange、loading、loadingDescription、enabled、colors、dark、hapticFeedbackEnabled、interactionSource `[Switch.kt:Switch]`
- `CouiSwitchColors` — public 不可变颜色集：选中/未选中轨道、滑块、三组禁用色与焦点轮廓色 `[Switch.kt:CouiSwitchColors]`
- `CouiSwitchDefaults` — public 固定参数：宽 44dp、高 24dp、滑块 18dp、内边距 3dp、默认胶囊 Shape 与 `colors(accent, dark)` 工厂；含 internal 手势阈值与弹簧常量 `[Switch.kt:CouiSwitchDefaults]`
- `CouiSwitchDrag` — internal 拖动/吸附会话：水平与垂直 CouiSpring，begin/dragTo/attach/cancel 与 isActive `[CouiSwitchDrag.kt:CouiSwitchDrag]`
- `CouiSwitchMotion` — internal 受控同步、RTL 端点换算、切换弹簧与拖动编排；暴露 translation/stretch/fraction/settled 与 onCommit `[CouiSwitchMotion.kt:CouiSwitchMotion]`
- `CouiSwitchLoadingGlyph` — internal 加载图标持有与绘制，每实例持有独立 mutate Drawable `[CouiSwitchLoadingGlyph.kt:CouiSwitchLoadingGlyph]`
- `CouiSwitchLoadingMotion` — internal 加载动画状态与两组 AnimatorSet，公开 thumbScale/scale/alpha/rotation 及 update/dispose `[CouiSwitchLoadingMotion.kt:CouiSwitchLoadingMotion]`

## Invariants
- begin 时 horizontal.snapTo(position)、vertical.snapTo(0f)、deltaSatisfied=false 并进入 Phase.Drag；状态机为 Idle/Drag/Attach 三态 `[CouiSwitchDrag.kt:begin]` `[CouiSwitchDrag.kt:Phase]`
- dragTo 仅在 Phase.Drag 生效，目标为 (start+x-downX).coerceIn(0f,travel)，只限制目标不截断弹簧当前位置 `[CouiSwitchDrag.kt:dragTo]`
- dragTo 两级启动阈值：distance>=.01f 且（horizontal.isRunning 或 distance>=1f）才 animateTo；停稳后重启初速度为 0，运行中续接速度为 null `[CouiSwitchDrag.kt:dragTo]`
- 水平或垂直位移任一项 >= transformDistancePx（4f）才置 deltaSatisfied；attach 等距时保留第一个锚点，仅 |v| 或 |vy|>=transformVelocityPx（1000f）且 deltaSatisfied 才按速度改选对侧锚点 `[CouiSwitchDrag.kt:dragTo]` `[CouiSwitchDrag.kt:attach]`
- 吸附完成必须同时满足 phase==Attach 且两路弹簧都不运行，之后才置 Idle 并回调 onAttached；cancel 立即置 Idle 并取消两路弹簧 `[CouiSwitchDrag.kt:finishAttachment]` `[CouiSwitchDrag.kt:Phase]`
- 加载图标资源不可缺失（checkNotNull）并 mutate，避免多开关共享 alpha/边界；update 在 loading 未变化时直接返回，停止加载会取消 startAnimation 并复位 thumbScale=1f `[CouiSwitchLoadingGlyph.kt:drawable]` `[CouiSwitchLoadingMotion.kt:update]`
- 端点规则 endpoint(checked)=if (checked != rtl) travel else 0f；logical 在 RTL 下为 1-value/travel `[CouiSwitchMotion.kt:endpoint]`
- update 仅在 checked != observed 时同步；completedAttachment == checked 时消费标记并 snap，不启动切换弹簧 `[CouiSwitchMotion.kt:update]`
- begin 在 toggle.isRunning 或 drag.isActive 时返回 false；reconcile 先消费 completedAttachment，仅在 toggle 与 drag 均空闲且 |translation-endpoint|>=.1f 时补启动 animateToggle `[CouiSwitchMotion.kt:begin]` `[CouiSwitchMotion.kt:reconcile]`
- 吸附提交阈值为 translation >= travel.toInt()/2 后再异或 rtl；finishAttachment 写入 completedAttachment、调用 onCommit 并 settled++ `[CouiSwitchMotion.kt:finishAttachment]`
- updatePosition 把 translation 截断为 Int 像素，fraction=coerceIn(0f,1f)；stretch 在切换中按 p=.34725848f 分段，拖动中固定 1f `[CouiSwitchMotion.kt:updatePosition]`
- travel=(widthPx-paddingPx*2-thumbPx).coerceAtLeast(1f)；interactive=enabled && !loading，非交互时清除 touchPressed `[Switch.kt:Switch]`
- 拖动仅在 onDragCheckedChange!=null 时安装；超过 touchSlop 才进入 dragging 并 consume，UP 且拖动过则 computeCurrentVelocity(1000,12000f) 后 attach；isCouiTouchCancelled/已消费/失去指针即退出 `[Switch.kt:dragInput]`
- 提交路径去重：onCommit 仅 target != currentChecked 时触发，先 performCouiHapticFeedback 再回调；settled/checked 变化后先 withFrameNanos 一帧再 reconcile `[Switch.kt:Switch]`
- loading 且 loadingDescription!=null 时设置 stateDescription 与 liveRegion=Polite；onCheckedChange==null 时不安装 toggleable 与 48dp 最小命中 `[Switch.kt:Switch]`
- 轨道颜色在绘制阶段按 fraction 以 ColorUtils.blendARGB 混色；轨道轮廓读 CouiTheme.shapes.capsule，滑块固定 CouiCapsuleShape，focus outline 在 clip 外 `[Switch.kt:Switch]`
- ThumbOutlineCache 仅在 Size 变化时重建 Path `[Switch.kt:ThumbOutlineCache]`

## Side Effects
- pointerInput 手势会话读取 MotionEvent.rawX/rawY 与 VelocityTracker，拖动中 consume 指针事件 `[Switch.kt:dragInput]`
- 无障碍语义：toggleable(Role.Switch)，加载时 stateDescription 与 liveRegion=Polite `[Switch.kt:Switch]`
- 触觉反馈：点击与拖动提交调用 view.performCouiHapticFeedback(CouiHapticFeedback.Switch) `[Switch.kt:Switch]`
- 绘制后端：drawBehind 混色轨道，Canvas/nativeCanvas 以 Android Paint 与 Path 绘制滑块阴影，loading 时叠加像素 Drawable `[Switch.kt:Switch]`
- 资源加载：ResourcesCompat.getDrawable(R.drawable.coui_switch_loading) 并 mutate，按 resources/configuration 变化重建 `[CouiSwitchLoadingGlyph.kt:drawable]`
- 动画时钟：CouiSpring 帧动画驱动 translation/fraction/stretch/settled `[CouiSwitchMotion.kt:animateToggle]`
- 动画时钟：ValueAnimator/AnimatorSet 驱动 thumbScale/scale/alpha/rotation；thumbScale/scale/alpha 与停止淡出使用 PathInterpolator(.3,0,.1,1)，rotation 使用 LinearInterpolator `[CouiSwitchLoadingMotion.kt:startAnimation]`
- 效果修饰符：couiSwitchFocusOutline 与 couiSwitchFeedback 参与焦点轮廓与反馈绘制 `[Switch.kt:Switch]`

## Verification
- Run: `./gradlew :coui:lintDebug`
