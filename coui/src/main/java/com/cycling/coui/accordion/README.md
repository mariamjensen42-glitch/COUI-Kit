# Accordion

## Overview
可展开分组的组件包。`Accordion` 由常驻标题行与按需挂载的内容区组成，展开状态由宿主持有；标题行复用页面列表的按压反馈与排版 token，箭头由组件自绘。

## Interface
- `public object CouiAccordionDefaults`：箭头尺寸/描边/间距与内容底部留白 [Accordion.kt:CouiAccordionDefaults]
- `public @Composable fun Accordion(modifier, title, expanded, onExpandedChange, summary, enabled, dark, colors, content)` [Accordion.kt:Accordion]

## Invariants
- `expanded` 为受控状态：组件不持有展开态，点击只调用 `onExpandedChange(!expanded)` [Accordion.kt:Accordion]
- 标题行在 `enabled = false` 时禁用点击与按压反馈 [Accordion.kt:Accordion]
- 内容区仅在 `expanded` 时由 `AnimatedVisibility(expandVertically/shrinkVertically)` 挂载 [Accordion.kt:Accordion]
- 标题/说明固定使用 `CouiTextKind.ItemTitle`/`ItemSummary`，颜色取自 `CouiPageColors.title`/`summary` [Accordion.kt:Accordion]
- 箭头旋转目标为 `expanded` 时 180 度，由 `graphicsLayer` 应用 [Accordion.kt:Accordion]

## Side Effects
- 读取 `CouiTheme.isDark` 作为 `dark` 默认值 [Accordion.kt:Accordion]
- 指针输入：`clickable` 交互源 + `couiButtonFeedback(list = true)` 按压蒙层，语义角色 `Role.Button` [Accordion.kt:Accordion]
- 绘制：`drawBehind` 自绘两段折线箭头 [Accordion.kt:AccordionArrow]
- 组合动画：`animateFloatAsState` 与 `AnimatedVisibility` [Accordion.kt:Accordion]

## Verification
- Run: `./gradlew :coui:lintDebug`
