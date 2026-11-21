# Card

## Overview
通用卡片容器包。`Card` 提供圆角、底色与内留白，内部结构完全由调用方决定；可选整卡点击复用列表行的按压反馈。

## Interface
- `public object CouiCardDefaults`：原生轮廓基线与四边内留白 [Card.kt:CouiCardDefaults]
- `public @Composable fun Card(modifier, onClick, enabled, dark, shape, containerColor, contentPadding, interactionSource, content)` [Card.kt:Card]

## Invariants
- `onClick == null` 时卡片纯静态；非 null 时整卡可点，且使用 `couiButtonFeedback(list = true)` + 无指示的 `clickable` [Card.kt:Card]
- 交互修饰符顺序为 `clip(shape) → background(containerColor) → 交互 → padding`，按压蒙层不裁剪轮廓 [Card.kt:Card]
- 默认 `shape` 为 `CouiTheme.shapes.card`，默认 `containerColor` 为 `CouiTheme.colorSchemeFor(dark).card` [Card.kt:Card]
- 可选 `interactionSource` 仅在 `onClick != null` 时被采用，否则组件内部 `remember` [Card.kt:Card]

## Side Effects
- 指针输入：`clickable` 语义角色 `Role.Button`，按压反馈通过 `MutableInteractionSource` 分发 [Card.kt:Card]
- 读取 `CouiTheme.isDark` 与 `CouiTheme.colorSchemeFor` 获取默认外观 [Card.kt:Card]
- 绘制：`clip`/`background` 圆角容器 [Card.kt:Card]

## Verification
- Run: `./gradlew :coui:lintDebug`
