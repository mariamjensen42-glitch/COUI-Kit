# 🔄下拉刷新

`PullToRefresh` 在内容滚动到顶后继续下拉时累积位移，松手超过阈值触发 `onRefresh`。

内容通过 `graphicsLayer` 整体下移，不改变布局尺寸，因此内部列表的滚动位置和 `Scaffold` 内边距都不受影响。刷新状态由宿主持有：`refreshing` 为真时位移停在阈值，变回假时自动回弹。

## 最小调用

~~~kotlin
var refreshing by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

PullToRefresh(
    refreshing = refreshing,
    onRefresh = {
        refreshing = true
        scope.launch {
            repository.reload()
            refreshing = false
        }
    },
) {
    LazyColumn(Modifier.fillMaxSize()) { /* 列表 */ }
}
~~~

## 参数

- `refreshing` / `onRefresh`：受控刷新态与触发回调，组件不发起请求。
- `indicator`：收到 `progress`（0..1）与 `refreshing`，默认是环形进度——下拉时按进度填充，刷新时转为不确定态旋转。替换成自绘指示器时保持同样的进度语义。
- `dark`：指示器与默认配色的深浅模式。

触发距离、最大拉伸比例与指示器尺寸在 `CouiPullToRefreshDefaults`。内容必须是可滚动容器（`LazyColumn`、`couiVerticalScroll` 的 `Column` 等），否则没有 overscroll 事件，下拉不会触发。

## 与滚动容器的配合

组件用 `nestedScroll` 的 `onPostScroll` 判断「已到顶且仍有向下位移」，不会抢走列表自身的滚动。如果外层还有别的 `nestedScroll` 消费者（折叠顶栏），把它们挂在更外层即可，顺序不影响下拉判定。

源码见 [PullToRefresh](https://github.com/ItosEO/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/pulltorefresh/PullToRefresh.kt)。
