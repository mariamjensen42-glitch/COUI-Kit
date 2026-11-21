# RadioButton

## Overview
单选按钮组件包。`RadioButton` 复刻 `coui_btn_radio` 的圆形几何与双向切换动画，只负责绘制；`selectableGroup`、`selectable` 与 `RadioButton` 语义由宿主提供。

## Interface
- `public data class CouiRadioButtonColors`：选中/未选中/禁用/中心点配色 [CouiRadioButtonDefaults.kt:CouiRadioButtonColors]
- `public object CouiRadioButtonDefaults`：`colors(selected, dark)` 配色工厂 [CouiRadioButtonDefaults.kt:CouiRadioButtonDefaults]
- `public @Composable fun RadioButton(modifier, selected, enabled, colors)` [RadioButton.kt:RadioButton]

## Invariants
- 组件只绘制，不提供选择交互或无障碍语义 [RadioButton.kt:RadioButton]
- 基准尺寸 `24.dp`，时间线固定 `0..300f`；仅启用态下 `selected` 变化时播放动画 [RadioButton.kt:RadioButton]
- 选中/取消使用不同缓动曲线，取消动画的轮廓/中心缩放按 267f 分段 [RadioButton.kt:RadioButton]
- 禁用态直接 `snapTo(300f)`，不播放过渡 [RadioButton.kt:RadioButton]
- 默认色取自 `CouiTheme.colorScheme.accent` 与 `CouiTheme.isDark` [RadioButton.kt:RadioButton]

## Side Effects
- 组合动画：`Animatable` + `LaunchedEffect(selected, enabled)` [RadioButton.kt:RadioButton]
- 绘制：`drawWithCache` 上以 `drawCircle` 绘制轮廓、填充与中心点 [RadioButton.kt:RadioButton]
- 读取主题 CompositionLocal 获取默认配色 [RadioButton.kt:RadioButton]

## Verification
- Run: `./gradlew :coui:lintDebug`
