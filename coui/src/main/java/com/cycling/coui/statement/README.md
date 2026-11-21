# Statement

## Overview
全屏隐私声明包。`PrivacyStatement` 按 COUI `COUIFullPageStatement` 与 `coui_full_page_statement.xml` 组装标题、可滚动正文与确认/拒绝入口；同意状态由宿主维护，组件只发出两个回调。

## Interface
- `public object CouiStatementDefaults`：标题与正文的文本样式、248dp 正文上限、174dp 按钮宽、横屏双按钮栏的 44/37/4dp 与 480dp 判定阈值 [CouiStatementDefaults.kt:CouiStatementDefaults]
- `public @Composable fun PrivacyStatement(modifier, title, statement: AnnotatedString, confirmText, exitText, onConfirm, onExit, dark)` [PrivacyStatement.kt:PrivacyStatement]
- `public @Composable fun PrivacyStatement(modifier, title, statement: String, confirmText, exitText, onConfirm, onExit, dark)`：纯文本重载 [PrivacyStatement.kt:PrivacyStatement]

## Invariants
- 标题与正文各自是独立的滚动容器：标题最多 90dp、正文最多 248dp，超出后自身滚动 [PrivacyStatement.kt:StatementLayout]
- 正文占剩余空间但不使用 `fill = true` 的权重，因此未填满时不会把按钮栏推到底部 [PrivacyStatement.kt:StatementLayout]
- `smallestScreenWidthDp < CouiStatementDefaults.compactLandscapeSmallestWidth` 且横屏时切换为一行两个等宽按钮，顺序为「拒绝 / 确认」 [PrivacyStatement.kt:StatementLayout]
- 确认与拒绝都是宿主回调；组件不持有勾选、禁用或提交中状态 [PrivacyStatement.kt:StatementLayout]

## Side Effects
- 布局：`Column` + `verticalScroll` + `heightIn`；按钮复用 `Button` 的 `Primary` / `Secondary` 样式 [PrivacyStatement.kt:StatementLayout]
- 读取 `LocalConfiguration` 的 `smallestScreenWidthDp` 与 `orientation` 决定按钮布局 [PrivacyStatement.kt:StatementLayout]

## Verification
- Run: `./gradlew :coui:lintDebug`
