# SidePane

## Overview
侧栏容器包。`SidePane` 以常驻页面内容为底，按 `visible` 从 `Start`/`End` 贴边滑出侧栏，遮罩点击回传关闭请求；不绑定导航、返回键或业务提交。

## Interface
- `public enum class CouiSidePaneSide { Start, End }`：RTL 下角与滑动方向自动镜像 [SidePane.kt:CouiSidePaneSide]
- `public @Immutable data class CouiSidePaneColors(container, scrim)` [SidePane.kt:CouiSidePaneColors]
- `public object CouiSidePaneDefaults`：`width = 280.dp`、`cornerRadius = 32.dp`、`maxWidthFraction = 0.85f`、进退场时长、`shape(side, radius)` 与 `colors(dark)` [SidePane.kt:CouiSidePaneDefaults]
- `internal val enterEasing`/`exitEasing`：进退场缓动曲线 [SidePane.kt:CouiSidePaneDefaults.enterEasing]
- `public @Composable fun SidePane(modifier, visible, onDismissRequest, dark, side, width, colors, paneTitle, pane, content)` [SidePane.kt:SidePane]

## Invariants
- 侧栏在 `visible || retained` 时组合；`visible` 变真先置 `retained = true`，收起动画播完才置 `false` [SidePane.kt:SidePane]
- `paneWidth = min(width, maxWidth * maxWidthFraction)` [SidePane.kt:SidePane]
- `collapseToLeft = (side == Start) != rtl`；收起位移按所贴边向外，符号随 RTL 翻转 [SidePane.kt:SidePane]
- 遮罩透明度为 `colors.scrim.alpha * progress`，以 `indication = null` 的 `clickable` 调用 `onDismissRequest` [SidePane.kt:SidePane]
- 侧栏用 `shape(side)` 只圆外缘两角，并以 `detectTapGestures` 吃掉自身空白区点击，避免穿透到遮罩 [SidePane.kt:SidePane]
- `paneTitle` 非空时才附加 `semantics { paneTitle }` [SidePane.kt:SidePane]
- `content` 始终参与布局；组件不注册返回键，宿主导航层须自行调用 `onDismissRequest` [SidePane.kt:SidePane]

## Side Effects
- 读取 `LocalLayoutDirection` 决定左右贴边与收起位移符号 [SidePane.kt:SidePane]
- 无障碍：非空 `paneTitle` 写入 `paneTitle` 语义属性 [SidePane.kt:SidePane]
- 指针输入：遮罩 `clickable(indication = null)`，侧栏 `detectTapGestures` [SidePane.kt:SidePane]
- 绘制与动画：`drawBehind` 遮罩、`graphicsLayer` 平移、`Animatable`/tween 驱动 250ms 展开与 200ms 收起 [SidePane.kt:SidePane]
- 读取 `CouiTheme.isDark` 生成日夜容器色与遮罩色 [SidePane.kt:CouiSidePaneDefaults.colors]

## Verification
- Run: `./gradlew :coui:lintDebug`
