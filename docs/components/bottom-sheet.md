# 🪟底部面板

`BottomSheet` 从屏幕底部升起，承载一组与当前页面相关的操作或内容。它只提供面板容器，标题、列表、按钮等全部由调用方在内容槽中装配。

## 最小调用

页面持有显隐状态，组件只通过回调请求关闭：

~~~kotlin
var visible by remember { mutableStateOf(false) }

BottomSheet(
    visible = visible,
    onDismissRequest = { visible = false },
) {
    Item(onClick = { visible = false }) { Text(text = "删除记录", kind = CouiTextKind.ItemTitle) }
}
~~~

`visible` 是受控状态，组件不会自行改为 `false`。点击遮罩、按返回键、向下拖拽关闭都只回调 `onDismissRequest`，是否关闭由页面决定。退场动画期间组件仍保留窗口，因此调用点要一直留在组合中，不要用 `if (visible)` 包住 `BottomSheet`，否则面板会立即消失、失去退场动画。

`dark` 和 `colors` 默认读取局部 `CouiTheme`；没有主题时使用原生预设。只覆盖其中一项时，其余配色仍取默认值：

~~~kotlin
BottomSheet(
    visible = visible,
    onDismissRequest = { visible = false },
    colors = CouiBottomSheetDefaults.colors(dark).copy(handle = Color.Transparent),
)
~~~

## 面板与内容

面板只有顶部两角是圆角，四周离屏幕边缘 8dp（`coui_bottom_sheet_dialog_follow_hand_margin_left` / `_right` / `_bottom`），像一张浮起的卡片而不是贴满屏幕；内容按 `ColumnScope` 纵向排列，组件不附加任何内边距，留白由内容自己给出。

内容高度超过面板上限时在面板内滚动，上限取「屏幕高度减去顶部 40dp 与底部留白」与 `coui_panel_max_height`（2000dp）的较小值：手势导航留 64dp，三键导航在导航栏之上只再留 8dp。键盘弹出时面板底部按 IME 与导航栏的并集避让，因此输入区不会被键盘遮挡。

面板顶部有一条 4dp 高、36dp 宽的拖拽指示条，同时是拖拽的抓手；条顶距面板边 12dp（`coui_panel_drag_view_shadow_margin_top` 4dp + `coui_panel_drag_view_padding_vertical` 8dp）。

## 拖拽与滚动

面板可以上下跟手拖动，位移直接跟随手指；向下拖动时遮罩随面板露出比例变淡。向上不能超过展开位。

松手时按位移与速度结算：向上拖动或向上甩动回到展开位；向下拖过面板一半，或向下速度超过 500px/s 且大于横向速度，回调 `onDismissRequest`，由页面把 `visible` 置 `false` 后播放退场动画。位移在退场动画期间保留，面板从手指离开的位置继续下落，不会先跳回展开位。

内容可滚动时，先滚动内容；内容已经在顶部还继续下拉，才拖动面板。面板已被下拉时，向上滚动先把它送回展开位，再滚动内容。这套顺序与原厂 `COUIBottomSheetBehavior.onNestedPreScroll` 一致。

宿主必须响应 `onDismissRequest`：组件不会自行隐藏，被拖下的面板会停在手指离开的位置。

## 遮罩与返回键

窗口不使用系统压暗，也不使用系统窗口动画：遮罩由组件自己绘制，强度取 `coui_color_mask`（日间 `#33000000`、夜间 `#99000000`），并与滑入同步淡入淡出。

遮罩只在面板以外的区域响应点击；面板空白处的点击会被面板自己吞掉，不会误触发关闭。返回键通过 `Dialog` 的 `onDismissRequest` 走同一个回调。

## 与 Dialog 的区别

| | `BottomSheet` | `Dialog` |
| --- | --- | --- |
| 内容协议 | 只有内容槽，全部由调用方装配 | 标题、正文、最多三个操作按钮 |
| 位置 | 固定在底部，四周离边缘 8dp | 底部或居中 |
| 底部圆角 | 无，底部留 8dp | 四角圆角并带底部留白 |
| 背景材质 | 不支持 | 可选模糊材质（需要 API 33 与窗口背景源） |
| 输入 | 不提供输入协议 | `InputDialog` 提供单行输入 |

需要确认文案与按钮排布时用 `Dialog`；需要承载自定义列表或较长内容时用 `BottomSheet`。

## 动画与资源

入场 250ms、退场 250ms，插值器分别取原厂 `anim/android_alert_dialog_enter` 的 `pathInterpolator(0.3, 0, 0.1, 1)` 与 `anim/android_alert_dialog_exit` 的 `pathInterpolator(0.3, 0, 1, 1)`。面板位移按自身高度计算，遮罩透明度与位移共用同一个进度值；拖拽位移在此基础上叠加，遮罩再乘面板的露出比例。

进度值、拖拽位移与遮罩比例只在绘制阶段读取，动画与拖动不产生逐帧重组；退场结束后窗口才离开组合，中途反向会从当前进度继续。松手回弹使用无回弹弹簧（阻尼比 1、刚度 400），对应原厂 `startSettleRunnable` 的 spring force 结算。

## Demo 示例

底部面板示例见 [DemoBottomSheetSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoBottomSheetSamples.kt)，库入口见 [BottomSheet](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/bottomsheet/BottomSheet.kt)。
