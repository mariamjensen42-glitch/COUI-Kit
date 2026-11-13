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

- `Loading`：默认居中显示不确定态 `CircularProgress`，可用 `loading` 槽替换成骨架屏。
- `Ready`：内容按整块区域布局，不额外加内边距。
- `Empty` / `Failed`：居中显示宿主提供的槽。

切换状态时内容会离开组合，列表滚动位置由宿主自行保存（例如用 `rememberSaveable` 的 `LazyListState`）。

源码见 [StateLayout](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/statelayout/StateLayout.kt)。
