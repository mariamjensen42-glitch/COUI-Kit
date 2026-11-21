# FloatingActionButton

## Overview
该包提供两种浮动操作入口：`Fab` 是 API 33+ 的单按钮，由 `CouiBackdrop`、`CouiFloatingRenderer`、按压/触摸动效与聚光渲染；`FloatingButton` 是可展开菜单，管理最多 6 个 `CouiFloatingButtonItem` 的错峰动画、受控或内部展开与自动收起。`CouiFabDefaults`、`CouiFloatingButtonDefaults` 固定形状、颜色和时长。

## Interface
- `public enum class CouiFabColor`：`Notes`、`Phone`、`InfraredRemote`、`Recorder`。`[CouiFabColor.kt:CouiFabColor]`
- `public object CouiFabDefaults`：`circleShape`、`roundedSquareShape`、`contentColor`、`containerColor(preset, dark)`、`material(color, dark, extension)`、`shadow(color, density, contourGlowEnabled)`。`[CouiFabDefaults.kt:CouiFabDefaults]`
- `public @RequiresApi(33) @Composable fun Fab(modifier, backdrop, renderer, onClick, dark, shape, material, shadow, foregroundComposition, contentColor, content)`。`[Fab.kt:Fab]`
- `public @Immutable data class CouiFloatingButtonItem`：图标、标签、位置、启用、可展开、描述、容器/内容色、标签底色与文字色。`[FloatingButton.kt:CouiFloatingButtonItem]`
- `public object CouiFloatingButtonDefaults`：`maxItems = 6`、尺寸与边距、时长、弹簧、旋转及 `autoCollapseMillis`。`[FloatingButton.kt:CouiFloatingButtonDefaults]`
- `public @Composable fun FloatingButtonLabel(modifier, onClick, item, dark, enabled, interactionSource)`。`[FloatingButton.kt:FloatingButtonLabel]`
- `public @Composable fun FloatingButton(modifier, item, items, onItemClick, dark, expanded, onExpandedChange, onMainActionSelected, autoCollapseMillis)`。`[FloatingButton.kt:FloatingButton]`

## Invariants
- `Fab` 标注 `@RequiresApi(33)`，组合需要 Android 33+。`[Fab.kt:Fab]`
- `Fab` 的 `backdrop` 默认 `checkNotNull(LocalCouiBackdrop.current)`；未显式传入且宿主未提供时组合抛 `IllegalStateException`。`[Fab.kt:Fab]`
- `Fab` 内容固定居中于 56dp Box；表面与内容缩放取 `motion.scale + press.scale - 1f`，两个动画源须围绕 1f。`[Fab.kt:Fab]`
- `FloatingButton` 以 `require(items.size <= maxItems)` 限制最多 6 项，超出抛 `IllegalArgumentException`。`[FloatingButton.kt:FloatingButton]`
- 展开受控条件为 `expanded != null`：此时不写 `internalExpanded`，`setExpanded` 仍总是调用 `onExpandedChange`。`[FloatingButton.kt:FloatingButton]`
- 自动收起由 `LaunchedEffect(open)` 延时 `autoCollapseMillis`，仅在 `open && autoCollapseMillis > 0` 时收起；其他交互不重启计时。`[FloatingButton.kt:FloatingButton]`
- 主按钮状态互斥：收起时展开；展开时调用 `onMainActionSelected`，仅当回调缺失或返回非 `true` 才收起。`[FloatingButton.kt:FloatingButton]`
- 子项自下而上排列：`asReversed()` 迭代，索引 0 用 `firstItemBottomMargin` 32dp，其余用 `itemBottomMargin` 16dp；`CouiFloatingButtonItem.location` 不参与布局。`[FloatingButton.kt:FloatingButton]` `[FloatingButton.kt:CouiFloatingButtonItem]`
- 子项点击先调用 `onItemClick` 再请求收起；点击启用要求 `item.enabled && expanded`，按压缩放另要求 `expandable`。`[FloatingButton.kt:FloatingButtonMenuItem]`
- 展开动画：索引大于 0 的项按 `index * itemStaggerMillis` 错峰；位移弹簧 stiffness 500f、damping 0.8f，缩放 0.6f→1f 300ms，透明度 350ms；收起位移/缩放 150ms、透明度 200ms。`[FloatingButton.kt:FloatingButtonMenuItem]`
- 主按钮旋转展开 45°、收起 0°；无子项用 300ms tween，有子项用 250ms。主按钮内容为 `item.copy(label = null)`，不渲染标签。`[FloatingButton.kt:FloatingButton]`
- 禁用子项容器 alpha 0.38f、内容 alpha 0.6f；标签阴影 12dp 仅在 `labelBackgroundColor.alpha > 0f` 时绘制。`[FloatingButton.kt:FloatingButtonItemContent]`
- `CouiFabDefaults.shadow` 按 `contourGlowEnabled` 分支给出不同投影、高度与光源参数；`causticColor` 以 luma 与通道比例计算并固定 alpha 153。`[CouiFabDefaults.kt:CouiFabDefaults.shadow]` `[CouiFabDefaults.kt:CouiFabDefaults.causticColor]`

## Side Effects
- 消费 `LocalCouiBackdrop`，缺失时组合失败。`[Fab.kt:Fab]`
- 消费 `LocalCouiContourGlowEnabled` 选择阴影与焦散配置。`[Fab.kt:Fab]`
- 通过 `ProvideCouiContent` 提供内容色。`[Fab.kt:Fab]`
- 默认 `remember { CouiFloatingRenderer.standalone() }`，并用 `rememberGraphicsLayer` 记录内容供 `couiFloatingSurface` 与 backdrop 绘制。`[Fab.kt:Fab]`
- 指针与光效：`couiTouchMotion(consumeDrag = true, cancelOnConsumedMove = false)`、`couiPressMotion`、`rememberCouiSpotlight(SpotlightPreset.OpaqueSmall1)`、`drawBehind(drawLight)`、`couiFabFeedback`。`[Fab.kt:Fab]`
- 无障碍：所有 `clickable` 使用 `indication = null` 与 `Role.Button`，子项 `contentDescription` 转发给 `CouiIcon`；标签按压使用外部或内部 `MutableInteractionSource`。`[Fab.kt:Fab]` `[FloatingButton.kt:FloatingButtonItemContent]` `[FloatingButton.kt:FloatingButtonLabel]`
- 平台转换：`android.graphics.BlendMode`、`android.graphics.Color`、`Color.toArgb`，并读取 `CouiShapes.Default` 与 `CouiColor` Day/Night。`[CouiFabDefaults.kt:CouiFabDefaults]`
- 协程动画：`LaunchedEffect(open)` 自动收起、`Animatable.animateTo`、`animateFloatAsState` 旋转、`collectIsPressedAsState` 按压。`[FloatingButton.kt:FloatingButton]`

## Verification
- Run: `./gradlew :coui:lintDebug`
