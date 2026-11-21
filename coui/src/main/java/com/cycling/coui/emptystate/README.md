# EmptyState

## Overview
整页空状态组件包。`EmptyState` 按原厂 `coui_component_empty_state.xml` 排列图标槽、标题、说明与操作槽；页面尺寸类型由 `StateLayout` 判定并通过 `LocalCouiEmptyPageSize` 下传。

## Interface
- `public @Immutable data class CouiEmptyStateColors(title, description)` [EmptyState.kt:CouiEmptyStateColors]
- `public enum class CouiEmptyPageSize { Small, Medium, Large }` [EmptyState.kt:CouiEmptyPageSize]
- `internal val LocalCouiEmptyPageSize: ProvidableCompositionLocal<CouiEmptyPageSize?>`：`null` 表示独立使用 [EmptyState.kt:LocalCouiEmptyPageSize]
- `public object CouiEmptyStateDefaults`：图标槽/文字列/操作间距、行数上限、整页留白比例与尺寸阈值、`colors(dark)` [EmptyState.kt:CouiEmptyStateDefaults]
- `public @Composable fun EmptyState(modifier, title, description, dark, colors, icon, actionLabel, onAction, action)` [EmptyState.kt:EmptyState]

## Invariants
- 两种操作入口互斥：同时传入 `action` 与 `actionLabel`/`onAction` 时以 `action` 内容槽为准 [EmptyState.kt:EmptyState]
- 容器判定为 `CouiEmptyPageSize.Small` 时跳过图标槽，对应原厂把插图区缩到 0×0 并隐藏 [EmptyState.kt:EmptyState]
- 标题最多 2 行、说明最多 3 行，文字块限高 92dp 且内部滚动 [EmptyState.kt:EmptyState]
- 组件不自行垂直居中；整页摆位由 `StateLayout` 按 45%/50% 顶部留白比例提供 [EmptyState.kt:EmptyState]
- 内置文字按钮使用 `CouiTextKind.ButtonLabel` 与强调色，且 `pressFeedback = false`（无容器文字按钮）[EmptyState.kt:EmptyStateActionLabel]
- 图标槽与操作槽内容由宿主提供，组件不内置插图或弹窗 [EmptyState.kt:EmptyState]

## Side Effects
- 消费 `LocalCouiEmptyPageSize` CompositionLocal 决定是否渲染图标槽 [EmptyState.kt:LocalCouiEmptyPageSize]
- 内部滚动状态：`rememberScrollState` + `verticalScroll` [EmptyState.kt:EmptyState]
- 指针/无障碍：文字按钮 `clickable(Role.Button)` + `couiButtonFeedback` [EmptyState.kt:EmptyStateActionLabel]
- 读取 `CouiTheme.isDark` 与 `colorSchemeFor` 获取默认文字配色 [EmptyState.kt:CouiEmptyStateDefaults.colors]

## Verification
- Run: `./gradlew :coui:lintDebug`
