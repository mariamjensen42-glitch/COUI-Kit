# 📐自适应布局

`CouiAdaptiveLayout` 按容器宽度给出窗口尺寸档位，让同一份内容在手机、折叠屏展开与平板横屏下重新排布。

它只做宽度分档，不提供导航、不切换页面、也不接管滚动；如何按档位调整布局由调用方决定。

## 最小调用

~~~kotlin
CouiAdaptiveLayout { sizeClass ->
    when (sizeClass) {
        CouiWindowSizeClass.Compact -> SingleColumn()
        CouiWindowSizeClass.Medium -> TwoColumn()
        CouiWindowSizeClass.Expanded -> TwoColumnWithSideBar()
    }
}
~~~

回调参数同时写入 `LocalCouiWindowSizeClass`，深层子组件可以直接读取，不必逐层传参：

~~~kotlin
val sizeClass = LocalCouiWindowSizeClass.current
~~~

## 三个档位

| 档位 | 宽度 | 典型窗口 |
| --- | --- | --- |
| `Compact` | 小于 600dp | 手机竖屏、分屏窄窗 |
| `Medium` | 600dp 至 839dp | 折叠屏展开态、手机横屏、平板竖屏 |
| `Expanded` | 840dp 及以上 | 平板横屏、桌面窗口 |

断点常量见 `CouiWindowSizeClass.mediumMinWidth` 与 `expandedMinWidth`。原厂参考设备一加 Ace 5（1264×2780 @560dpi，约 361dp）属于 `Compact`。

## 量的是容器

`CouiAdaptiveLayout` 用当前约束的宽度分档，而不是窗口宽度。分屏里的局部区域、侧栏内容、对话框内部都会报自己的可用宽度，因此同一屏上可以并存不同的档位：

~~~kotlin
Row {
    CouiAdaptiveLayout(Modifier.weight(1f)) { compact -> CompactContent(compact) }
    CouiAdaptiveLayout(Modifier.weight(2f)) { wide -> WideContent(wide) }
}
~~~

约束无界时（例如放在横向滚动容器里）回退到窗口宽度，避免读出 0。

容器按内容尺寸布局，不撑满父级。需要铺满时显式传 `Modifier.fillMaxSize()`：

~~~kotlin
CouiAdaptiveLayout(Modifier.fillMaxSize()) { sizeClass -> ... }
~~~

这一点在包 `Scaffold` 时尤其重要：`Scaffold` 要求测量约束有界，省略 `fillMaxSize` 会让它拿到无界约束。

## 只有宽度一维

档位只按宽度分。页面纵向滚动、横屏手机与平板竖屏的高度都可能相同，按高度分档反而让同一类窗口落到不同分支，因此组件不读高度。需要处理键盘或折叠屏姿态时，用 `WindowInsets` 与 `LocalWindowInfo` 自行判断。

## 预览

`CouiBreakpointPreview` 一次渲染 361dp / 720dp / 1000dp 三档宽度，用来检查断点两侧的切换：

~~~kotlin
@CouiBreakpointPreview
@Composable
private fun MyContentPreview() {
    CouiTheme {
        CouiAdaptiveLayout { sizeClass -> MyContent(sizeClass) }
    }
}
~~~

注解只负责声明预览尺寸与外观，不提供内容外壳，所以示例自己套了一层 `CouiTheme`。库内预览用的底板 `CouiPreviewSurface` 是 `internal`，不对外发布。

## Demo 示例

三档宽度下的分栏示例见 [DemoContainerSamples](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/itos/coui/demo/DemoContainerSamples.kt)，库入口见 [CouiAdaptiveLayout](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/layout/CouiAdaptiveLayout.kt)。
