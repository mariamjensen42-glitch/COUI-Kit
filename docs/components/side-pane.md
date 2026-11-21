# 🚪侧栏

`SidePane` 从窗口的一侧滑出，承载与当前页面相关的导航或设置。页面内容常驻不动，侧栏覆盖在上面，因此下层的滚动位置、输入内容与展开状态都不受影响。

## 最小调用

页面持有显隐状态，组件只通过回调请求关闭：

~~~kotlin
var open by remember { mutableStateOf(false) }

SidePane(
    visible = open,
    onDismissRequest = { open = false },
) {
    Item(onClick = { open = false }) { Text(text = "设置", kind = CouiTextKind.ItemTitle) }
}
~~~

`visible` 是受控状态，组件不会自行改为 `false`。点击遮罩只回调 `onDismissRequest`，是否关闭由页面决定。收起动画播完之前组件仍保留侧栏，因此调用点可以直接写 `visible = open`，不需要额外的保留状态。

## 页面内容与侧栏

`content` 是常驻的页面内容槽，`pane` 是侧栏内容槽：

~~~kotlin
SidePane(
    visible = open,
    onDismissRequest = { open = false },
    side = CouiSidePaneSide.End,
    paneTitle = "设置",
    pane = { /* 侧栏内容，ColumnScope */ },
) {
    SettingsPage()
}
~~~

`content` 始终参与布局，可以直接包住整页。侧栏不跟手拖动：需要边缘滑动展开时，由宿主在 `modifier` 外层接手势；库只保证开合转场与遮罩的一致性。

`pane` 是 `ColumnScope`，组件不附加任何内边距，留白由内容自己给出。侧栏贴在窗口边缘，**系统栏留白也由内容自己给**：需要避开状态栏与导航栏时，在 `pane` 里加 `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`，库不替宿主决定是否要让侧栏压到系统栏下面。

## 铺满整页

遮罩和侧栏都以组件自身的位置与尺寸为基准，而不是窗口。组件默认铺满宿主给它的容器，所以宿主应该把它放在页面根部、让它拿到整页尺寸：

~~~kotlin
Box(Modifier.fillMaxSize()) {
    SidePane(visible = open, onDismissRequest = { open = false }) {
        SettingsPage()
    }
}
~~~

塞进列表、分组卡片或滚动区里的某一个格子时，组件只会和那个格子一样大，遮罩就只盖住那一小块，页面其余区域既不压暗，也点不到关闭。需要限制侧栏所在的区域时，用一个铺满该区域的容器包住它，而不是依赖组件自己去测量窗口。

## 贴靠方向

`side` 决定侧栏贴哪一侧并自动处理镜像：

- `CouiSidePaneSide.Start`（默认）：贴在起始边，收起时向左滑出；从右到左的布局里自动翻到右侧。
- `CouiSidePaneSide.End`：贴在结束边，收起时向右滑出。

外侧两个角是圆角（`CouiSidePaneDefaults.cornerRadius`，32dp），贴边一侧保持直角，视觉上像从窗口边缘抽出来的一页。

## 宽度

`width` 默认 280dp，在窄窗口下最多占容器宽度的 85%（`CouiSidePaneDefaults.maxWidthFraction`），所以分屏窄窗里不会占满整屏：

~~~kotlin
SidePane(visible = open, onDismissRequest = { open = false }, width = 320.dp) { ... }
~~~

宽度在布局阶段按实际约束计算，窗口宽度变化时自动重新收窄，不需要宿主监听配置。

## 遮罩与返回键

遮罩由组件自己绘制，强度取 `CouiSidePaneColors.scrim`，并与滑入同步淡入淡出：日间 `#33000000`、夜间 `#99000000`，与底部面板同一套配比。

遮罩只在侧栏以外的区域响应点击；侧栏空白处的点击会被面板自己吞掉，不会误触发关闭。

库没有注册返回键的入口（`coui` 模块不依赖 `activity-compose`）。宿主应在自己的导航层里把返回手势接成 `onDismissRequest`，并保证同一时刻只有一层浮层响应返回——Demo 用 `BackHandler` 演示这一点：

~~~kotlin
// 展开时只收起侧栏, 不让返回手势直接退出页面。
BackHandler(open) { open = false }
~~~

这段要写在 `SidePane` 所在的组合作用域里，且在页面自己的返回处理之前，否则第一次返回就会退出整页。

## 动画与资源

展开 250ms、收起 200ms，插值器沿用与原厂 `anim/android_alert_dialog_enter` / `_exit` 同一组贝塞尔曲线（`0.3, 0, 0.1, 1` 与 `0.3, 0, 1, 1`）。位移按侧栏自身宽度计算，遮罩透明度与位移共用同一个进度值。

进度值只在绘制阶段读取，动画不产生逐帧重组；中途反向会从当前进度继续。

原厂 `com.coui.appcompat.sidepane.COUISidePaneLayout` 在未展开状态下只表现为包住整页的全屏容器，布局树里读不到侧栏自身的宽度、遮罩透明度或转场数值。因此这里的时长与遮罩沿用其它浮层的同一套取值，不声明与原厂逐像素对应。

## Demo 示例

起始边与结束边的开关示例见 [DemoContainerSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoContainerSamples.kt)，库入口见 [SidePane](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/sidepane/SidePane.kt)。

## 与原厂的差异

面板宽度与停靠判定按原厂公式（抽屉 300dp、停靠 `min(280dp, 宽 × 0.382)`）；`DrawerLayout` 的边缘拖动、返回键与预测性返回未复刻。
