# Text

## Overview
语义排版与文本组件包。`CouiTextKind` 定义 COUI 排版角色，`Text` 按角色生成 Compose 文本样式并支持 `AnnotatedString`；`MarqueeText` 复刻原厂 `COUIMarqueeTextView`；`CouiTextLinkDefaults` 提供链接配色与 `TextLinkStyles`。

## Interface
- `public enum class CouiTextKind(size/family/weight internal)`：ItemTitle/ItemSummary/Body/BodyLarge/TopBar/HomeTitle/Display/Search/ItemValue/ButtonLabel/ButtonDescLabel/ButtonDescSubLabel/Tag/GroupTitle/GroupFooter [Text.kt:CouiTextKind]
- `public @Composable fun Text(modifier, text: String, kind, color, onLineCount, textAlign, maxLines, overflow)` [Text.kt:Text]
- `public @Composable fun Text(modifier, text: AnnotatedString, kind, color, onLineCount, textAlign, maxLines, overflow)` [Text.kt:Text]
- `public @Composable fun rememberCouiTextStyle(kind): TextStyle` [Text.kt:rememberCouiTextStyle]
- `internal @Composable fun rememberCouiSystemTextStyle(kind, size)`：应用系统字重补偿 [Text.kt:rememberCouiSystemTextStyle]
- `public object CouiMarqueeTextDefaults`：空档、速度、起始偏移、渐隐长度与 `startDelayMillis = 1000L` [MarqueeText.kt:CouiMarqueeTextDefaults]
- `public @Composable fun MarqueeText(modifier, text, kind, color, enabled)` [MarqueeText.kt:MarqueeText]
- `public @Immutable data class CouiTextLinkColors(normal, pressed, disabled)` [CouiTextLinkDefaults.kt:CouiTextLinkColors]
- `public object CouiTextLinkDefaults`：`colors(dark)` 与 `textLinkStyles(dark)` [CouiTextLinkDefaults.kt:CouiTextLinkDefaults]
- `internal fun couiTextStyle(...)` 与 `internal fun couiAdaptFontSize(...)`：字体度量与字号适配算法 [CouiTextMetrics.kt:couiTextStyle]

## Invariants
- 字重/字族/字号由 `CouiTextKind` 固定；`ItemValue` 按 dp 换算（`size * density`），其余按 sp [Text.kt:rememberCouiTextStyle]
- 系统字重补偿：`fontWeightAdjustment` 非 `UNDEFINED` 时叠加并夹到 `1..1000` [Text.kt:rememberCouiSystemTextStyle]
- 行距按 SDK 35 与非 35 两套倍数（`couiLineSpacing`）；`HomeTitle`/`Display`/`Tag` 等固定 1f [Text.kt:couiLineSpacing]
- 默认对齐：ItemTitle/ItemSummary/Body/BodyLarge/GroupFooter/Display 起始对齐，其余居中 [Text.kt:couiDefaultTextAlign]
- `AnnotatedString` 中部分 span 自带颜色优先于 `color` 参数 [Text.kt:Text]
- `MarqueeText` 仅在 `enabled`、容器已测量且文本超宽时滚动，否则按静态单行绘制且无渐隐 [MarqueeText.kt:MarqueeText]
- 滚动先静止 `startDelayMillis`，按帧时间以 `speed` 推进，与刷新率无关；内容按周期重复实现无缝回绕 [MarqueeText.kt:MarqueeText]
- 文本含 RTL 字符时滚动方向相反；无障碍描述固定为原始文本 [MarqueeText.kt:MarqueeText]
- Compose `TextLinkStyles` 无禁用态：链接不可用时应改用 `disabled` 颜色的普通 span，不挂 `LinkAnnotation` [CouiTextLinkDefaults.kt:CouiTextLinkDefaults]
- `couiAdaptFontSize` 在 `densityDpi` 为 300/296 或 `smallestScreenWidthDp <= 210` 时固定 `scale = 1f` [CouiTextMetrics.kt:couiAdaptFontSize]

## Side Effects
- 平台字体：`Typeface`/`TextPaint`/`StaticLayout` 与 `fontWeightAdjustment` 读取 [Text.kt:rememberCouiSystemTextStyle]、[MarqueeText.kt:marqueeTextPaint]
- 帧时钟动画：`delay(startDelayMillis)` + `withFrameNanos` 推进相位 [MarqueeText.kt:MarqueeText]
- 原生画布：`drawIntoCanvas`、`saveLayer`、`LinearGradient` 与 `DST_IN` 渐隐遮罩 [MarqueeText.kt:MarqueeText]
- 无障碍：`semantics { contentDescription = text }` [MarqueeText.kt:MarqueeText]
- 读取 `LocalConfiguration`/`LocalDensity` 与 `CouiTheme.contentColor` [Text.kt:CouiTextContent]

## Verification
- Run: `./gradlew :coui:lintDebug`
