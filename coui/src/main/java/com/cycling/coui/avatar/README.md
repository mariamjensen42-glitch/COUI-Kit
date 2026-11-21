# Avatar

## Overview
头像容器包。`Avatar` 只提供固定尺寸、轮廓与底色的容器，内容（图片、首字母、图标）由调用方提供；不做图片加载、占位或取字。

## Interface
- `public object CouiAvatarDefaults`：默认/小/大尺寸与原生轮廓基线 [Avatar.kt:CouiAvatarDefaults]
- `public @Composable fun Avatar(modifier, size, shape, containerColor, content)` [Avatar.kt:Avatar]

## Invariants
- 组件只负责尺寸、裁剪轮廓与底色，不加载图片、不生成占位或首字母 [Avatar.kt:Avatar]
- 修饰符顺序固定为 `size(size).clip(shape).background(containerColor)`，内容居中 [Avatar.kt:Avatar]
- 默认 `shape = CouiTheme.shapes.circle`，默认容器色为 `CouiTheme.colorScheme.container` [Avatar.kt:Avatar]

## Side Effects
- 无跨包副作用。

## Verification
- Run: `./gradlew :coui:lintDebug`
