# QuickSetting

## Overview
控制中心磁贴包。`QuickSettingTile` 提供小/大两种开关磁贴，`DeformationTileGrid` 实现 1×1 与 2×1 跨度切换和弹簧重排，`LevelSlider` 提供竖排档位条；尺寸与行为对齐 ColorOS 17 控制中心。

## Interface
- `public enum class CouiQuickSettingSize { Small, Large }` [QuickSettingTile.kt:CouiQuickSettingSize]
- `public object CouiQuickSettingDefaults`：单元/间距/大磁贴/图标/标签尺寸 [QuickSettingTile.kt:CouiQuickSettingDefaults]
- `public @Composable fun QuickSettingTile(modifier, label, icon, checked, onToggle, size, enabled, dark, colors, containerColor, checkedContainerColor, iconContainerColor, shape)` [QuickSettingTile.kt:QuickSettingTile]
- `public enum class CouiDeformationSpan(val columns: Int) { OneByOne(1), TwoByOne(2) }` [CouiDeformationTileDefaults.kt:CouiDeformationSpan]
- `public @Immutable data class CouiDeformationTileColors(container, stroke, handle)` [CouiDeformationTileDefaults.kt:CouiDeformationTileColors]
- `public object CouiDeformationTileDefaults`：列数/间距/圆角/描边/把手、`spanToggleFraction`、弹簧 response 与 `colors(dark)` [CouiDeformationTileDefaults.kt:CouiDeformationTileDefaults]
- `internal data class CouiDeformationPlacement(column, row)`、`internal fun flowDeformationSpans(spans, columns)` [DeformationTileGrid.kt:flowDeformationSpans]
- `public @Composable fun <T> DeformationTileGrid(items, spanOf, modifier, columns, editing, gap, cornerRadius, colors, onSpanChange, tile)` [DeformationTileGrid.kt:DeformationTileGrid]
- `public object CouiLevelSliderDefaults`：62×138dp 档位条、图标内边距与尺寸 [LevelSlider.kt:CouiLevelSliderDefaults]
- `public @Composable fun LevelSlider(modifier, value, onValueChange, icon, dark, colors, containerColor, fillColor, shape, enabled)` [LevelSlider.kt:LevelSlider]

## Invariants
- `QuickSettingTile` 的底色：开启用 `checkedContainerColor`；关闭用 `containerColor`，未传时深色 20% 白、浅色卡片色；禁用内容色为 `colors.disabled` [QuickSettingTile.kt:QuickSettingTile]
- 小磁贴标签位于磁贴外下方且宽 `cellSize + gap`；大磁贴标签在磁贴内部 [QuickSettingTile.kt:QuickSettingTile]
- 整卡（含小磁贴下方标签）由 `toggleable` 承担点击并暴露 `Role.Switch` [QuickSettingTile.kt:QuickSettingTile]
- 磁贴与档位条不自带材质：`containerColor = Color.Transparent` 后宿主用 `Modifier.couiFloatingSurface` 包裹，`shape` 需两处一致 [QuickSettingTile.kt:QuickSettingTile]、[LevelSlider.kt:LevelSlider]
- `flowDeformationSpans` 要求 `columns > 0`；跨度先夹到列数，放不下的 2×1 磁贴整体换行 [DeformationTileGrid.kt:flowDeformationSpans]
- `DeformationTileGrid` 要求 `columns > 0`；编辑态叠加描边与右缘把手，横向拖动超过一格宽度的 `spanToggleFraction` 切换跨度并回调 `onSpanChange` [DeformationTileGrid.kt:DeformationTileGrid]
- `LevelSlider` 将 `value` 夹到 `0f..1f`，按下/拖动按纵向位置映射数值；不做步进、触感或 RTL 镜像 [LevelSlider.kt:LevelSlider]
- `LevelSlider` 与横向 `Slider` 不是同一控件；深色默认白色填充，浅色默认强调色 [LevelSlider.kt:LevelSlider]

## Side Effects
- 指针输入：`toggleable` [QuickSettingTile.kt:QuickSettingTile]、`detectHorizontalDragGestures` [DeformationTileGrid.kt:DeformationTileFrame]、`awaitEachGesture`/`drag` [LevelSlider.kt:LevelSlider]
- 无障碍：`Role.Switch` [QuickSettingTile.kt:QuickSettingTile] 与 `ProgressBarRangeInfo` [LevelSlider.kt:LevelSlider]
- 动画：`animateDpAsState`（重排弹簧、描边 120ms tween）[DeformationTileGrid.kt:DeformationTileGrid]
- 绘制：`Canvas` 绘制编辑态圆角描边 [DeformationTileGrid.kt:DeformationTileFrame]
- 按压反馈：`couiButtonFeedback(list = true)` [QuickSettingTile.kt:QuickSettingTile]
- 读取 `CouiTheme.isDark`/`colorSchemeFor` 获取默认配色 [CouiDeformationTileDefaults.kt:CouiDeformationTileDefaults.colors]

## Verification
- Run: `./gradlew :coui:lintDebug`
