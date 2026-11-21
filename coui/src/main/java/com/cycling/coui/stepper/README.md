# Stepper

## Overview
数值步进与步骤条组件包。`Stepper` 复刻 OEM `COUIStepperView` 的加减控件（长按连发、触觉反馈、焦点轮廓），`StepBar` 提供横向步骤进度，`StepRing` 提供 24dp 环形步进徽标；三者状态均由宿主持有。

## Interface
- `public object CouiStepBarDefaults`：节点尺寸、连线宽度与标签间距 [StepBar.kt:CouiStepBarDefaults]
- `public @Composable fun StepBar(modifier, steps, currentIndex, dark, colors)` [StepBar.kt:StepBar]
- `public object CouiStepRingDefaults`：24dp 尺寸、1.63dp 描边、18dp 设计字号与取色工厂 [CouiStepRingDefaults.kt:CouiStepRingDefaults]
- `public @Immutable data class CouiStepRingColors(track, progress, text)` [CouiStepRingDefaults.kt:CouiStepRingColors]
- `public @Composable fun StepRing(modifier, step, total, dark, size, strokeWidth, colors)` [StepRing.kt:StepRing]
- `public object CouiStepperDefaults`：取值范围 -999..9999、按钮/指示器尺寸、`repeatIntervalMillis = 100L`、轮廓与配色工厂 [Stepper.kt:CouiStepperDefaults]
- `public @Immutable data class CouiStepperColors(container, content, disabled, focusOutline)` [Stepper.kt:CouiStepperColors]
- `public @Composable fun Stepper(modifier, value, onValueChange, valueRange, step, enabled, dark, colors, plusIcon, minusIcon, decreaseDescription, increaseDescription)` [Stepper.kt:Stepper]

## Invariants
- `StepBar` 要求 `steps.isNotEmpty()`；`currentIndex` 只被局部收敛到 `0..steps.lastIndex`，不回写宿主；单步列表不画连接线 [StepBar.kt:StepBar]
- `StepRing` 把 `total` 收敛到至少 1、`step` 夹在 `1..total`；弧长 `360 × step / total` 自 12 点顺时针，`step >= total` 时改画整圈实心椭圆描边 [StepRing.kt:StepRing]
- `StepRing` 数字字号 = `designTextSize × size / designSize`，用 `Dp.toSp()` 换算，因此不跟随系统字体缩放（与原厂 `setTextSize` 的 px 语义一致） [StepRing.kt:StepRing]
- `Stepper` 以 `require()` 约束 `valueRange` 位于默认最小值/最大值内且 `first <= last` [Stepper.kt:Stepper]
- 值被夹取到 `valueRange`，仅当步进结果与当前值不同才调用 `onValueChange`；到边界的按钮禁用 [Stepper.kt:Stepper]
- `StepperButton` 修饰符顺序是契约：`graphicsLayer` 按压缩放 → `couiPressMotion` → `couiFocusOutline`（位于底色外侧）→ `background` → `couiStateMask(HoverPress)` → `pointerInput` → `semantics` [Stepper.kt:StepperButton]
- 长按立即提交一次，之后每 `repeatIntervalMillis` 提交，直到抬手、取消或父容器消费；每次提交触发一次触觉反馈 [Stepper.kt:StepperButton]
- 指示器宽度按最宽数字 `maximum.toString().length + 1` 次重复测量（保留 OEM 拆位偏一），并以 `indicatorMinWidth` 兜底，保证步进时数字不抖动 [Stepper.kt:indicatorWidth]

## Side Effects
- 触觉反馈：`LocalView.performCouiHapticFeedback(CouiHapticFeedback.GranularShortVibrateSync)` [Stepper.kt:StepperButton]
- 指针输入：`awaitEachGesture`/`awaitFirstDown`，使用 `longPressTimeoutMillis` 并检测越界与取消 [Stepper.kt:awaitTouchEnd]
- 无障碍语义：每个按钮设置 `Role.Button`、`contentDescription` 与 `onClick` [Stepper.kt:StepperButton]
- 资源加载：`painterResource(coui_stepper_plus/minus)` 与 `stringResource(coui_stepper_decrease/increase)` [Stepper.kt:Stepper]
- 文本测量：`rememberTextMeasurer` + `LocalDensity` 计算数字宽度 [Stepper.kt:indicatorWidth]
- 绘制：`StepBar` 的 `Canvas` 连线 [StepBar.kt:StepBar]；`couiFocusOutline` 与 `couiStateMask(CouiMaskType.HoverPress)` 效果层 [Stepper.kt:StepperButton]

## Verification
- Run: `./gradlew :coui:lintDebug`
