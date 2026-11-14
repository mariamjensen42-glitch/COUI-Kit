# 💬轻提示

`ToastHost` 在页面之上短暂显示一条提示，不需要用户操作，也不打断当前流程。

## 最小调用

状态由页面持有，触发处直接调用 `show`：

~~~kotlin
val toast = rememberToastHostState()

Box(Modifier.fillMaxSize()) {
    content()
    ToastHost(toast, Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp))
}

// 任意回调中
toast.show(message)
~~~

`show` 不是挂起函数，可以直接在点击回调里调用。位置由宿主决定：`ToastHost` 只按内容测量自身，不占满父级，也不安装点击或手势，因此覆盖在内容上不会拦截触摸；只有操作按钮区域接收点击。

## 停留时长

~~~kotlin
toast.show(message)                                // 短提示，2 秒
toast.show(message, CouiToastDuration.Long)        // 长提示，3.5 秒
~~~

时长从进入动画开始计算，退出动画另计。计时在 `ToastHost` 内完成，因此它必须保持组合：页面销毁时未消费的提示随之丢弃，不会在返回后补显。

## 操作按钮

~~~kotlin
toast.show(
    message = "已删除会话",
    actionLabel = "撤销",
    onAction = { restore() },
)
~~~

`actionLabel` 与 `onAction` 同时提供时才显示按钮。点击后先回调 `onAction`，再立即进入退出动画，不等剩余停留时间；进入、停留、退出与出队仍在同一副作用内顺序推进，多次点击只生效一次。

按钮文案 14sp、与文案间距 16dp、最大宽 110dp、到容器右缘 4dp，最多两行，超出以省略号结尾；单行时与文案同行居中，两段文字同为 14sp，基线自然对齐，多行时整体垂直居中。

## 图标

~~~kotlin
toast.show(
    message = "已复制到剪贴板",
    icon = {
        Image(
            painter = rememberVectorPainter(CouiIcons.Filled.Bell),
            contentDescription = null,
            colorFilter = ColorFilter.tint(CouiTheme.colorScheme.onSurface),
        )
    },
)
~~~

图标槽是 `CouiToastDefaults.iconSize`（30dp）的方框，内容在其中居中裁剪，到容器左缘 16dp，上下各 10dp；文案起点相应右移到图标之后 16dp。槽位只负责尺寸与位置，尺寸、颜色与无障碍描述由传入内容决定，颜色建议取 `CouiTheme.colorScheme.onSurface`。图标内容建议填满槽位，否则小图标会在 30dp 方框内留出过多空白。

原厂 `coui_snack_bar_icon_margin_top_horizontal` 的上下留白是 16dp，会把带图标的单行容器撑到 62dp；单行文案本身只有约 15dp 高，沿用后上下出现大片空白。这里收到与文案垂直留白一致的 10dp，容器高度随之降到 50dp，图标与文案仍保持垂直居中。这是本库与九宫格的已知差异。

## 多行

文案按 `CouiToastDefaults.maxWidth` 换行，不截断也不压缩高度，多行时按原厂的 `textAlignment="viewStart"` 左对齐。单行时上下留白 10dp；实际排版超过一行时改为上 12dp、下 8dp，操作按钮同步改为上 8dp、下 9dp。

## 堆叠

同时最多显示 `CouiToastDefaults.maxVisible`（3）条，超出时移除最早的一条，按加入顺序纵向排列，间距为 `CouiToastDefaults.spacing`。

~~~kotlin
repeat(3) { index -> toast.show(queuedMessage(index)) }
~~~

需要立刻清空时调用 `toast.dismissAll()`，它不等待退出动画。

## 配色与文案

COUI 没有独立的 Toast 组件，短暂的底部反馈由 SnackBar 承担，因此轻提示的几何与配色取自 `coui_snack_bar_*`：容器日间白色、夜间 `#242424`，圆角 `coui_snack_bar_background_radius` 12.67dp（等半径标准圆角，背景是九宫格图片而非超椭圆），内留白水平 16dp、单行垂直 10dp，文字取 `coui_snack_bar_item.xml` 的 `couiTextAppearanceButton` 14sp（随系统字号缩放），内容取当前前景色，操作按钮取强调色（原厂为 `?couiColorPrimaryTextOnPopup`）。九宫格内部是纯色填充，内缘没有额外暗部，容器只画平涂底色。

~~~kotlin
ToastHost(toast, dark = dark)
~~~

原厂背景是九宫格图片，阴影烘焙在图片外缘，没有 elevation 资源。九宫格在 xxhdpi 下外扩约 14dp（上）、21dp（左右）、26dp（下），峰值不透明度约 5%，`coui_snack_bar_shadow_size` 26dp 是它的总跨度。平台投影把模糊跨度与不透明度绑在同一个 elevation 上，这里用 26dp 跨度配 5% 黑色还原同一组观感参数，见 `CouiToastDefaults.shadowElevation` 与 `shadowColor`。需要与页面风格统一时可以显式传入 `CouiToastColors`。文案由宿主提供。

无障碍方面，每条提示标注为礼貌级实时区域，屏幕阅读器在空闲时朗读，不会打断正在朗读的内容；操作按钮带按钮角色，可单独聚焦。

## 与原厂 SnackBar 的对应

排布取自 `layout/coui_snack_bar_item.xml`，取值来自同名资源：

| 分支 | 资源 |
| --- | --- |
| 容器 | `coui_snack_bar_background`、`coui_snack_bar_background_radius` 12.67dp |
| 文案 | `coui_snack_bar_child_margin_horizontal_start` / `_end` 16dp、`_vertical` 10dp、`_vertical_multi_lines` 12dp、`_vertical_multi_lines_bottom` 8dp、`couiTextAppearanceButton` 14sp |
| 操作按钮 | `coui_snack_bar_action_max_width` 110dp、`_margin_horizontal_end` 4dp、`_margin_vertical` 10dp、`_margin_top_multi_lines` 8dp、`_margin_bottom_multi_lines` 9dp、`_text_max_lines` 2；与文案的间距由文案的 `child_margin_horizontal_end` 16dp 承担 |
| 图标 | `coui_snack_bar_icon_width` / `_height` 30dp、`coui_snack_bar_context_margin_start_with_icon` 16dp、`coui_snack_bar_icon_margin_top_horizontal` 16dp（本库收到 10dp） |
| 操作按钮配色 | `coui_snack_bar_action_text_selector` → `?couiColorPrimaryTextOnPopup` |

原厂其余分支不在本组件范围内：

- 通知样式 SnackBar（`coui_notification_snack_bar_*`）：44dp 方形图片图标、标题与副标题、关闭按钮和 COUIButton，属于跨应用通知横幅，由系统通知承载。
- 悬浮分支（`coui_snack_bar_float_*`）：52×28dp 悬浮操作、40dp 关闭按钮、36dp 圆形图标，属于独立浮层宿主。
- 操作按钮禁用态（`coui_snack_bar_action_text_disable` `#26FFFFFF`）：本库不提供禁用状态，需要禁用时应改为不传 `onAction`。
- 九宫格背景（`coui_snack_bar_background.9.png`）：改用平台投影，原因见上节。

## 与弹窗的区别

轻提示不获取焦点、不阻挡输入，适合「已保存」「已复制」这类结果反馈；需要用户确认或输入时使用[弹窗](dialog.md)。操作按钮只承接一次性的即时动作，需要表单或多选项时仍应使用弹窗。

## Demo 示例

单条、堆叠、操作按钮、图标与清空示例见 [DemoToastSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoToastSamples.kt)，库入口见 [ToastHost / ToastHostState](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/toast/Toast.kt)。
