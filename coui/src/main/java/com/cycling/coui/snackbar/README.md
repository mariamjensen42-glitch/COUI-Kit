# Snackbar

## Overview
COUI 底部提示条组件包：SnackbarHost 渲染 CouiSnackbarState.current 的单实例，支持操作回调、超时、按压暂停与弹簧入场/退场；内容复用 toast 包的 internal CouiToastSurface 与 CouiToastDefaults，32dp 底部外边距由组件自身承担。

## Interface
- `public @Immutable data class CouiSnackbarData` - id/message/durationMillis/actionLabel/onAction/icon，show 时快照 `[Snackbar.kt:CouiSnackbarData]`
- `public @Stable class CouiSnackbarState`（internal 构造） - current 只读，internal dismissing/withoutAnimation `[Snackbar.kt:CouiSnackbarState]`
- `public fun CouiSnackbarState.show(...)` / `dismiss(withoutAnimation = false)` / `internal clear(id)` - 提交、退场与按 id 清理 `[Snackbar.kt:CouiSnackbarState.show]` `[Snackbar.kt:CouiSnackbarState.dismiss]` `[Snackbar.kt:CouiSnackbarState.clear]`
- `public @Composable fun rememberCouiSnackbarState()`、`public @Composable fun SnackbarHost(...)` - 状态创建与单条渲染 `[Snackbar.kt:rememberCouiSnackbarState]` `[Snackbar.kt:SnackbarHost]`
- `public object CouiSnackbarDefaults` + `internal const val springMinimumChange` - 时长、弹簧参数与底部边距 `[CouiSnackbarDefaults.kt:CouiSnackbarDefaults]` `[CouiSnackbarDefaults.kt:springMinimumChange]`

## Invariants
- show require durationMillis >= 0；current 非空时返回 false 且不替换内容（单实例互斥）`[Snackbar.kt:CouiSnackbarState.show]`
- dismiss 在 current 为空或已 dismissing 时直接返回，重复调用不叠加动画 `[Snackbar.kt:CouiSnackbarState.dismiss]`
- clear 仅当 current?.id == id 时清理并复位 dismissing/withoutAnimation，由 SnackbarHost 在退场收尾后调用 `[Snackbar.kt:CouiSnackbarState.clear]`
- current 为 null 时不渲染；退场动画结束才 clear 并回调 onDismissed；durationMillis=0 表示不计时 `[Snackbar.kt:SnackbarHost]`
- DOWN/MOVE 暂停计时，UP/CANCEL 后按完整时长重新计时；手势只观察不消费 `[Snackbar.kt:SnackbarHost]`
- actionTaken 保证业务回调只提交一次，回调后立即 dismiss `[Snackbar.kt:SnackbarHost]`
- 入场 showResponse=.3、退场 dismissResponse=.25、bounce=0；进度映射 minScale .8→1 缩放与 0→1 透明度 `[Snackbar.kt:SnackbarHost]`
- withoutAnimation=true 时 spring.cancelComplete 后立即 clear，不播退场动画 `[Snackbar.kt:SnackbarHost]`
- 操作与图标在 show 时快照；bottomMargin=32dp 由组件承担，宿主只需放置底部居中 `[Snackbar.kt:CouiSnackbarData]` `[CouiSnackbarDefaults.kt:CouiSnackbarDefaults]`

## Side Effects
- CouiSnackbarState 用 mutableStateOf 持有 current/dismissing/withoutAnimation，nextId 自增 `[Snackbar.kt:CouiSnackbarState]`
- SnackbarHost 用 CouiSpring 动画（LaunchedEffect animateTo、退场 onEnd、onDispose cancelComplete）与 graphicsLayer 写 scaleX/scaleY/alpha `[Snackbar.kt:SnackbarHost]`
- LaunchedEffect 计时协程使用 withTimeoutOrNull 与 snapshotFlow{paused}；pointerInput 在 Initial pass 观察手势 `[Snackbar.kt:SnackbarHost]`
- 跨包复用 CouiToastSurface/CouiToastDefaults/CouiToastColors，读取 CouiTheme.isDark；在入场结束回调 onShown、退场收尾回调 onDismissed `[Snackbar.kt:SnackbarHost]`

## Verification
- Run: `./gradlew :coui:lintDebug`
