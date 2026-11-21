# Tooltip

## Overview
COUI 气泡提示包：Tooltip 在锚点上方创建非焦点 Popup，上方放不下时改为锚点下方并在窗口内夹紧。可见性由宿主持有，组件不安装长按/悬停/点击手势，触发方式由调用方决定。

## Interface
- `public @Composable fun Tooltip(...)` - 锚点上方的气泡 Popup，`text` 为气泡文本、`content` 为锚点内容，两者均为必填 `[Tooltip.kt:Tooltip]`
- `public @Immutable data class CouiTooltipColors`、`public object CouiTooltipDefaults` + `colors(accent)` - 容器/内容配色与 shape/gap/padding/maxWidth `[Tooltip.kt:CouiTooltipColors]` `[Tooltip.kt:CouiTooltipDefaults]` `[Tooltip.kt:colors]`

## Invariants
- 可见性由宿主持有：visible=true 才创建 Popup，false 不注册任何弹层 `[Tooltip.kt:Tooltip]`
- PopupProperties：focusable=false、dismissOnClickOutside=true、dismissOnBackPress=true、usePlatformDefaultWidth=false，气泡不改变页面焦点顺序 `[Tooltip.kt:Tooltip]`
- 组件不安装长按/悬停/点击手势，避免抢占子节点输入 `[Tooltip.kt:Tooltip]`
- 容器 maxWidth=240dp、四边 padding=13dp，圆角与背景取 CouiTheme.shapes.medium `[Tooltip.kt:Tooltip]`
- CouiTooltipPositionProvider 水平居中后 coerce 到窗口内；垂直优先上方，放不下则改为 anchorBounds.bottom+gap 并夹紧窗口 `[Tooltip.kt:CouiTooltipPositionProvider]`

## Side Effects
- 使用 androidx.compose.ui.window.Popup/PopupPositionProvider/PopupProperties 创建独立弹层 `[Tooltip.kt:Tooltip]`
- 读取 LocalDensity 将 gap 转 px；remember 缓存 PositionProvider 与 PopupProperties `[Tooltip.kt:Tooltip]`
- 默认参数读取 CouiTheme.isDark 与 colorSchemeFor(dark).accent；ProvideCouiContent 提供内容色，text 包的 `Text` 渲染文本 `[Tooltip.kt:Tooltip]`
- Popup 在外部点击或返回键时回调 onDismissRequest `[Tooltip.kt:Tooltip]`

## Verification
- Run: `./gradlew :coui:lintDebug`
