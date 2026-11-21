# TopTips

## Overview
顶部提示条包。`TopTips` 复刻 `COUICustomTopTips` 容器，仅以 `scaleY` 显隐；`TopTipsContent` 提供图标、标题、跑马灯正文、操作与关闭槽的默认排布。

## Interface
- `public object CouiTopTipsDefaults`：`animationMillis = 250`、内容上下留白/最小高度、图标尺寸与左右边距、`containerColor(dark)` [CouiTopTipsDefaults.kt:CouiTopTipsDefaults]
- `public @Composable fun TopTips(modifier, visible, shape, dark, containerColor, content: BoxScope.() -> Unit)` [TopTips.kt:TopTips]
- `public @Composable fun TopTipsContent(modifier, content, title, icon, action, close, dark)` [TopTipsContent.kt:TopTipsContent]

## Invariants
- `TopTips` 只动画 `scaleY`；`!visible` 且 scale 不大于 0 时直接返回，不参与布局、绘制与触摸 [TopTips.kt:TopTips]
- 修饰符顺序固定：`graphicsLayer(scaleY)` → `clip(shape)` → `background(containerColor)` → `defaultMinSize(contentMinHeight)` → 内边距 [TopTips.kt:TopTips]
- `TopTipsContent` 子项顺序固定：icon 槽 → title（最多 2 行省略）→ `MarqueeText`（`weight(1f)`）→ action → close 槽 [TopTipsContent.kt:TopTipsContent]
- `title` 为 null 或空串时不占位；`icon`/`action`/`close` 为 null 时不组合 [TopTipsContent.kt:TopTipsContent]
- 标题使用 `colorSchemeFor(dark).onSurface`，正文使用 `onSurfaceVariant` [TopTipsContent.kt:TopTipsContent]

## Side Effects
- 组合动画：`animateFloatAsState`（250ms tween）+ `graphicsLayer` 缩放 [TopTips.kt:TopTips]
- 默认参数读取 `CouiTheme.shapes.medium` 与 `CouiTheme.isDark` [TopTips.kt:TopTips]
- `TopTipsContent` 读取 `CouiTheme.colorSchemeFor(dark)` 并组合 `MarqueeText` 产生正文滚动 [TopTipsContent.kt:TopTipsContent]
- 绘制：`clip(shape)` 与 `background(containerColor)` [TopTips.kt:TopTips]

## Verification
- Run: `./gradlew :coui:lintDebug`
