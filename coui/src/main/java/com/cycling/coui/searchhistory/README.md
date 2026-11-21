# SearchHistory

## Overview
COUI 搜索历史区组件包。`SearchHistory` 渲染标题、删除按钮与可折叠 `FlowLayout`；`items`、`expanded`、点击与删除均由宿主持有，删除动画结束后才回调 `onDelete`。`CouiSearchHistoryDefaults` 提供固定间距、命中区与淡出时长。

## Interface
- `SearchHistory` — public @Composable 搜索历史区入口：modifier、title、items、expanded、onExpandedChange、onItemClick、onDelete、deleteContentDescription、maxRowsFolded、dark、colors、expand/collapseContentDescription、deleteContent `[SearchHistory.kt:SearchHistory]`
- `CouiSearchHistoryDefaults` — public 固定参数对象：start/end 16dp、top 16dp、bottom 32dp、标题上下 7dp、删除图标 18dp 与 48dp 命中区、FlowLayout 上边距 8dp、删除淡出 300ms `[CouiSearchHistoryDefaults.kt:CouiSearchHistoryDefaults]`

## Invariants
- items 为空时在组合开始即 return，不渲染标题、删除按钮或 FlowLayout `[SearchHistory.kt:SearchHistory]`
- 删除流程：点击仅在 !deleting 时置 deleting=true；LaunchedEffect 以 tween(deleteDurationMillis=300ms, CouiFlowLayoutDefaults.MoveEase) 把 deleteAlpha 动画到 0，随后调用 onDelete，重置 deleting=false 并 snapTo(1f) `[SearchHistory.kt:SearchHistory]`
- deleteAlpha 的 Animatable.value 不参与快照观察，必须经 asState() 读取才能让淡出帧触发重组 `[SearchHistory.kt:SearchHistory]`
- 删除命中区固定 48dp、内部图标 18dp，点击使用 indication=null 与 Role.Button，contentDescription 由 deleteContentDescription 提供 `[SearchHistory.kt:SearchHistory]`
- FlowLayout 展开上限固定为 CouiFlowLayoutDefaults.searchHistoryMaxRowsUnfolded，折叠行数由 maxRowsFolded（默认 CouiFlowLayoutDefaults.maxRowsFolded）控制 `[SearchHistory.kt:SearchHistory]`
- 固定间距来自 CouiSearchHistoryDefaults：外层 start/end 16dp、top 16dp、bottom 32dp，标题栏 vertical 7dp，FlowLayout top 8dp `[SearchHistory.kt:SearchHistory]`
- 标题使用 CouiTextKind.ButtonLabel 与 CouiTheme.colorSchemeFor(dark).onSurface，并占用剩余宽度 weight(1f) `[SearchHistory.kt:SearchHistory]`
- 默认删除图标为私有自绘 Canvas 图标（18dp、线宽 1.2dp），核心不依赖图标模块 `[SearchHistory.kt:SearchHistoryDeleteGlyph]`
- deleteDurationMillis 是 public const 300，直接决定整块淡出时长与宿主清空 items 的时序 `[CouiSearchHistoryDefaults.kt:deleteDurationMillis]`

## Side Effects
- 语义：删除按钮通过 semantics 设置 contentDescription `[SearchHistory.kt:SearchHistory]`
- 指针输入：删除按钮 clickable 且无 ripple indication（Role.Button） `[SearchHistory.kt:SearchHistory]`
- 动画时钟：Animatable.animateTo(tween 300ms) 驱动 Column 整块 alpha 淡出 `[SearchHistory.kt:SearchHistory]`
- 绘制：默认删除图标以 Compose Canvas 绘制垃圾桶轮廓线条 `[SearchHistory.kt:SearchHistoryDeleteGlyph]`

## Verification
- Run: `./gradlew :coui:lintDebug`
