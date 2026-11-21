# 🎯分数环

`ScanRing` 是 ColorOS 17 手机管家首页的安全分数环，对应 `com.oplus.phonemanager.common.view.ScanCircleView`：弧长按分数换算，弧本身不是单色，而是沿角度分布的五段角度渐变；扫描期间再叠一圈旋转的镜像色带。

原厂没有固定缺口。首页 78 分截图里出现在右下方的空档是「未填充的分数差额」——`360° × (1 − score/100)`，随分数变化。这一点与[进度与占位](progress.md#分数环)里用固定 `gapDegrees` 拼出来的近似示例不同，后者只对某个分数点成立。

## 最小调用

~~~kotlin
ScanRing(
    score = 100,
    scanning = true,
) {
    Text(text = "100", kind = CouiTextKind.Display)
}
~~~

弧从 6 点方向开始顺时针绘制，`score = 100` 时闭合整圈；`content` 在环心居中，用于分数与单位。

## 参数

- `score`：0..100，越界先夹紧；弧长为 `score / 100 × 360°`。
- `scanning`：扫描态开关。为真时把五阶色板按 `[0,1,2,2,3,2,1,0]` 镜像成八阶，绕环 5000ms 一圈匀速旋转；置为假时按剩余角度补完当前一圈再归零，时长按剩余角度换算并夹在 500–5000ms，曲线为 `PathInterpolator(0.17, 0.17, 0.1, 0.1)`。
- `redThreshold`：转红阈值，默认 70。低于阈值转红是立即的，回到绿色按 600ms + 300ms 延迟逐段插值颜色与停位（原厂 `colorAnimation` 只在回绿时插值）。
- `animateScore`：分数变化是否用 2000ms 缓动。关掉时分数直接落位，配色也不做过渡，对应原厂 `setScoreNoAnimation`。
- `greenPalette` / `redPalette`：两套 `CouiScanRingPalette`。默认取原厂资源：绿 `#40DC30 → #00D616 → #40DC30`（停位 `.2 / .45 / .7`）、红 `#FF851B → #FC701F → #FF851B`（停位 `.2 / .5 / .85`）。两套的停位数量必须一致，首尾颜色必须相同，否则旋转时会出现接缝。
- `onScoreChange`：显示分数变化时回调，2 秒动画内按整数变化触发，不是逐帧。
- `onScoreAnimationEnd`：分数动画结束回调；目标与当前一致时不会触发（原厂 same-score 分支直接 return）。
- `size` / `strokeWidth`：默认 172dp 与 22dp，取自 `main_circle_width` 与 `circle_stroke_width`。

原厂在 `setScore()` 结束时会强制停转（`stopScanRotationImmediately`）。本组件不读分数动画内部状态来改 `scanning`，需要同效果时在 `onScoreAnimationEnd` 里把 `scanning` 置为假，由补完分支收尾。

## 与原厂的差异

- 外发光底图、中心图标与环外扩散波纹是 `main_circle_*.webp` 位图：`ScanCircleWaveView` 逐帧把同一张图从 `0.8` 缩放到 `1.3`，alpha 在最后 600ms 内从 `1` 降到 `0.2`，每 1000ms 起一波。本组件只画环，位图部分留给宿主背景层；把波纹做成矢量会改变观感，没有按近似处理。
- 原厂在渲染层用 `canvas.setMatrix(rotate 90°)` 同时旋转弧与渐变，本组件把 90° 折进渐变的停位相位，弧的起始角显式写成 90°，绘制结果一致。
- 原厂的绿→红过渡还交叉淡化 `main_circle_bg_*.webp` 背景图（`changeBackgroundIfNeed`），无位图时该分支无对应实现。
- 原厂 `setSelfRotationRepeatCount()` 可限制补完动画的重复次数，本组件固定补完一次。

## Demo

完整示例见 [DemoProgressSamples.kt](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/demo/src/main/java/com/cycling/coui/demo/DemoProgressSamples.kt)：点击在满分与 52 分之间切换，`onScoreChange` 驱动环心读数。

源码见 [ScanRing](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/progress/ScanRing.kt)、[CouiScanRingDefaults](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/progress/CouiScanRingDefaults.kt)。
