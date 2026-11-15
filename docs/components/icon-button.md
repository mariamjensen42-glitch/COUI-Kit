# ⭕️图标按钮

`IconButton` 是只有图标的按钮：圆形命中区、跟手光与按压蒙层复用 `Button` 的同一条反馈链，与文字按钮观感一致。

它只承载单次操作，不表达选中状态；需要切换外观请用 `Switch`、`Checkbox` 或 `ChipTab`。

原厂没有独立的图标按钮控件类，界面里的图标按钮一律是 `android.widget.ImageButton` 加自定义背景：最近任务头部的「更多」测量为 147×147px（@560dpi 约 42dp），控制中心磁贴的 `icon_frame` 为 217×217px 而内部图标为 98×98px。因此尺寸按布局树里的命中区取值，并与顶栏按钮共用 40dp 一档。

## 最小调用

~~~kotlin
IconButton(onClick = onMore) {
    Icon(CouiIcons.Filled.More, contentDescription = moreDescription)
}
~~~

`onClick` 是必填参数；图标与 `contentDescription` 都由调用方提供，库不做 tint，也不内置任何图标。

## 尺寸

~~~kotlin
IconButton(onClick = onClick) { ... }                                   // 40dp，与顶栏按钮一致
IconButton(onClick = onClick, size = CouiIconButtonDefaults.smallSize) { ... }  // 紧凑，行尾与标签内
IconButton(onClick = onClick, size = CouiIconButtonDefaults.largeSize) { ... }  // 强调，页面主操作
~~~

`size` 只决定绘制与形变的范围，命中区由主题的最小交互尺寸兜底：传 32dp 时仍按 48dp 响应点击，`IconButton` 不会因为视觉变小而变难点。

## 外观

~~~kotlin
IconButton(
    onClick = onCreate,
    containerColor = CouiTheme.colorScheme.accent,
    contentColor = CouiTheme.colorScheme.onAccent,
) {
    Icon(CouiIcons.Filled.Add, contentDescription = null)
}
~~~

- `containerColor` 默认透明；需要底色按钮时传入强调色或 `container`。
- `contentColor` 默认取当前外观的 `onSurface`，槽内内容继承该内容色；子内容的显式 `color` 与 `tint` 仍然优先。
- `shape` 默认 `CouiTheme.shapes.circle`；放进紧凑行内需要胶囊时传 `CouiTheme.shapes.capsule`。
- `enabled = false` 时内容色乘 0.38（与顶栏按钮、浮动操作栏、菜单同档），同时不响应点击、不产生蒙层与形变。

## 交互链

- 触摸形变与位移：`couiTouchMotion`
- 按压缩放：`couiPressMotion`
- 跟手光：`rememberCouiSpotlight`，与次要按钮共用 `OpaqueMedium2`，不额外提亮
- hover / focus / press 蒙层：`couiButtonFeedback`，不带列表行的最低按压进度

该效果要求 API 33，低版本返回空实现，形变与蒙层仍然生效。手势被父容器接管、取消或组件禁用时，形变、跟手光与蒙层各自收尾，不提交点击。

按钮不使用背景采样与材质模糊，因此不要求 `CouiBackdrop`，也不要求 API 34；需要玻璃材质时用 `Fab` 或 `TopAppBarButton`。

## Demo 示例

三档尺寸、带底色与禁用示例见 [DemoContainerSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoContainerSamples.kt)，库入口见 [IconButton](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/button/IconButton.kt)。
