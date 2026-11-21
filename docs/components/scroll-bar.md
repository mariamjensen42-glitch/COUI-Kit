# ↕️滚动条

`ScrollBar` 是叠在滚动容器上的竖向滚动条：按视口与内容比例显示拇指，滚动后停留再淡出，拇指可以直接拖动。

它只读取宿主的 `ScrollState`，不接管滚动内容，也不持有业务状态。

## 最小调用

把滚动条放在滚动容器同级的 `Box` 里，并铺满该容器：

~~~kotlin
val state = rememberScrollState()
Box(Modifier.fillMaxWidth().height(280.dp)) {
    Column(Modifier.fillMaxSize().verticalScroll(state)) { /* 内容 */ }
    ScrollBar(Modifier.matchParentSize(), state = state)
}
~~~

没有 `matchParentSize()` 时组件只按自身约束取尺寸，拇指位置与拖动都会落在错误的坐标上。

## 显隐

滚动后拇指停留 2 秒（`CouiScrollBarDefaults.fadeDelayMillis`），随后在 250 毫秒内淡出；首次挂载停留 1.2 秒。拖动期间常亮，不参与淡出。

内容不足以滚动（可滚范围为 0）时不绘制。

## 拖动

只有拇指矩形命中时才接管手势，其余触摸原样留给滚动容器：

~~~kotlin
ScrollBar(Modifier.matchParentSize(), state = state)
~~~

拖动位置按原厂换算写回滚动偏移，松手后重新开始停留计时。RTL 下拇指贴起始侧，可滚范围与偏移仍取自同一个 `ScrollState`。

拖动时本组件不会取消滚动容器已经在进行的惯性滚动（Compose 没有取消 fling 的公开入口），位移会在当前滚动事务结束后应用；需要严格同步时，宿主可以在拖动开始时自行停止 fling。

## 几何与配色

固定参数归 `CouiScrollBarDefaults`：轨道宽 14dp、拇指最小高 48dp，绘制区上下留白 8dp 与 16dp，常态左右各内缩 5.5dp、按下 4dp，对应原厂 `coui_scrollbar_*` 资源。颜色取 `coui_scrollbar_color`（日间 `#29000000`、夜间 `#40FFFFFF`），可显式覆盖：

~~~kotlin
ScrollBar(Modifier.matchParentSize(), state = state,
    colors = CouiScrollBarColors(thumb = CouiTheme.colorScheme.accent))
~~~

## 装配

组件读取 Compose 的 `ScrollState`，适用于 `verticalScroll` 这类普通滚动容器。`LazyListState` 没有对应的可滚范围与视口尺寸，暂未提供适配入口；需要时由宿主自行换算，或改用普通滚动容器。

## Demo 与源码

示例见 [DemoScrollBarSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoScrollBarSamples.kt)，公开入口见 [ScrollBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/scrollbar/ScrollBar.kt)。

## 与原厂的差异

只接受 `ScrollState`，`LazyListState` 需宿主适配；不取消滚动容器已进行的 fling；几何与 2000ms 停留后 250ms 淡出按原厂。
