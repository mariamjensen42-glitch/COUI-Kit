# Checkbox

## Overview
复选框组件包。`Checkbox` 复刻 `coui_btn_check` 的几何与双向切换动效，只负责绘制；点击、无障碍语义与状态由宿主提供。

## Interface
- `public data class CouiCheckboxColors`：选中/未选中/禁用/对勾配色 [Checkbox.kt:CouiCheckboxColors]
- `public object CouiCheckboxDefaults`：原生轮廓基线、`borderShape` 与 `colors(checked, dark)` [CouiCheckboxDefaults.kt:CouiCheckboxDefaults]
- `public @Composable fun Checkbox(modifier, checked, enabled, shape, borderShape, colors)` [Checkbox.kt:Checkbox]

## Invariants
- 组件不处理点击与无障碍语义，仅绘制；宿主负责交互 [Checkbox.kt:Checkbox]
- 固定测量为 `24.dp`，几何按 `size.minDimension / 24f` 缩放 [Checkbox.kt:Checkbox]
- 仅当 `enabled`、前后均启用且 `checked` 变化时播放动画；禁用态变化直接 `snapTo(1f)` [Checkbox.kt:Checkbox]
- 反向切换从另一条时间线起点重播，时间线长度固定在 `300` 毫秒 [Checkbox.kt:Checkbox]
- 默认 `shape = CouiTheme.shapes.extraSmall`，默认色取自 `CouiTheme.colorScheme.checkboxAccent` 与 `CouiTheme.isDark` [Checkbox.kt:Checkbox]

## Side Effects
- 组合动画：`Animatable` + `LaunchedEffect(checked, enabled)` [Checkbox.kt:Checkbox]
- 绘制：`drawWithCache`/`PathMeasure`/`drawOutline` 自绘轮廓、填充与对勾 [Checkbox.kt:Checkbox]
- 读取主题 CompositionLocal 获取默认轮廓与配色 [Checkbox.kt:Checkbox]

## Verification
- Run: `./gradlew :coui:lintDebug`
