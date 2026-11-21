# StateLayout

## Overview
加载状态分发容器包。`StateLayout` 按 `CouiLoadState` 显示加载/就绪/空/失败内容，并按原厂 `COUIEmptyStateView` 规则摆放空态与失败态，向其槽位下发页面尺寸类型。

## Interface
- `public @Immutable sealed interface CouiLoadState`：`Loading`、`Ready`、`Empty` 与 `Failed(message)` [StateLayout.kt:CouiLoadState]
- `public @Composable fun StateLayout(modifier, state, empty, failed, loading, emptyPageSize, content)` [StateLayout.kt:StateLayout]

## Invariants
- 状态分发固定：`Loading` 为 `fillMaxSize` 居中 Box，`Ready` 为 `fillMaxSize` Box，`Empty`/`Failed` 都交给 `EmptyPageSlot` [StateLayout.kt:StateLayout]
- `EmptyPageSlot` 先解析 `pageSize`（显式参数优先，否则按约束判定），再通过 `LocalCouiEmptyPageSize` 下发给槽内容并测量 [StateLayout.kt:EmptyPageSlot]
- 顶部留白仅在 `constraints.hasFixedHeight` 时计算为 `(height - slotHeight) * ratio`，`Small` 取 0.5、其余 0.45，并 `coerceAtLeast(0)`；高度不固定时容器收拢到槽高且顶部为 0 [StateLayout.kt:EmptyPageSlot]
- 槽内容按 `maxWidth`/`maxHeight` 测量（无界时为 `Constraints.Infinity`），水平居中 [StateLayout.kt:EmptyPageSlot]
- 尺寸判定：高度小于 `pageHeightThresholdSmall` 为 `Small`；宽度小于 `pageWidthThresholdMedium` 或高度小于 `pageHeightThresholdMedium` 为 `Medium`；否则 `Large`；`Infinity` 参与判定时按 0 处理 [StateLayout.kt:resolvePageSize]
- 切换 `state` 会让旧内容离开组合，宿主须自行保存滚动位置 [StateLayout.kt:StateLayout]

## Side Effects
- CompositionLocal 供给：向 empty/failed 槽下发 `LocalCouiEmptyPageSize` [StateLayout.kt:EmptyPageSlot]
- `SubcomposeLayout` 子组合并测量槽内容，再按结果 layout/place [StateLayout.kt:EmptyPageSlot]
- 默认 loading 槽组合 `CircularProgress()` [StateLayout.kt:StateLayout]
- 读取 `Density` 并用 `roundToPx` 比较页面尺寸阈值 [StateLayout.kt:resolvePageSize]

## Verification
- Run: `./gradlew :coui:lintDebug`
