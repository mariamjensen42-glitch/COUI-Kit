# 🔄下拉刷新

`PullToRefresh` 按 ColorOS 的 `BounceLayout` + `DefaultHeader` 复刻：内容滚到顶后继续下拉累积位移，松手后锁定到头部两倍高度并回调 `onRefresh`，刷新结束时用 500ms 收回。头部是 16dp 环加「下拉刷新 / 正在刷新」文案，整体 26% 不透明度。

内容通过 `graphicsLayer` 整体下移，不改变布局尺寸，因此内部列表的滚动位置和 `Scaffold` 内边距都不受影响。位移只在本组件内部持有，不会传递成滚动位置，也不会叠到平台拉伸回弹上。

## 最小调用

~~~kotlin
var refreshing by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

PullToRefresh(
    modifier = Modifier.fillMaxSize(),
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

## 状态与位移

| 阶段 | 位移 | 头部 |
| --- | --- | --- |
| 下拉中 | 每帧增量按橡皮筋阻尼衰减，上限为屏幕高 | 环从 270° 转出，越过 40dp 后继续转到 360° |
| 松手且位移 ≥ 80dp | 800ms 均匀锁定到 80dp | 旋转继续，文案在 100ms 后淡出淡入换成「正在刷新」 |
| 刷新中 | 停在 80dp | 环转为不确定态自转 |
| 刷新结束 | 500ms 收到 0 | 文案切回「下拉刷新」 |
| 松手且位移 < 80dp | 弹簧回弹（张力倍数 2.15） | 随位移抬回 |

位移越过 80dp 时触发一次 `CouiHapticFeedback.ScrollEdge` 触感反馈，可用 `hapticFeedbackEnabled = false` 关闭。

## 参数

- `refreshing` / `onRefresh`：受控刷新态与触发回调，组件不发起请求。`refreshing` 变回假时用 500ms 收回；若业务侧只回调 `onRefresh` 而从不把 `refreshing` 置真，位移会停在锁定位置。
- `header`：收到 `progress`（位移相对 80dp 的比值，达到 1 表示已过阈值）与 `refreshing`，默认是 `CouiPullToRefreshDefaults.Header`。替换成自绘头部时保持同样的进度语义，头部高度仍取 `CouiPullToRefreshDefaults.headerHeight`。
- `dark`：头部与默认配色的深浅模式。
- `hapticFeedbackEnabled`：越过阈值时是否触发触感反馈。

头部高度、环尺寸与间距、阈值、动画时长、文案透明度都在 `CouiPullToRefreshDefaults`。

## 与滚动容器的配合

内容必须是可滚动容器（`LazyColumn`、`verticalScroll` 的 `Column` 等），否则没有滚动事件，下拉不会触发。

组件同时接管 `onPreScroll` 与 `onPostScroll`：`onPreScroll` 只在向上收（`available.y < 0`）时先收回位移，向下拉完全留给内层列表；内层滚到顶后 `onPostScroll` 把剩余位移转成下拉，并返回消费量压住平台的拉伸回弹。**不要再把组件本身套在另一层滚动容器里**，外层会先消耗下拉位移，表现为「拉不动」。样例页需要整页自持滚动时，页面外壳不能再统一包裹滚动容器。

内层列表用普通 `verticalScroll` / `LazyColumn` 即可，不需要也不应再加一个 `couiVerticalScroll`，两层位移会叠加。外层另有 `nestedScroll` 消费者（折叠顶栏）时，把它挂在更外层，顺序不影响下拉判定。

源码见 [PullToRefresh](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/pulltorefresh/PullToRefresh.kt)。

## 与原厂的差异

按原厂 `BounceLayout` + `DefaultHeader` 的度量复刻（80dp 阈值、800ms 锁定、26% 不透明度）；外层滚动容器会先吃掉下拉位移，必须直接包住目标列表。

## Demo

完整示例见 [DemoExtraSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoExtraSamples.kt)。
