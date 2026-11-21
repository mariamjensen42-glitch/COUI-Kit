# Badge

## Overview
状态角标包。`Badge` 按 `count` 渲染红点或数字角标，`BadgedBox` 把角标叠放在内容右上角；角标本身不处理点击。

## Interface
- `public object CouiBadgeDefaults`：红点尺寸、角标高度、按位数宽度、字号、`maxCount`、偏移与配色 [Badge.kt:CouiBadgeDefaults]
- `public @Composable fun Badge(modifier, count, maxCount, dark, color, contentColor)` [Badge.kt:Badge]
- `public @Composable fun BadgedBox(modifier, badge, offset, content)` [Badge.kt:BadgedBox]

## Invariants
- `count == null` 显示红点；`count > 0` 显示数字；`count <= 0` 不占位 [Badge.kt:Badge]
- 数字超过 `maxCount` 时显示 `"$maxCount+"` [Badge.kt:Badge]
- 宽度按标签长度取 1/2/3 位以上三档 [Badge.kt:Badge]
- 角标只表达状态，不处理点击；点击与语义由宿主在角标外层提供 [Badge.kt:Badge]
- `BadgedBox` 不改变内容尺寸，角标可超出内容边界；RTL 下水平偏移取反 [Badge.kt:BadgedBox]

## Side Effects
- 读取 `LocalDensity`（dp→sp）与 `LocalLayoutDirection`（RTL 翻转）[Badge.kt:Badge]
- 绘制：`clip` + `background`，数字使用 `BasicText` 单行排版 [Badge.kt:Badge]
- 读取 `CouiTheme.isDark` 作为 `dark` 默认值 [Badge.kt:Badge]

## Verification
- Run: `./gradlew :coui:lintDebug`
