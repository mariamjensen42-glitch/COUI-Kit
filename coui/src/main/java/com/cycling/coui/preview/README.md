# Preview

## Overview
Compose 预览辅助包。提供浅色/深色与三档断点预览注解，以及预览专用页面底板；三份内部预览用于校验图标按钮、卡片、侧栏、自适应布局与主题外观，不参与发布 API。

## Interface
- `public annotation class CouiLightDarkPreview`：浅色 + 深色两个 `@Preview` [CouiPreviewAnnotations.kt:CouiLightDarkPreview]
- `public annotation class CouiBreakpointPreview`：361dp/720dp/1000dp 三档宽度 `@Preview` [CouiPreviewAnnotations.kt:CouiBreakpointPreview]
- `internal @Composable fun CouiPreviewSurface(dark, content)`：铺满、套 `CouiTheme`、按分组间距留白的预览底板 [CouiPreviewAnnotations.kt:CouiPreviewSurface]
- `private @CouiLightDarkPreview @Composable fun IconButtonPreview()`、`CardPreview()`、`SidePanePreview()` [CouiContainerPreviews.kt:IconButtonPreview]
- `private @CouiBreakpointPreview @Composable fun AdaptiveLayoutPreview()` [CouiContainerPreviews.kt:AdaptiveLayoutPreview]
- `private @Composable fun PlusGlyph/CrossGlyph/DotsGlyph(tint)`：预览占位字形 [CouiContainerPreviews.kt:PlusGlyph]

## Invariants
- 预览不引用可选模块 `coui-icons`，改用自绘字形占位，只验证命中区、轮廓光与蒙层 [CouiContainerPreviews.kt:PlusGlyph]
- `CouiLightDarkPreview` 的深色项以 `Configuration.UI_MODE_NIGHT_YES` 声明；断点注解固定 361/720/1000dp [CouiPreviewAnnotations.kt:CouiLightDarkPreview]
- `CouiPreviewSurface` 内部以 `CouiTheme(darkTheme = dark)` 包裹，背景取 `CouiTheme.colorScheme.background`，并加 16dp 内边距 [CouiPreviewAnnotations.kt:CouiPreviewSurface]
- 全部预览函数为 `private`，不进发布 API [CouiContainerPreviews.kt:IconButtonPreview]

## Side Effects
- Android 工具注解：`@Preview` 元注解 [CouiPreviewAnnotations.kt:CouiLightDarkPreview]
- CompositionLocal 供给：预览底板通过 `CouiTheme` 建立主题作用域 [CouiPreviewAnnotations.kt:CouiPreviewSurface]
- 绘制：占位字形使用 `Canvas` 绘制线段与圆点 [CouiContainerPreviews.kt:PlusGlyph]
- 读取预览容器：`SidePane`、`Card`、`IconButton`、`AdaptiveLayout` 等公开组件 [CouiContainerPreviews.kt:SidePanePreview]

## Verification
- Run: `./gradlew :coui:lintDebug`
