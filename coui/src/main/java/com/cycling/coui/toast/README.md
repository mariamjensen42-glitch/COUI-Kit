# Toast

## Overview
COUI 轻提示组件包：ToastHost 按 id 渲染 CouiToast 队列，ToastHostState 持有条目与自增 id；每条提示的入场动画与停留计时同时开始，退场动画结束后才从队列移除；操作与图标在入队时快照。internal CouiToastSurface 是单条绘制节点，被 snackbar 包跨包复用。

## Interface
- `public @Immutable data class CouiToast` - id/message/duration/actionLabel/onAction/icon `[Toast.kt:CouiToast]`
- `public @Stable class ToastHostState`（internal 构造） - 持有 toasts 队列，不持协程、Context 或导航 `[Toast.kt:ToastHostState]`
- `public fun ToastHostState.show(...)` / `dismiss(id)` / `dismissAll()` - 入队、按 id 移除、立即清空 `[Toast.kt:ToastHostState.show]` `[Toast.kt:ToastHostState.dismiss]` `[Toast.kt:ToastHostState.dismissAll]`
- `public @Composable fun rememberToastHostState()`、`public @Composable fun ToastHost(...)` - 状态创建与队列渲染 `[Toast.kt:rememberToastHostState]` `[Toast.kt:ToastHost]`
- `public enum class CouiToastDuration`、`public @Immutable data class CouiToastColors`、`public object CouiToastDefaults` + `colors(dark)` - 停留档位与尺寸/颜色参数 `[CouiToastDefaults.kt:CouiToastDuration]` `[CouiToastDefaults.kt:CouiToastColors]` `[CouiToastDefaults.kt:CouiToastDefaults]` `[CouiToastDefaults.kt:colors]`
- `internal @Composable fun CouiToastSurface(...)` - 单条提示绘制节点，snackbar 跨包复用 `[Toast.kt:CouiToastSurface]`

## Invariants
- show 追加后 while entries.size > maxVisible(3) 移除最早条目 `[Toast.kt:ToastHostState.show]`
- 队列项在 exitMillis=160ms 退场动画结束后才移除，队列删除与动画时长必须一致 `[Toast.kt:ToastItem]` `[CouiToastDefaults.kt:CouiToastDefaults.exitMillis]`
- ToastItem 在单个 LaunchedEffect(toast.id) 内顺序推进：targetState=true → 停留结束 → false → delay(exitMillis) → dismiss(id) `[Toast.kt:ToastItem]`
- 停留阶段由 withTimeoutOrNull(duration) 与 snapshotFlow{actionTaken}.first 二选一结束，操作后立即退场 `[Toast.kt:ToastItem]`
- actionTaken 保证操作回调只触发一次；actionLabel 仅在 onAction 非空时显示 `[Toast.kt:ToastItem]`
- dismissAll 不等待退场动画；dismiss(id) 只移除指定 id；宿主必须保持组合，页面移除时未消费提示随之丢弃 `[Toast.kt:ToastHost]`
- 有操作按钮时文案 end padding 为 0；换行由 onTextLayout 回传，多行上下留白不参与宽度测量 `[Toast.kt:CouiToastSurface]`
- 操作按钮与图标槽在入队时随文案快照，更新内容需重新调用 show `[Toast.kt:CouiToast]`

## Side Effects
- ToastHostState 用 mutableStateListOf 持有队列，nextId 自增分配 id `[Toast.kt:ToastHostState]`
- ToastItem 使用 LaunchedEffect + delay/withTimeoutOrNull/snapshotFlow，以及 MutableTransitionState + AnimatedVisibility fade/slide 160ms `[Toast.kt:ToastItem]`
- CouiToastSurface 使用 Modifier.shadow(26dp)、clip/background、semantics(liveRegion=Polite) 与 clickable(indication=null, Role.Button) `[Toast.kt:CouiToastSurface]`
- ToastHost 默认参数读取 CouiTheme.isDark 并调用 CouiToastDefaults.colors `[Toast.kt:ToastHost]`

## Verification
- Run: `./gradlew :coui:lintDebug`
