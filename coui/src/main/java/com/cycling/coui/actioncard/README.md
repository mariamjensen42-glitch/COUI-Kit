# ActionCard

## Overview
页面内操作卡的组件包。`ActionCard` 组合图标、可选标题/说明与调用方提供的操作组，用于手机管家类页面的提示卡与推荐卡两种形态；容器与配色复用页面 token。

## Interface
- `public object CouiActionCardDefaults`：内留白、图标尺寸/间距、标题说明间距与操作区间距 [ActionCard.kt:CouiActionCardDefaults]
- `public @Composable fun ActionCard(modifier, summary, title, icon, colors, actions)` [ActionCard.kt:ActionCard]

## Invariants
- `title` 为 null 或空时进入提示卡形态，说明使用 `CouiTextKind.BodyLarge`；否则为推荐卡形态，说明使用 `ItemSummary` [ActionCard.kt:ActionCard]
- 整卡不承担点击，交互只来自 `actions` 中的子控件 [ActionCard.kt:ActionCard]
- 图标槽固定 `CouiActionCardDefaults.iconSize`，库不对调用方图标做 tint [ActionCard.kt:ActionCard]
- 容器固定 `CouiTheme.shapes.card` + `colors.card`，操作组右对齐且顶部固定间距 [ActionCard.kt:ActionCard]

## Side Effects
- 读取 `CouiPageDefaults.themeColors()` 作为配色默认值 [ActionCard.kt:ActionCard]
- 绘制：按 `clip`/`background` 顺序绘制圆角容器 [ActionCard.kt:ActionCard]

## Verification
- Run: `./gradlew :coui:lintDebug`
