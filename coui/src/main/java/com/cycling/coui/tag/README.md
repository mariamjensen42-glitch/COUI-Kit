# Tag

## Overview
标签组件包。`Tag` 提供可选左图标、单行文本与可选右图标的状态标签，容器按可覆盖的四角轮廓裁剪；对应 ColorOS 17 `COUITagView`，不处理点击。

## Interface
- `public data class CouiTagColors(container, content, stroke)` [Tag.kt:CouiTagColors]
- `public object CouiTagDefaults`：四角半径、左右内留白、左图标尺寸/边距/间距、字号、描边宽度与 `colors(dark)` [Tag.kt:CouiTagDefaults]
- `public @Composable fun Tag(modifier, text, leftIcon, rightIcon, dark, colors, shape, strokeWidth, textSize, contentPadding)` [Tag.kt:Tag]

## Invariants
- 标签只表达状态，不处理点击；交互由宿主在外层提供 [Tag.kt:Tag]
- 文本固定单行，超出以 `TextOverflow.Ellipsis` 省略 [Tag.kt:Tag]
- 仅当 `strokeWidth > 0.dp` 时绘制描边 [Tag.kt:Tag]
- 左图标槽固定 `5×10dp`，上下各 `3dp`、右侧 `2dp` 间距 [Tag.kt:Tag]
- 传入 `shape` 为 `RoundedCornerShape` 时可独立覆盖四角，对应原厂四角属性 [Tag.kt:Tag]

## Side Effects
- 读取 `LocalDensity`（dp→sp）与 `CouiTheme.isDark` [Tag.kt:Tag]
- 绘制：`clip(shape)` + `background` + 可选 `border` [Tag.kt:Tag]
- 文本排版：`BasicText` 使用 `PlatformTextStyle(includeFontPadding = false)` [Tag.kt:Tag]

## Verification
- Run: `./gradlew :coui:lintDebug`
