# SeekBar

## Overview
COUI 滑动条组件包。三个公开 Composable：`IconSeekBar`（图标加轨道）、`SectionSeekBar`（分段进度）与 `VerticalSeekBar`（竖向带标签）；各自在 DrawScope 中自绘轨道与进度；SectionSeekBar 另绘段标记与滑块，VerticalSeekBar 另绘滑块与文字标签。`Coui*SeekBarDefaults` 提供尺寸、动画常量与主题配色，交互统一使用 pointerInput、`progressBarRangeInfo` 语义与触感反馈。

## Interface
- `IconSeekBar` — public @Composable 图标滑动条：progress、onProgressChange、type、max、min、enabled、dark、colors、onProgressChangeFinished `[IconSeekBar.kt:IconSeekBar]`
- `SectionSeekBar` — public @Composable 分段滑动条：value、onValueChange、valueRange、enabled、dark、colors、onValueChangeFinished `[SectionSeekBar.kt:SectionSeekBar]`
- `VerticalSeekBar` — public @Composable 竖向滑动条：value、onValueChange、label、enabled、dark、colors、onValueChangeFinished `[VerticalSeekBar.kt:VerticalSeekBar]`
- `CouiIconSeekBarType` / `CouiIconSeekBarDefaults` / `CouiIconSeekBarColors` — public 类型枚举（Brightness/Volume）、尺寸/动画常量与配色数据类；含 `colors` 工厂 `[CouiIconSeekBarDefaults.kt:CouiIconSeekBarType]` `[CouiIconSeekBarDefaults.kt:CouiIconSeekBarDefaults]` `[CouiIconSeekBarDefaults.kt:CouiIconSeekBarColors]`
- `CouiSectionSeekBarDefaults` / `CouiSectionSeekBarColors` — public 尺寸/动画常量与配色数据类；含 `colors` 工厂 `[CouiSectionSeekBarDefaults.kt:CouiSectionSeekBarDefaults]` `[CouiSectionSeekBarDefaults.kt:CouiSectionSeekBarColors]`
- `CouiVerticalSeekBarDefaults` / `CouiVerticalSeekBarColors` — public 尺寸/动画/惯性常量与配色数据类；含 `colors` 工厂 `[CouiVerticalSeekBarDefaults.kt:CouiVerticalSeekBarDefaults]` `[CouiVerticalSeekBarDefaults.kt:CouiVerticalSeekBarColors]`

## Invariants
- IconSeekBar require(max>min)；visual 初值由外部 progress 换算，仅在 interacting=false 时由 LaunchedEffect 跟随 progress `[IconSeekBar.kt:IconSeekBar]`
- IconSeekBar 横向位移未超过系统 touchSlop 不进入拖动，抬手按点击处理；拖动阻尼为 1-ease(|x-center|/center) 夹到 [minDamping,1]，center 取进度区半宽并与面板绝对横坐标比较 `[IconSeekBar.kt:IconSeekBar]`
- IconSeekBar 拖动结束以 80ms tween 平滑回档（受控状态适配，原厂不做吸附）；未拖动则以 |target-visual|*483ms（下限 150ms、moveEase）过渡到点击档位 `[IconSeekBar.kt:IconSeekBar]`
- IconSeekBar 拖动中仅档位变化回调 onChange，点击过渡仅在 committed 与 target 不同时回调，onFinished 在动画结束后调用 `[IconSeekBar.kt:IconSeekBar]`
- IconSeekBar 的 setProgress 将目标 roundToInt 后 coerceIn(min,max)，档位未变时不回调但仍返回 true；禁用时语义 disabled 且不安装手势 `[IconSeekBar.kt:IconSeekBar]`
- fractionAt 在 usable<=0 时返回 0f，RTL 下返回 1f-raw；轨道起点为 iconCenterInset+iconSize/2+iconTrackGap、终点为 width-trackEndInset `[IconSeekBar.kt:fractionAt]` `[IconSeekBar.kt:drawIconSeekBar]`
- Brightness 图标旋转 progress*2 度；Volume 且 progress==0 时图标 alpha 为 0.5；图标路径内联于私有对象，库不依赖 coui-icons `[IconSeekBar.kt:drawIconSeekBar]` `[IconSeekBar.kt:CouiIconSeekBarPaths]`
- SectionSeekBar require 范围非空且 last-first>0；横向位移未超过系统 touchSlop 不进入拖动，越过后以按下位置确定拖动锚点 `[SectionSeekBar.kt:SectionSeekBar]`
- SectionSeekBar 拖动按 0.6 跟随 + 0.4 动画混合；跨档以 100ms dragSnapEasing 中间吸附，方向反转重新锚定；档位按方向取 round/ceil 后 coerceIn(first,last) `[SectionSeekBar.kt:SectionSeekBar]`
- SectionSeekBar 抬手 150ms、点击 200ms 均使用默认 AccelerateDecelerate；点击在动画前提交目标档位，抬手立即回调 onValueChangeFinished；仅档位变化回调 onValueChange `[SectionSeekBar.kt:SectionSeekBar]`
- SectionSeekBar 语义 steps=sections-1，setProgress 取整并 coerceIn(first,last)；段标记按 0..sections 等距，被滑块覆盖时跳过 `[SectionSeekBar.kt:SectionSeekBar]` `[SectionSeekBar.kt:drawSectionSeekBar]`
- VerticalSeekBar 先将 value coerceIn(0f,1f)，visual 仅在 interacting=false 时跟随；手势可用高度为控件高度减 paddingVertical*2，usable<=0 时消费事件；越过 touchSlop 首个拖动事件直接跳到手指位置，之后按 0.4 阻尼从锚点收敛且仅在档位变化时推进锚点，落在上下留白内的抬手不触发点击跳转 `[VerticalSeekBar.kt:VerticalSeekBar]`
- VerticalSeekBar 在拖动、点击过渡或惯性结束后调用 onValueChangeFinished；文字 Paint 由 remember 复用，不逐帧分配 `[VerticalSeekBar.kt:VerticalSeekBar]`
- VerticalSeekBar 触感按 (progress*20).roundToInt() 的 5% 档变化触发，同档内不重复震动 `[VerticalSeekBar.kt:VerticalSeekBar]`
- VerticalSeekBar 抬手未拖动走点击跳转，拖动且 |velocity|>=100 且 usable>0 才启动 fling；fling 每帧 speed/=1+15*seconds，fraction 减去 speed*seconds/usable 后夹 0..1 `[VerticalSeekBar.kt:VerticalSeekBar]` `[VerticalSeekBar.kt:fling]`
- VerticalSeekBarGeometry.progressOf 以底部为 0、顶部为 1；拖动每次移动都回调 onValueChange，点击动画逐帧回调，不做去重 `[VerticalSeekBar.kt:VerticalSeekBarGeometry.progressOf]` `[VerticalSeekBar.kt:VerticalSeekBar]`

## Side Effects
- pointerInput + awaitEachGesture/awaitFirstDown 读取并 consume 按下与移动事件，处理拖动、点击与惯性 `[IconSeekBar.kt:IconSeekBar]` `[SectionSeekBar.kt:SectionSeekBar]` `[VerticalSeekBar.kt:VerticalSeekBar]`
- semantics 暴露 disabled、progressBarRangeInfo 与 setProgress；VerticalSeekBar 另带 stateDescription 百分比文本 `[IconSeekBar.kt:IconSeekBar]` `[VerticalSeekBar.kt:VerticalSeekBar]`
- View.performHapticFeedback：IconSeekBar 在 API 34+ 端点用 SEGMENT_TICK、非端点用 SEGMENT_FREQUENT_TICK；SectionSeekBar 与 VerticalSeekBar 在 API 34+ 始终用 SEGMENT_FREQUENT_TICK；旧版本统一用 CLOCK_TICK `[IconSeekBar.kt:performIconHaptic]` `[SectionSeekBar.kt:performSectionHaptic]` `[VerticalSeekBar.kt:performSeekHaptic]`
- DrawScope 自绘：drawRoundRect/drawPath/rotate/translate/scale 与 Canvas drawCircle；VerticalSeekBar 经 nativeCanvas 与 android.graphics.Paint 绘制 12sp 标签 `[IconSeekBar.kt:drawIconSeekBar]` `[SectionSeekBar.kt:drawSectionSeekBar]` `[VerticalSeekBar.kt:drawVerticalSeekBar]`
- PathParser 解析内联的 24dp viewport 亮度/音量 SVG 路径为 Path `[IconSeekBar.kt:CouiIconSeekBarPaths]`
- VelocityTracker.obtain/computeCurrentVelocity/recycle 与 MotionEvent.obtain/recycle 管理速度采样 `[VerticalSeekBar.kt:VerticalSeekBar]` `[VerticalSeekBar.kt:addChange]`
- 读取 LocalDensity、LocalView 与 LocalLayoutDirection；默认配色读取 CouiTheme.isDark/colorSchemeFor `[IconSeekBar.kt:IconSeekBar]` `[CouiVerticalSeekBarDefaults.kt:CouiVerticalSeekBarDefaults.colors]`
- rememberCoroutineScope 异步推进吸附/点击动画，withFrameNanos 驱动惯性；LaunchedEffect 在非交互时同步 visual `[IconSeekBar.kt:IconSeekBar]` `[VerticalSeekBar.kt:fling]`

## Verification
- Run: `./gradlew :coui:lintDebug`
