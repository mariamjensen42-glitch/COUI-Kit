# 🧩三态容器

`StateLayout` 按 `CouiLoadState` 在加载、内容、空与失败之间切换，把「四种状态各写一个 `when`」收敛成一个入口。

它只做状态分发与占位排版：不内置文案、不发起请求、不缓存上一次的内容。空态和失败态的 UI 由宿主提供，`EmptyState`、`Skeleton`、`CircularProgress` 都可以直接放进去。

## 最小调用

~~~kotlin
StateLayout(
    state = state,
    empty = { EmptyState(title = "暂无内容", summary = "下拉刷新试试") },
    failed = { message ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message ?: "加载失败", kind = CouiTextKind.Body)
            Button(onClick = { load() }, label = "重试", style = CouiButtonStyle.Text)
        }
    },
) {
    ContentList(items)
}
~~~

## 状态

~~~kotlin
sealed interface CouiLoadState {
    object Loading
    object Ready
    object Empty
    data class Failed(val message: String? = null)
}
~~~

- `Loading`：默认居中显示不确定态 `CircularProgress`，可用 `loading` 槽替换成骨架屏。加载态没有与原厂同源的整页规则，保持居中。
- `Ready`：内容按整块区域布局，不额外加内边距。
- `Empty` / `Failed`：按原厂 `COUIEmptyStateView` 的整页规则摆放，见下一节。

切换状态时内容会离开组合，列表滚动位置由宿主自行保存（例如用 `rememberSaveable` 的 `LazyListState`）。

## 整页几何

空态与失败态的摆位取自原厂 `COUIEmptyStateView` 的 `onMeasure` / `onLayout`，不是简单居中：

- 高度由外部固定（`Modifier.fillMaxSize()`、固定高容器等 min 与 max 相等的情况）时用容器高度；未固定时容器收拢到槽内容高度，顶部留白为 0。
- 槽内容的顶部留白 = (容器高 − 槽高) × 0.45，取整后不小于 0；尺寸类型为 `Small` 时改用 0.5。原厂常量 `HEIGHT_RATIO_NORMAL` / `HEIGHT_RATIO_SMALL`。
- 槽内容水平居中，宽度按容器宽度测量。

尺寸类型由容器可用尺寸判定，对应原厂 `EMPTY_VIEW_SIZE_TYPE_*`：

| 判定 | 条件 | 表现 |
| --- | --- | --- |
| `Small` | 高度 < 200dp（高度无界时按 0 计） | 顶部留白 0.5；槽内 `EmptyState` 跳过图标槽，与原厂把插图区缩到 0 × 0 一致 |
| `Medium` | 宽 < 320dp 或高 < 480dp | 顶部留白 0.45，图标槽保留 |
| `Large` | 其余情况 | 顶部留白 0.45，图标槽保留 |

阈值对应原厂 `coui_component_height_threshold_small`、`coui_component_width_threshold_medium`、`coui_component_height_threshold_medium`，判定结果通过 `emptyPageSize` 显式覆盖：

~~~kotlin
StateLayout(
    state = state,
    emptyPageSize = CouiEmptyPageSize.Large, // null = 按可用尺寸判定
    empty = { EmptyState(title = "暂无内容") },
    failed = { message -> FailureContent(message) },
) {
    ContentList(items)
}
~~~

判定结果同时决定槽内 `EmptyState` 是否渲染图标槽，宿主无需自己判断。

源码见 [StateLayout](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/statelayout/StateLayout.kt)。
