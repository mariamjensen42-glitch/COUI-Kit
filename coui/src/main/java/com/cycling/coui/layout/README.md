# Layout

## Overview
页面布局基础包。提供窗口宽度档位与 `AdaptiveLayout`、页面配色与尺寸 token、分组容器（`Group`/`Divider`/`Footer`/`Item`）、快捷入口网格 `QuickActionGrid`，以及带背景采样与系统安全区处理的 `Scaffold`。

## Interface
- `public enum class CouiWindowSizeClass { Compact, Medium, Expanded }`：`of(width)` 与 600dp/840dp 断点 [AdaptiveLayout.kt:CouiWindowSizeClass]
- `public val LocalCouiWindowSizeClass: ProvidableCompositionLocal<CouiWindowSizeClass>` [AdaptiveLayout.kt:LocalCouiWindowSizeClass]
- `public @Composable fun AdaptiveLayout(modifier, content: (CouiWindowSizeClass) -> Unit)` [AdaptiveLayout.kt:AdaptiveLayout]
- `public @Immutable data class CouiPageColors(background, card, title, summary, disabled, accent, divider)` [CouiPageComponents.kt:CouiPageColors]
- `public object CouiPageDefaults`：页面/行/图标/间距/工具栏/快捷入口尺寸、`themeColors(dark)` 与 `colors(accent, dark)` [CouiPageComponents.kt:CouiPageDefaults]
- `public @Composable fun Group(modifier, title, footer, headerActions, colors, content)` [CouiPageComponents.kt:Group]
- `public @Composable fun Divider(modifier, startIndent, endIndent, color)` [CouiPageComponents.kt:Divider]
- `public @Composable fun Footer(modifier, text, color)` [CouiPageComponents.kt:Footer]
- `public @Composable fun Item(modifier, icon, iconSize, summary, trailing, onClick, enabled, dark, contentPadding, title)` [CouiPageComponents.kt:Item]
- `public @Stable class CouiQuickAction(label, icon, onClick, enabled, labelMaxLines)` [QuickActionGrid.kt:CouiQuickAction]
- `public enum class CouiQuickActionStyle { Card, Capsule }` [QuickActionGrid.kt:CouiQuickActionStyle]
- `public @Composable fun QuickActionGrid(modifier, actions, columns, style, dark, colors)` [QuickActionGrid.kt:QuickActionGrid]
- `public @Composable fun Scaffold(modifier, topBar, bottomBar, containerColor, windowInsets, floatingActionButton, content)` [Scaffold.kt:Scaffold]

## Invariants
- `AdaptiveLayout` 按自身可用宽度取档，无界时回退窗口宽度；零/负宽度归 `Compact` [AdaptiveLayout.kt:AdaptiveLayout]
- 未包裹时 `LocalCouiWindowSizeClass` 默认 `Compact` [AdaptiveLayout.kt:LocalCouiWindowSizeClass]
- `Group` 的 `colors` 只控制本组卡片、标题与说明，内容槽继续使用各组件自己的主题；`headerActions` 仅在 `title` 非空时渲染 [CouiPageComponents.kt:Group]
- `Item` 在 `onClick != null` 时安装唯一点击与反馈，行内留白参与点击；无 `onClick` 时不提供点击、按钮语义、语义合并或反馈 [CouiPageComponents.kt:Item]
- `QuickActionGrid` 要求 `columns > 0`；末行不足用空位补齐，卡片宽度不随内容变化，整卡承担点击 [QuickActionGrid.kt:QuickActionGrid]
- 快捷项禁用时仅降低内容不透明度，仍保留占位 [QuickActionGrid.kt:QuickActionCell]
- `Scaffold` 要求有界宽高；横向安全区由容器应用并消费，纵向安全区由栏位处理，无栏位或高度为零时交给 `content` [Scaffold.kt:Scaffold]
- `Scaffold` 的正文须应用并消费返回的 `PaddingValues`；IME 避让由宿主 `Modifier.imePadding()` 负责 [Scaffold.kt:Scaffold]
- `Scaffold` 的顶栏/底栏/FAB 在正文采样层之外绘制；正文内采样消费者须自行分离源与消费节点 [Scaffold.kt:Scaffold]

## Side Effects
- CompositionLocal 供给 `LocalCouiWindowSizeClass` [AdaptiveLayout.kt:AdaptiveLayout]
- 读取 `LocalWindowInfo`/`LocalDensity` 与窗口宽度 [AdaptiveLayout.kt:AdaptiveLayout]
- `Scaffold` 装配背景采样：`rememberCouiBackdrop`、`couiBackdrop`、`ProvideCouiBackdrop` [Scaffold.kt:Scaffold]
- `Scaffold` 使用 `SubcomposeLayout`、`onConsumedWindowInsetsChanged`、`windowInsetsPadding` 与 `drawBehind` [Scaffold.kt:Scaffold]
- 指针/无障碍：`clickable(Role.Button)`、`heading()` 语义与 `couiButtonFeedback` [CouiPageComponents.kt:Item]
- 绘制：`clip`/`background` 卡片、`Divider` 至少一个物理像素 [CouiPageComponents.kt:Divider]
- 读取 `CouiTheme.isDark`/`colorSchemeFor`/`shapes` 派生默认外观 [CouiPageComponents.kt:CouiPageDefaults]

## Verification
- Run: `./gradlew :coui:lintDebug`
