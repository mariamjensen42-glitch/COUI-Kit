# 🔴角标与气泡

`Badge` 在图标或文字上标注未读数量，`Tooltip` 在长按或悬停后解释一个控件的含义。

## 角标

`count` 为 `null` 显示红点，大于 0 显示数字，小于等于 0 时不占位：

~~~kotlin
Badge(count = unread)
Badge()                    // 红点
~~~

几何与配色取自原厂 `Widget.COUI.COUIHintRedDot`：红点直径 6dp，数字高度 16dp，宽度按位数取 16 / 20 / 26dp，字号 10dp（原厂以 dp 声明，不随系统字号缩放），容器 `coui_color_additional_red`（日间 `#EB3B2F`、夜间 `#EB493D`），内容白色。叠在内容右上角时，红点离角落 1dp（`coui_hint_red_dot_small_reddot_topend_margin`），数字角标离角落 6dp（`coui_hint_red_dot_small_number_topend_margin_rectangle`），分别对应 `CouiBadgeDefaults.dotOffset` 与 `numberOffset`。

超过 `maxCount`（默认 99）显示为 `99+`，宽度按三位数字取最宽档。颜色可显式覆盖：

~~~kotlin
Badge(count = unread, color = CouiTheme.colorScheme.accent, contentColor = Color.White)
~~~

角标只表达状态，不处理点击。需要点击时把交互放在外层，例如导航栏项目或 FAB 本身。

## 叠在内容上

`BadgedBox` 把角标放在内容右上角，内容尺寸不因角标改变，角标可以超出内容边界：

~~~kotlin
BadgedBox(badge = { Badge(count = unread) }) {
    Icon(CouiIcons.Filled.Notifications, contentDescription = null, modifier = Modifier.size(24.dp))
}
~~~

RTL 下角标跟随起始侧翻转。内容的点击、语义和最小触摸区域仍由内容自己提供。

## 气泡提示

`Tooltip` 的可见性由宿主控制，组件不安装手势：

~~~kotlin
var tipVisible by remember { mutableStateOf(false) }

Tooltip(
    visible = tipVisible,
    onDismissRequest = { tipVisible = false },
    text = tipText,
) {
    Icon(
        imageVector = CouiIcons.Filled.Info,
        contentDescription = null,
        modifier = Modifier
            .size(24.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { tipVisible = true })
            },
    )
}
~~~

组件不安装长按、悬停或点击手势，避免抢占子节点的输入；触发方式由宿主决定，长按、悬停或点击都可以。气泡在 anchor 上方水平居中弹出，上方空间不足时翻到下方，两侧夹紧在窗口内。

气泡不获取焦点，也不改变页面的焦点顺序；点击气泡外部或按返回键会回调 `onDismissRequest`。宿主必须在回调里把 `visible` 置为 `false`，否则气泡不会关闭。

## 配色

角标和气泡的颜色都由各自 Defaults 提供：

~~~kotlin
Badge(count = unread, color = accent, contentColor = onAccent)
Tooltip(visible = tipVisible, onDismissRequest = { tipVisible = false },
    text = tipText, colors = CouiTooltipDefaults.colors(CouiTheme.colorScheme.accent)) { anchor() }
~~~

气泡的容器取原厂 `?couiColorTips`（日间 `#0080FF`、夜间 `#1A8CFF`），内容为白色，四边留白 13dp、圆角 12dp（`couiRoundCornerM`），均来自 `COUIToolTips` 默认分支；定位与夹紧规则由本库实现。

角标与气泡的容器、尺寸与留白均沿用原厂 `COUIHintRedDot` 与 `COUIToolTips`；`BadgedBox` 的叠放位置、气泡定位与夹紧规则由本库实现。

## Demo 示例

角标、叠放和气泡示例见 [DemoBadgeSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoBadgeSamples.kt)，库入口见 [Badge / BadgedBox](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/badge/Badge.kt) 与 [Tooltip](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/tooltip/Tooltip.kt)。
