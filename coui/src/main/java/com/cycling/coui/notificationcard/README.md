# NotificationCard

## Overview
通知卡片组件包。`NotificationCard` 按 ColorOS 17 通知中心单条通知布局图标、标题、相对时间与正文；只负责排版，不自带材质，凝光玻璃等由宿主通过浮动表面链路叠加。

## Interface
- `public object CouiNotificationCardDefaults`：轮廓基线、留白、图标槽、标题/时间间距与最小高度 [NotificationCard.kt:CouiNotificationCardDefaults]
- `public @Composable fun NotificationCard(modifier, title, body, time, icon, dark, colors, containerColor, shape, bodyMaxLines)` [NotificationCard.kt:NotificationCard]

## Invariants
- 组件不自带材质：需要凝光玻璃时宿主传 `Color.Transparent` 并用同一 `shape` 包裹 `Modifier.couiFloatingSurface` [NotificationCard.kt:NotificationCard]
- 标题固定单行省略；正文最多 `bodyMaxLines` 行省略 [NotificationCard.kt:NotificationCard]
- `time` 与 `icon` 可省略；图标由调用方提供，库不做 tint [NotificationCard.kt:NotificationCard]
- 默认 `shape = CouiTheme.shapes.card`，默认 `containerColor = colors.card` [NotificationCard.kt:NotificationCard]

## Side Effects
- 读取 `CouiTheme.isDark` 与页面配色 token 获取默认外观 [NotificationCard.kt:NotificationCard]
- 绘制：`clip(shape)` + `background(containerColor)` 容器 [NotificationCard.kt:NotificationCard]

## Verification
- Run: `./gradlew :coui:lintDebug`
