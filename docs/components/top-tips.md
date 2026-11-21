# 📢顶部提示条

`TopTips` 是页面顶部的提示条容器，对应原厂 `COUICustomTopTips`。半透明卡片背景，显示与消失各 250ms，只做纵向缩放。

## 最小调用

显示状态由页面持有：

~~~kotlin
var visible by rememberSaveable { mutableStateOf(false) }

TopTips(visible = visible) {
    TopTipsContent(content = "系统更新已完成")
}
~~~

组件位于 `com.cycling.coui.toptips`。

## 内容排布

`TopTipsContent` 按原厂 `coui_default_toptips.xml` 的顺序装配：图标、可选标题、跑马灯正文、可选操作按钮与关闭按钮。

~~~kotlin
TopTips(visible = visible) {
    TopTipsContent(
        icon = {
            Image(
                painter = rememberVectorPainter(CouiIcons.Filled.Bell),
                contentDescription = null,
                colorFilter = ColorFilter.tint(CouiTheme.colorScheme.onSurface),
            )
        },
        title = "系统更新",
        content = "本次更新包含安全补丁与稳定性优化，重启后生效。",
        action = {
            Button(onClick = { restart() }, label = "立即重启", style = CouiButtonStyle.Text)
        },
        close = {
            IconButton(onClick = { visible = false }) { /* 关闭图标 */ }
        },
    )
}
~~~

图标与关闭按钮的槽位固定为 24dp；标题最多两行并按尾部省略；正文使用[跑马灯文本](marquee-text.md)，超宽时自动滚动。

## 外观

- 圆角默认使用 `CouiTheme.shapes.medium`（`coui_round_corner_m`，12dp 超椭圆，`cornerWeight = 2.5`）。原厂在支持平滑圆角的版本取 `couiRoundCornerMRadius = 9dp` / `couiRoundCornerMWeight = 0.99`，不支持时回退到 12dp 普通圆角；`0.99` 低于库内 `CouiShape` 的 `cornerWeight >= 2` 下限，现有轮廓 token 无法表达，两者轮廓不一致，待真机对照。需要普通圆角可显式传入 `RoundedCornerShape(9.dp)` 或 `shape`。
- 底色取 `couiColorFillThin`，日间 `#0A000000`、夜间 `#14FFFFFF`，默认由 `CouiTopTipsDefaults.containerColor(dark)` 给出；覆盖主题外观时传 `dark`。
- 显示与消失动画各 250ms，只改 `scaleY`，不改 alpha；收起后不占位，也不拦截触摸。原厂用 `ObjectAnimator` 默认插值器，这里用 `tween` 的 `FastOutSlowInEase`，曲线不同。

## 与原厂的差异

原厂的 `COUICustomTopTips` 是 `View` 容器，内容来自子类覆写的布局 id；这里用内容插槽表达同一职责。动画时长、只改 `scaleY` 与容器底色一致，但轮廓、动画插值器不一致，且原厂命令式 `showWithAnim/dismissWithAnim` 的回调、`setContentView` 重复调用保护与 `COUIDefaultTopTips` 的双按钮横竖排约束、AI 支付样式、300ms 宽度变化动画都不在本实现范围内。

## Demo

完整示例见 [DemoSystemWidgets.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoSystemWidgets.kt)。
