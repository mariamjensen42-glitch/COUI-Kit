# SwipeReveal

## Overview
侧滑露出预设包。`CouiSwipeRevealDefaults` 提供 SystemUI 风格侧滑的融合、跟随、停靠尺寸与弹簧参数；只定义算法预设，不包含记录侧滑、删除确认或业务提交逻辑。

## Interface
- `public object CouiSwipeRevealDefaults`：融合操作尺寸/模糊/起点、菜单跟随距离/最小间距、开启触发距离、最大速度、融球混合范围，以及 `menuReboundSpec`/`itemSettleSpec` 弹簧参数 [CouiSwipeRevealDefaults.kt:CouiSwipeRevealDefaults]

## Invariants
- 本包只提供预设常量与弹簧规格，不处理删除确认或业务提交 [CouiSwipeRevealDefaults.kt:CouiSwipeRevealDefaults]
- `menuReboundSpec` 按注释的 `COUISpringForce` 换算：`dampingRatio = 1 - bounce`，`stiffness = (2π / response)²` [CouiSwipeRevealDefaults.kt:menuReboundSpec]
- 融合与菜单参数与条目停靠参数分离，调用方需成组使用 [CouiSwipeRevealDefaults.kt:CouiSwipeRevealDefaults]

## Side Effects
- 无跨包副作用。

## Verification
- Run: `./gradlew :coui:lintDebug`
