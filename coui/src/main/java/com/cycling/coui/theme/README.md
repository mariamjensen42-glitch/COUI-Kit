# Theme

## Overview
COUI 的主题与外观上下文包。提供进程级默认样式注册（`Coui`）、`CouiTheme` 组合作用域、色彩/排版/轮廓 token，以及内容色与文本样式 CompositionLocal；其余组件包通过读取这些 token 装配外观。

## Interface
- `public object Coui`：进程级默认样式入口，`initialize(defaultStyle)` 一次性注册 `CouiStyle` [Coui.kt:Coui]
- `public interface CouiStyle`：适配模块契约，解析主题默认值、内容作用域与最小交互尺寸 [Coui.kt:CouiStyle]
- `public data class CouiThemeDefaults`：单次主题解析结果（colorScheme/typography/materialTint）[Coui.kt:CouiThemeDefaults]
- `public object CouiColor`：固定日夜色值，含 `Day`/`Night` 语义色 [CouiColor.kt:CouiColor]
- `public data class CouiColorScheme`：组件使用的色彩角色 token [CouiTheme.kt:CouiColorScheme]
- `public object CouiPresets`：`Light`/`Dark`/`GreenLight`/`GreenDark` 预设与 `green(dark)` [CouiTheme.kt:CouiPresets]
- `public data class CouiTypography`：body/label/menuItem 文本样式与可选对话框字体族 [CouiTheme.kt:CouiTypography]
- `public val LocalCouiShapes`、`LocalCouiDarkTheme`、`LocalCouiContentColor`、`LocalCouiTextStyle`：外观 CompositionLocal [CouiTheme.kt:LocalCouiShapes]
- `public object CouiTheme`：`isDark`/`colorScheme`/`typography`/`shapes`/`colorSchemeFor(dark)`/`materialTintFor(dark)` [CouiTheme.kt:CouiTheme]
- `public @Composable fun CouiTheme(...)`：以 `CompositionLocalProvider` 建立主题作用域 [CouiTheme.kt:CouiTheme]
- `internal @Composable fun ProvideCouiContent(...)` 与 `ProvideCouiTextStyle(...)`：内容色/文本样式下传 [Coui.kt:ProvideCouiContent]
- `internal @Composable fun CouiText(...)`、`CouiIcon(...)`：包内文本与图标原语 [CouiContent.kt:CouiText]

## Invariants
- `Coui.initialize` 只能成功调用一次，重复调用触发 `check(!initialized)` 异常 [Coui.kt:initialize]
- `Coui.defaultStyle` 未初始化时为 `NativeCouiStyle`，读取方无需判空 [Coui.kt:defaultStyle]
- `CouiTheme.colorSchemeFor(dark)`：请求模式与宿主 `dark` 不一致时不复用宿主色板，回退 `CouiPresets` [CouiTheme.kt:colorSchemeFor]
- `CouiTheme.materialTintFor(dark)` 在模式不一致时返回 `Color.Transparent` [CouiTheme.kt:materialTintFor]
- `CouiTheme` 的 `shapes` 默认沿用 `LocalCouiShapes.current`，嵌套主题不重置轮廓 [CouiTheme.kt:CouiTheme]
- `CouiTheme` 以 `mergeTextStyle = false` 提供 `typography.body` [CouiTheme.kt:CouiTheme]
- `CouiIcon` 在 `contentDescription == null` 时不建立语义节点，非空时设置 `Role.Image` 语义 [CouiContent.kt:CouiIcon]
- `LocalCouiContentColor` 的 `Color.Unspecified` 表示未提供，由 `CouiTheme.contentColor` 在消费处回退 `onSurface` [CouiTheme.kt:LocalCouiContentColor]

## Side Effects
- 提供 `LocalCouiTheme`、`LocalCouiDarkTheme`、`LocalCouiShapes`、`LocalCouiContentColor`、`LocalCouiTextStyle` 五个 CompositionLocal [CouiTheme.kt:CouiTheme]
- 进程级可变单例状态 `Coui.defaultStyle`（`@Volatile`，`initialize` 加 `@Synchronized`）[Coui.kt:Coui]
- 读取平台 `isSystemInDarkTheme()` 作为 `isDark` 默认值 [CouiTheme.kt:isDark]
- `CouiIcon` 加载 `Painter`/`ImageVector` 并通过 `ColorFilter.tint` 绘制 [CouiContent.kt:CouiIcon]

## Verification
- Run: `./gradlew :coui:lintDebug`
