# Shape

## Overview
全库共享的轮廓 token 包。`CouiShapes` 提供十一个圆角 token，是 `CouiTheme.shapes` 与各组件 `Defaults` 的形状来源；`CouiShape` 按圆角权重生成平滑圆角轮廓，`CouiCapsuleShape` 提供胶囊等值线。

## Interface
- `public object CouiCapsuleShape : Shape`：胶囊零等值线，`createOutline` 返回 `Outline.Generic` [CouiCapsuleShape.kt:CouiCapsuleShape]
- `public const val CouiCornerWeight = 2.5f`：默认圆角权重 [CouiShape.kt:CouiCornerWeight]
- `public @Immutable data class CouiShape(radius, cornerWeight) : Shape` [CouiShape.kt:CouiShape]
- `internal fun Shape.resolveCouiCornerRadius(explicitRadius): Dp`：为已知 `CouiShape` 解析并校验半径 [CouiShape.kt:resolveCouiCornerRadius]
- `public @Immutable data class CouiShapes`：extraSmall/small/medium/large/extraLarge/card/menu/panel/dialog/capsule/circle 十一个 token [CouiShapes.kt:CouiShapes]
- `public val CouiShapes.Companion.Default: CouiShapes`：组件 `Defaults` 与无主题回退的取值来源 [CouiShapes.kt:CouiShapes.Companion.Default]

## Invariants
- `CouiShape` 初始化要求 `radius` 有限非负、`cornerWeight` 有限且不小于 2 [CouiShape.kt:CouiShape]
- `CouiShape.createOutline` 把半径夹到 `minDimension / 2`；半径为 0 返回 `Outline.Rectangle`，否则返回 `Outline.Generic` 的 Path [CouiShape.kt:CouiShape]
- 每个半角按弦长误差不超过 0.05px 的段数采样 [CouiShape.kt:CouiShape]
- `CouiCapsuleShape` 在尺寸非正或中心 SDF 不小于 0 时返回空的 `Outline.Generic` Path [CouiCapsuleShape.kt:CouiCapsuleShape]
- 胶囊采样点按精确 `Size` 缓存只读数据，Path 每次调用独立创建 [CouiCapsuleShape.kt:quarterContours]
- 胶囊轮廓每个角度做 32 次二分求半径，曲线细分误差阈值 0.025、深度上限 16 [CouiCapsuleShape.kt:buildQuarterContour]
- `resolveCouiCornerRadius` 要求显式半径与 `CouiShape.radius` 一致；非 `CouiShape` 必须显式给出有限非负半径 [CouiShape.kt:resolveCouiCornerRadius]

## Side Effects
- 全局 `android.util.LruCache<Size, List<Offset>>(16)` 跨实例共享胶囊轮廓采样点 [CouiCapsuleShape.kt:quarterContours]
- 每次 `createOutline` 分配新的 `Path` 并返回 `Outline.Generic`，走软件路径光栅化 [CouiShape.kt:CouiShape]

## Verification
- Run: `./gradlew :coui:lintDebug`
