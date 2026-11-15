# 按钮条

`ButtonBar` 承载一到三个同级操作，几何与横竖排规则按 ColorOS 17 的 `COUIButtonBarLayout` 实现。弹窗的操作区是它的一个消费者（`Dialog` 内部复用同一套布局），也可以单独放进页面或自定义容器。

## 最小调用

~~~kotlin
ButtonBar(
    items = listOf(
        CouiButtonBarItem(text = cancelLabel, onClick = onCancel),
        CouiButtonBarItem(text = confirmLabel, onClick = onConfirm, style = CouiButtonStyle.Primary),
    ),
)
~~~

`items` 的顺序就是布局顺序，横排与竖排都不重排。最少一个、最多 `CouiButtonBarDefaults.maxItems`（3）个，越界抛出异常。

## 横排与竖排

只有恰好两个按钮时才比较文案宽度：先把父宽（上限 `coui_dialog_max_width` 392dp）均分，再减去按钮左右内留白 12dp（`coui_alert_dialog_button_horizontal_padding`）以及父容器的 16dp / 4dp 留白（`coui_alert_dialog_button_parent_padding_horizontal` / `_inside`），任一文案超出就整体转竖排。一个或三个按钮恒竖排，与 `COUIButtonBarLayout.onMeasure` 的 `getButtonCount() != 2 || needSetButVertical(...)` 一致。

- 横排：按钮均分父宽，首项左侧 16dp、末项右侧 16dp，相邻按钮之间各 4dp，上下留白 4dp 与 16dp。
- 竖排：按钮占满父宽，左右 16dp，相邻按钮各留 4dp，末项下沿 16dp。

按钮高度由文案行数决定：单行 44dp（`coui_alert_dialog_button_height`），双行 56dp（`coui_alert_dialog_button_min_height_multiline`），最多两行并以省略号结束。按钮条内容超过容器可用高度时自己滚动，不会把上方的正文挤出窗口。

## 按钮外观

按钮条只负责布局与尺寸，外观由 `CouiButtonStyle` 与 `colors` 决定，默认按当前色板取主、次操作配色：

~~~kotlin
CouiButtonBarItem(text = resetLabel, onClick = onReset, style = CouiButtonStyle.Secondary)
CouiButtonBarItem(text = confirmLabel, onClick = onConfirm, style = CouiButtonStyle.Primary)
~~~

禁用只影响对应按钮，不改变排布。`CouiButtonBarDefaults` 提供全部尺寸常量，需要贴合其他容器时可读取同名数值。

## 与弹窗操作区的差异

`Dialog` 复用同一套布局函数，但保留项目既有特化：

- 横排顺序为「取消、中性、确认」，竖排顺序为「中性、确认、取消」；原厂 `COUIButtonBarLayout` 的 DOM 顺序是「中性、确认、取消」，两种朝向相同。
- 弹窗操作按钮使用弹窗自身的表面与轮廓（`CouiDialogActionColors`、材质分支），不是 `Button`。
- 原厂按下的按钮会由 `getChildDrawingOrder` 提到最上层绘制，这里未复刻；按压缩放不会覆盖相邻按钮。
- 原厂推荐按钮（`setRecommendButtonId`）与 AI Payment 专用文本色不在范围内。
- `UIUtil.setViewClipExpandSize` 的裁剪扩展是厂商私有实现，未复刻。

## Demo 示例

两个短操作与长文案竖排两种排布见 [DemoButtonSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoButtonSamples.kt) 的 `ButtonBarSample`，实现见 [ButtonBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/buttonBar/ButtonBar.kt)。
