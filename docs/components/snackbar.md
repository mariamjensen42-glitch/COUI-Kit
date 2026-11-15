# 📢Snackbar

`SnackbarHost` 在页面底部显示一条短暂反馈，可带一个操作按钮；同一时刻只显示一条，触摸会暂停自动消失计时。

## 最小调用

状态由页面持有，触发处直接调用 `show`：

~~~kotlin
val snackbar = rememberCouiSnackbarState()

Box(Modifier.fillMaxSize()) {
    content()
    SnackbarHost(
        state = snackbar,
        modifier = Modifier.align(Alignment.BottomCenter),
        onDismissed = { pending?.let { snackbar.show(it) } },
    )
}

// 任意回调中
snackbar.show(message)
~~~

`pending` 与 `content()` 由页面提供。`show` 不是挂起函数，可以直接在点击回调里调用；`dark` 默认跟随 `LocalCouiDarkTheme` 或系统，配色规则见[外观与预设](../guide/appearance.md)。

## 单实例与排队

原厂 `COUISnackBar.make()` 在父容器已有可见 Snackbar 时不再挂载新实例；这里保持同一语义：已有提示未退场时 `show` 返回 `false`，不替换当前内容，也不排队。宿主需要连续提示时，在 `onDismissed` 里提交下一条，队列仍由宿主持有：

~~~kotlin
if (!snackbar.show(message)) pending.add(message)
~~~

`onShown` 在新提示进入时回调，`onDismissed` 在退场动画收尾后回调。`dismiss()` 手动进入退场，`dismiss(withoutAnimation = true)` 立即移除。退场期间重复调用不会叠加动画。

这与[轻提示](toast.md)的 `ToastHost` 不同：后者维护最多三条的可见队列，适合多条结果反馈；Snackbar 保持单实例互斥，适合"当前只有一件事需要回应"的场景。两者共用同一套容器几何。

## 时长与触摸

~~~kotlin
snackbar.show(message, durationMillis = CouiSnackbarDefaults.longMillis)
snackbar.show(message, durationMillis = CouiSnackbarDefaults.indefiniteMillis,
    actionLabel = actionLabel, onAction = { retry() })
~~~

`durationMillis` 是任意非负毫秒数，`0`（`indefiniteMillis`）表示不自动消失。按下或移动时暂停计时，抬手或手势取消后按**完整时长**重新计时，不续剩余时长。操作按钮与 `onAction` 同时提供时显示；点击后先回调，再立即退场，重复点击只提交一次。

## 外观与装配

容器几何、文字、操作按钮、图标与配色复用轻提示的 `coui_snack_bar_*` 绘制节点，见[轻提示](toast.md#配色与文案)；`colors` 的类型也是 `CouiToastColors`。差异在状态与动画：

- 入场/退场用 `COUISpringAnimation` 的 response 0.3 / 0.25、bounce 0，进度映射到容器 0.8→1 缩放与 0→1 透明度。
- 底部留白 `coui_snack_bar_margin_bottom`（32dp）由组件自身承担，宿主只需把 `SnackbarHost` 放在 `Box` 的 `BottomCenter`。

~~~kotlin
snackbar.show(
    message = message,
    actionLabel = actionLabel,
    onAction = onAction,
    icon = { Icon(painter, contentDescription = null) },
)
~~~

组件自身按内容测量、不占满父级；提示本体不拦截页面输入，覆盖层上的触摸只用于暂停计时，操作按钮仍可点击。每条提示带礼貌级实时区域，操作按钮带按钮角色。

## 与原厂的差异

- 原厂自己沿 View 树找 `CoordinatorLayout` / `FrameLayout` 并 `addView`；Compose 里没有等价的公开父容器查找，宿主用 `Box` 决定位置，底部留白由组件承担。
- 原厂 `COUINotificationSnackBar` 的标题/副标题/关闭按钮与纵向拖动消失、以及 `COUICustomSnackBar` 的 `setTouchSlidable` 分支没有移植。
- 原厂九宫格背景与烘焙阴影在[轻提示](toast.md#与原厂-snackbar-的对应)已按平台投影还原，本组件沿用同一绘制节点，不另复制一份。
- 原厂 `setSnackBarProgress` 把弹簧进度放大到 10000 后积分；这里归一化到 0..1，response、bounce 与缩放/透明度端点一致。

## Demo 示例

单条、带操作按钮、不自动消失与排队见 [DemoNextComponents](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoNextComponents.kt)，库入口见 [SnackbarHost / CouiSnackbarState](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/snackbar/Snackbar.kt) 与 [Defaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/snackbar/CouiSnackbarDefaults.kt)。
