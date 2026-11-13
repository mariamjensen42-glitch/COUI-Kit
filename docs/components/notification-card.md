# 🔔通知卡

`NotificationCard` 是一条通知的排版：图标、标题、相对时间与正文，对应 ColorOS 17 通知中心的单条通知。

组件**只做布局，不自带材质**。默认用页面卡片色填充；需要通知中心那种凝光玻璃时，把 `containerColor` 设为 `Color.Transparent`，再用 `Modifier.couiFloatingSurface` 包裹并传入同一个 `shape`，模糊、材质混色和轮廓光全部走既有的浮动表面链路。

## 最小调用

~~~kotlin
NotificationCard(
    title = "USB 调试已打开",
    body = "点击可设置停用 USB 调试",
    time = "刚刚",
    icon = { Icon(CouiIcons.Filled.Bell, null, Modifier.size(24.dp)) },
)
~~~

接入玻璃材质时不复制任何材质代码，把卡片包进 `Modifier.couiFloatingSurface`：

~~~kotlin
NotificationCard(
    modifier = Modifier.couiFloatingSurface(
        backdrop = backdrop,
        renderer = renderer,          // CouiFloatingRenderer，单个绘制节点独占
        material = material,          // CouiFloatingMaterial，取值参考 FloatingToolbar 的预设
        shadow = shadow,              // CouiFloatingShadow
        shape = CouiNotificationCardDefaults.shape,
        contentLayer = layer,         // GraphicsLayer，与 renderer 同节点
        contourGlowEnabled = true,
        surface = { surface },        // CouiFloatingSurface，几何由宿主按卡片尺寸构造
    ),
    title = "USB 调试已打开",
    body = "点击可设置停用 USB 调试",
    containerColor = Color.Transparent,
)
~~~

各参数的完整含义见[浮动表面](../effects/floating-surfaces.md)，具体装配参照 [FloatingToolbar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/floatingtoolbar/FloatingToolbar.kt)。这条链路需要 API 34 与页面背景采样源，低版本没有替代实现；`shape` 必须与 `couiFloatingSurface` 的 `shape` 一致，否则描边和裁剪会错位。

## 参数

- `title` / `body`：必填，标题 16sp `ItemTitle`，正文 13sp `ItemSummary`。
- `time`：可选，相对时间，12sp `Body`，与标题同一行、靠右。
- `icon`：可选，24dp 槽位，库不做 tint。
- `containerColor`：卡片底色，默认 `colors.card`；接材质时传 `Color.Transparent`。
- `shape`：轮廓，默认 `CouiNotificationCardDefaults.shape`；与 `couiFloatingSurface` 的 `shape` 必须一致，否则描边和裁剪会错位。
- `bodyMaxLines`：正文最大行数，默认 2。

## 尺寸

取自真机布局树（一加 Ace 5，1264×2780 @560dpi，3.5px/dp）：

| 项 | 值 |
| --- | --- |
| 卡片宽度 | 329dp（页面左右各 16dp 边距） |
| 单条最小高度 | 60dp（实测 211px ≈ 60.3dp） |
| 内留白 | 16dp |
| 图标 | 24dp |
| 图标到文字 | 18dp（内容左起点 58dp = 16 + 24 + 18） |
| 标题到正文 | 2dp |

标题与时间的字号按布局树行高反推：标题 66px ≈ 18.9dp 对应 16sp，时间 49px ≈ 14dp 对应 12sp。正文行高受两行排版影响，取 13sp 与[操作卡](action-card.md)的副标题一致，尚未用原厂资源确认。

源码见 [NotificationCard](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/notificationcard/NotificationCard.kt)。
