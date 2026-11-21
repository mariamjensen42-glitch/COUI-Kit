# SegmentedControl

## Overview
COUI 胶囊分段选择器组件包。`SegmentedControl` 是 @RequiresApi(33) 的受控 Composable；`CouiSegmentMotion` 提供指示器弹簧状态机与 `SegmentSelectionSession` 提交语义，`SegmentGeometry`/`segmentWidths` 负责测量与命中，`CouiSegmentedControlDefaults` 提供尺寸、颜色、材质与文字样式；backdrop 非空时走透明材质与光效路径。

## Interface
- `SegmentedControl` — public @Composable 受控胶囊分段选择器：labels、selectedIndex、onSelected、backdrop、dark、enabled、small、commitOnRelease、textStyle、containerColor、indicatorColor、renderer `[SegmentedControl.kt:SegmentedControl]`
- `CouiSegmentedControlDefaults` — public 默认对象；public 成员 height=40dp、smallHeight=32dp、containerColor、indicatorColor、material、textStyle `[CouiSegmentedControlDefaults.kt:CouiSegmentedControlDefaults]`
- `CouiSegmentedControlDefaults` internal 成员 — padding=4dp、smallPadding=2dp、minWidth=52dp、labelPadding=12dp、switchDistance=5dp、spotlightNoise、spotlightDither `[CouiSegmentedControlDefaults.kt:CouiSegmentedControlDefaults.padding]` `[CouiSegmentedControlDefaults.kt:CouiSegmentedControlDefaults.switchDistance]`
- `segmentWidths` — internal 按自然宽度做水位分配并输出整数像素宽度 `[CouiSegmentMotion.kt:segmentWidths]`
- `SegmentGeometry` — internal 宽高、padding、各段宽度、RTL/小尺寸标记，含 indicatorHeight/center/hit `[CouiSegmentMotion.kt:SegmentGeometry]`
- `SegmentSelectionSession` — internal 选择会话：allowLight/touching/preview，sync/begin/select/finish 提交语义 `[CouiSegmentMotion.kt:SegmentSelectionSession]`
- `CouiSegmentMotion` — internal 指示器弹簧状态机：select/rect/beginDrag/dragRubber/release/dispose `[CouiSegmentMotion.kt:CouiSegmentMotion]`

## Invariants
- require labels 非空且 selectedIndex 在 labels.indices 内；backdrop 非空时必须提供 renderer `[SegmentedControl.kt:SegmentedControl]`
- 默认拖过新项即提交：select 在 !onRelease 且 index != emitted 时更新 emitted 并 submit；commitOnRelease 只在 finish 的有效 UP 提交 `[CouiSegmentMotion.kt:SegmentSelectionSession.select]`
- finish 仅在 touching 时生效；取消时 preview 回到 selected 且不提交；未取消、onRelease 且 preview != selected 才提交 `[CouiSegmentMotion.kt:SegmentSelectionSession.finish]`
- 外部 selectedIndex 同步只在非触摸期进行：sync 在 touching 时忽略 `[CouiSegmentMotion.kt:SegmentSelectionSession.sync]`
- 手势只处理单指；changes.size != 1、isCouiTouchCancelled、坐标脱离或变更已消费均中断；未取得 ownership 前纵向位移大于 touchSlop 且大于横向时让给父级 `[SegmentedControl.kt:SegmentedControl]`
- ownsDrag = dragging && pressed && abs(delta.x)>touchSlop；取得 ownership 后在 Initial 阶段 consume，已开始滑选后纵向偏移不再交回页面 `[SegmentedControl.kt:SegmentedControl]`
- 仅当按下点命中当前选中项或 inflate(10f) 的指示器矩形才进入拖动；拖动时项目命中两侧各扣 switchDistance `[SegmentedControl.kt:SegmentedControl]`
- finally 中未完成时 allowLight=false 且 touch.release(Offset.Zero)；随后 session.finish(!completed)、motion.release 与各 press 逐个释放 `[SegmentedControl.kt:SegmentedControl]`
- segmentWidths 先按自然宽度升序分配低于均值的项目，再重设上限并把余量均摊给除最长项外的项目；末项 end 强制等于 available `[CouiSegmentMotion.kt:segmentWidths]`
- SegmentGeometry.starts 由 runningFold(padding) 逐项累加宽度；indicatorHeight=height-padding*2 且不小于 0；center/hit 在 RTL 下镜像 `[CouiSegmentMotion.kt:SegmentGeometry]`
- hit 返回首个 logical>=start+inset 且 < nextStart-inset 的下标，未命中返回 null `[CouiSegmentMotion.kt:SegmentGeometry.hit]`
- geometry 变化时 select 直接 snap position/width/height/rubber；几何不变才走弹簧动画；指示器变宽时 widthLimit=target+(small?5f:7f)*density `[CouiSegmentMotion.kt:CouiSegmentMotion.select]`
- rect 半宽=indicatorWidth*scale/2，左右夹在 padding 与 width-padding，高度下限 indicatorHeight*0.9 `[CouiSegmentMotion.kt:CouiSegmentMotion.rect]`
- 默认尺寸：height 40dp、smallHeight 32dp、padding 4dp、smallPadding 2dp、minWidth 52dp、labelPadding 12dp、switchDistance 5dp `[CouiSegmentedControlDefaults.kt:CouiSegmentedControlDefaults]`
- 自然宽度=max(minWidth, 单行测量宽度+labelPadding*2)，字体异步解析完成时一起重算；padding 夹到不超过 actualWidth/2 `[SegmentedControl.kt:SegmentedControl]`
- 每个标签语义 mergeDescendants、Role.Tab、selected；Enter/Space/DirectionCenter 的 KeyUp 同样提交 `[SegmentedControl.kt:SegmentedControl]`
- 禁用时 LaunchedEffect(enabled) 以 finish(true) 取消会话并释放 indicatorPress、touch、press、所有 labelPresses 与 motion `[SegmentedControl.kt:SegmentedControl]`
- 非 backdrop 路径用 indicatorShape 裁剪并先画 containerColor；backdrop 路径改用胶囊裁剪与 contentLayer 并在材质上混色 `[SegmentedControl.kt:SegmentedControl]`
- 指示器形状固定为 CouiShape(999.dp,2f)，不随主题替换 `[SegmentedControl.kt:SegmentedControl]`
- drawSegmentIndicator 在 rect 宽或高<=0 时直接返回；阴影高度=containerHeight-rect.top-2dp 且不小于 0 `[SegmentedControl.kt:drawSegmentIndicator]`
- SegmentIndicatorDrawing 仅在 Size 变化时重建 Path 与径向渐变；禁用标签文字 alpha 乘 0.38 `[SegmentedControl.kt:SegmentIndicatorDrawing]` `[SegmentedControl.kt:SegmentedControl]`

## Side Effects
- pointerInput 在 Initial 阶段观察新按下以允许 spotlight，并在 Initial 阶段 consume 变更与跟踪速度 `[SegmentedControl.kt:SegmentedControl]`
- selectableGroup、Role.Tab/selected/disabled/onClick 语义、focusable 与 onKeyEvent 键盘操作 `[SegmentedControl.kt:SegmentedControl]`
- HapticFeedbackType.TextHandleMove 触感反馈 `[SegmentedControl.kt:SegmentedControl]`
- CouiBackdrop 采样、CouiFloatingRenderer 透明模糊、CouiFloatingSurface、CouiContour 与 rememberCouiSpotlight 光效 `[SegmentedControl.kt:SegmentedControl]`
- rememberGraphicsLayer 录制内容层、drawWithCache/drawWithContent、nativeCanvas Paint 与 clipPath 绘制 `[SegmentedControl.kt:SegmentedControl]`
- onGloballyPositioned/onSizeChanged 记录坐标与尺寸；CouiSpring 动画依赖动画时钟 `[SegmentedControl.kt:SegmentedControl]` `[CouiSegmentMotion.kt:CouiSegmentMotion]`
- LocalConfiguration/LocalDensity 读取、Typeface.create 创建字体、CouiMaterialDefaults.of 取材质预设 `[CouiSegmentedControlDefaults.kt:CouiSegmentedControlDefaults.textStyle]`
- session 与 motion 的提交通过 submit.value 回调宿主 onSelected `[SegmentedControl.kt:SegmentedControl]`

## Verification
- Run: `./gradlew :coui:lintDebug`
