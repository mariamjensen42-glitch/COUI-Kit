# 👤用户关注顶栏

用户主页顶栏：圆形头像、主副标题与关注按钮。副标题可省略，关注态在按钮底色、内容色和字号之间过渡。

## 最小调用

```kotlin
var following by remember { mutableStateOf(false) }

UserFollowBar(
    title = "Alex Mercer",
    subtitle = "2.4k followers",
    following = following,
    onFollowingChange = { following = it },
    modifier = Modifier.fillMaxWidth(),
)
```

`following` 与 `onFollowingChange` 由宿主持有；组件只在点击按钮时回调新值，不自行改状态。

## 参数

| 参数 | 说明 |
| --- | --- |
| `title` | 主标题，单行，超出按 `TextOverflow.Ellipsis` 截断 |
| `subtitle` | 副标题；`null` 或空串时不显示，主标题整体垂直居中 |
| `following` | 关注态，决定按钮文案、配色与字号 |
| `onFollowingChange` | 点击按钮时回调取反后的值 |
| `fill` | `true` 时按钮贴到末端，标题与副标题占满中间；`false` 时按钮紧跟文字块 |
| `avatar` | 头像内容，只渲染一个布局节点，尺寸固定为 24dp；默认是空白的圆形占位 |
| `followLabel` / `followingLabel` | 两种状态的按钮文案，默认取库内字符串资源 |
| `enabled` | 关闭后整体 30% 透明且按钮不可点 |
| `colors` / `metrics` | 配色与度量，默认来自 `CouiUserFollowDefaults` |

## 尺寸与配色

| 项 | 值 |
| --- | --- |
| 顶栏最小高度 | 52dp |
| 左右内边距 | 8dp |
| 头像 | 24dp 圆形，描边 1dp |
| 头像与文字、文字与按钮间距 | 8dp |
| 按钮 | 52dp × 28dp 胶囊，内边距 0 |
| 主标题 | 18sp medium，`onSurface` |
| 副标题 | 13sp 常规，`onSurfaceVariant` |
| 未关注按钮 | 强调色底 + `onAccent` 文字，14sp |
| 已关注按钮 | 强调色 15%（深色 30%）底 + 强调色文字，12sp |

`subtitle` 省略时副标题节点不参与布局，主标题与头像按内容高度垂直居中。

## 关注态过渡

`following` 变化时，按钮底色、内容色、字号与按钮位置都在 `CouiUserFollowDefaults.transitionDurationMillis`（300ms）内插值过渡，缓动曲线为 `CouiUserFollowDefaults.transitionEasing`。`fill` 变化同样参与这段过渡，因此切换时按钮会平滑滑到末端。

## 与原厂的差异

1. 原厂 `COUIUserFollowView` 是 `MotionLayout`，运行时用 `generateViewId()` 动态拼一个 `MotionScene` 并靠 `setCustomAttribute("Text")` / `"TextSize"` / `"TextColor"` 驱动过渡；这里改为三条 `animateFloatAsState` 分别插值关注度、填充度与副标题可见度，没有 MotionScene 的路径插值。
2. 原厂 `COUIUserInfoToolbar` 把 user follow view 作为 `COUICustomToolbar` 的 customView（match_parent、初始 `GONE`）并转发全部接口；这里只提供独立组件，顶栏容器由宿主选择，`Toolbar` 封装未移植。
3. 原厂头像固定为 `COUIRoundImageView` + `BitmapDrawable` 默认占位，`setHasBorder(true)` 时画外圈；这里用圆形容器 + 1dp 边框，头像内容交给宿主，`setImage(Bitmap)` 与 `release()` 的位图回收无对应。
4. 原厂副标题的显示/隐藏靠 `Barrier` 重新连接约束（`connect(SUB_TEXT, END, TEXT, END)`）；这里用副标题高度乘可见度插值，关闭时副标题不参与布局，标题末端对齐略有差异。
5. 原厂 `setAnimate(false)` 时直接写约束集跳变；这里所有状态变化都走 300ms 过渡。

Demo：`DemoUserFollowSamples.kt` 中的 User follow bar 页面。源码见 [UserFollowBar](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/topappbar/UserFollowBar.kt)、[CouiUserFollowDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/topappbar/CouiUserFollowDefaults.kt)。
