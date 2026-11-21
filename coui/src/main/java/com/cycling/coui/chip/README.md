# Chip

## Overview
COUI 标签组件包：Chip 是受控的单个标签，ChipGroup/ChipGroupEditText 负责自动换行、折叠与内联输入，ChipTab/ChipTabRow 提供带材质、阴影与聚光的分类栏。选中、关闭与输入状态均由宿主持有；组件通过 CouiTheme、effect 包的材质/阴影/聚光能力与按下反馈完成外观。

## Interface
- `public @Composable fun Chip(...)` - 受控标签入口，默认 Suggestion 样式与 CouiCapsuleShape `[Chip.kt:Chip]`
- `public @Composable fun ChipGroup(...)` / `ChipGroupEditText(...)` - 自动换行标签组与标签加内联输入框组 `[ChipGroup.kt:ChipGroup]` `[ChipGroup.kt:ChipGroupEditText]`
- `public @Composable fun ChipTab(...)` / `ChipTabButton(...)` - 受控单选分类项与独立图标标签按钮 `[ChipTab.kt:ChipTab]` `[ChipTab.kt:ChipTabButton]`
- `public data class CouiChipTabItem` / `public @Composable fun ChipTabRow(...)` - 分类栏条目与单行受控分类栏 `[ChipTabRow.kt:CouiChipTabItem]` `[ChipTabRow.kt:ChipTabRow]`
- `public enum class CouiChipStyle`、`public data class CouiChipMetrics`/`CouiChipColors`、`public object CouiChipDefaults` - 样式枚举、尺寸/颜色参数表与 stiffness/metrics/textStyle/colors `[CouiChipDefaults.kt:CouiChipStyle]` `[CouiChipDefaults.kt:CouiChipDefaults]`
- `public object CouiChipTabDefaults` - ChipTab 颜色、文本样式、材质与阴影默认值 `[CouiChipTabDefaults.kt:CouiChipTabDefaults]`
- `internal object CouiChipPaths`、`internal fun flowChips(...)` - 关闭/折叠/展开路径与换行求解 `[CouiChipDefaults.kt:CouiChipPaths]` `[ChipGroup.kt:flowChips]`

## Invariants
- Chip 为受控组件：checkable=onSelectedChange!=null；可选中时点击调用 onSelectedChange(!selected)，不可选时才用 onClick `[Chip.kt:Chip]`
- Chip 子项顺序固定为背景、leadingIcon、文本、count、关闭按钮、红点；宽度夹在 minWidth 与 min(maxWidth, constraints.maxWidth) 之间 `[Chip.kt:Chip]`
- ChipGroup 行数上限：singleLine 为 1，collapsable && collapsed 取 max(1, collapsedMaxRows)，否则 Int.MAX_VALUE；collapsed 受控 `[ChipGroup.kt:ChipGroup]`
- ChipGroupEditText 固定把 measurables 最后一项当输入框，content 之外只允许该输入框；行尾空间不足 inputMinWidth 时另起一行 `[ChipGroup.kt:ChipGroupEditText]`
- ChipTab 重复点击仍回调 onClick，组件不自行取消选中，选中值由宿主决定 `[ChipTab.kt:ChipTab]`
- ChipTabRow require：key 唯一；空列表 selectedKey 为 null、非空时必须命中某项；renderers 与 items 等长且互不相同；animationLevel 在 1..4；必须有有限宽度 `[ChipTabRow.kt:ChipTabRow]`
- CouiChipDefaults.stiffness 要求 response 有限且大于 0 `[CouiChipDefaults.kt:CouiChipDefaults.stiffness]`
- CouiChipTabDefaults.material/shadow 把 checkedFraction 限制到 0..1，并据此缩放 elevation 与 caustic alpha `[CouiChipTabDefaults.kt:CouiChipTabDefaults.material]` `[CouiChipTabDefaults.kt:CouiChipTabDefaults.shadow]`

## Side Effects
- Chip 使用 animateFloatAsState + chipSpring、rememberCouiPressMotion/couiPressRipple，读取 LocalDensity、LocalLayoutDirection、CouiTheme，并注册 selectable(Role.Checkbox) 或 clickable(Role.Button) `[Chip.kt:Chip]`
- ChipGroup 在 measure 相位写 targetHeight，经 LaunchedEffect 驱动 Animatable snapTo/animateTo，并用 clipToBounds 裁剪 `[ChipGroup.kt:ChipGroup]`
- ChipTab 通过 ProvideCouiContent 提供 contentColor/textStyle，经 rememberGraphicsLayer、CouiBackdrop、CouiFloatingRenderer、rememberCouiSpotlight 绘制，并用手势/SpringAnimation 驱动选中过渡 `[ChipTab.kt:ChipSurface]` `[ChipTab.kt:ChipSelectionAlpha]`
- ChipTabRow 注册 Choreographer FrameCallback、rememberSaveable 持久化滚动位置、pointerInput 速度跟踪、rememberGraphicsLayer + nativeCanvas saveLayer(DST_OUT) 渐隐遮罩、280ms contentPadding 动画，并读取 LocalView 与刷新率 `[ChipTabRow.kt:ChipTabRow]` `[ChipTabRow.kt:ChipRowScroll]`

## Verification
- Run: `./gradlew :coui:lintDebug`
