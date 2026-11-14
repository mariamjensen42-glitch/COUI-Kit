# Agent Note: 线性进度、缺口环、快捷动作网格与绿色预设

Status: implemented

## Problem

按 ColorOS 17「手机管家」真机截图对照，页面主体由四类元素构成，而库当时只有环形进度与骨架占位：

- 电池页的电量条、存储空间的多色构成条，都是横向胶囊进度，库内没有线性进度入口。
- 清理存储页的 62.0MB 环与手机管家首页的 78 分环，尺寸远大于 40dp，且带缺口与外发光，中心有读数。原 `CircularProgress` 把尺寸写死为 40dp/30dp，没有缺口、光晕与中心内容。
- 实用工具页是两列彩色图标入口卡片，`Item` 是横向行式带 `trailing`，没有网格形态。
- 页面强调色为绿色，`CouiPresets` 只有蓝色。

## Decision

**线性进度** `LinearProgress` / `SegmentedProgress`：

- 轨道与进度共用一次形状轮廓裁剪，进度端部随容器收圆，不用矩形叠加再补角。
- 默认高度 8dp，大号 12dp（`linearHeight` / `linearLargeHeight`）；形状取 `CouiCapsuleShape`，与库内胶囊族一致。
- 取色规则与环形一致：省略 `color` 取主题强调色，省略 `trackColor` 取分隔线色。
- 分段版按占比从左向右累加，段间无间隙，累计超宽后不再绘制，尾部保留轨道色。

**环形进度扩展**（不改变原有 40dp/30dp 默认行为）：

- 新增 `size`、`gapDegrees`、`startAngleDegrees`、`glow`、`content` 五个参数，均追加在 `strokeWidth` 之后。
- 缺口位于起点之前，不参与比例换算；`gapDegrees` 夹紧到 350°，保证轨道至少保留一段可见弧。
- `glow` 用三层同色加宽弧叠加并逐层衰减透明度，不使用 `BlurMaskFilter`（硬件加速 Canvas 不支持）；环半径按 `circularGlowRingScale`（0.82）内收以容纳光晕，因此 `size` 在开启光晕时表示含光晕的整体尺寸。
- `content` 在 `Box` 中心绘制，不参与测量。

**快捷动作网格** `QuickActionGrid` / `CouiQuickAction`：

- 两列等分，末行用 `Spacer(weight)` 补齐，卡片宽度不随标题变化。
- 沿用 `CouiPageDefaults.shape`（24dp 超椭圆）与 `CouiPageColors.card`，与 `Group` 卡片同轮廓同底色。
- 图标由调用方提供，库不做 tint：截图中的入口是彩色图标资源，不是单色矢量。
- 整卡承担点击，`enabled` 只降低内容不透明度并保留占位。

**绿色预设** `CouiPresets.GreenLight` / `GreenDark` / `green(dark)`：

- 只替换 `accent`、`accentOnSurface`、`onAccent`，中性表面、卡片与页面底色沿用对应深浅模式的原生预设。
- 命名表达「页面主色」，不声称原厂存在名为 Green 的主题。

**排版**新增 `CouiTextKind.Display`（48sp、`sans-serif-semibold`、700），供电量百分比、健康分数这类展示型读数使用，比 `HomeTitle` 更大且不承担标题语义。

## Evidence

材料只有真机截图 6 张（手机管家 19:45–19:46 截取，含电池、实用工具、清理存储、手机管家首页），**没有取得对应版本的 APK 资源**。因此本轮的参数来源分三类：

| 项 | 依据 | 性质 |
| --- | --- | --- |
| 线性进度 8dp 高、胶囊端部 | 截图中电量条与存储构成条的实际比例 | 按截图推断，未经资源核对 |
| 分段构成条的段序与配色 | 截图存储空间条（绿 / 紫 / 橙 / 浅灰 / 轨道） | 按截图推断，宿主可覆盖 |
| 环形缺口 70°、外发光三层 | 截图 78 分环与 62.0MB 环 | 原厂环形进度无此分支，属项目扩展 |
| 快捷网格 24dp 圆角、56dp 最小高 | 与 `Group` 现有卡片同轮廓；高度按截图比例 | 轮廓复用既有决定，高度为推断 |
| 绿色强调色 `#3DDC5A` / `#00BD13` | 截图目视取色 | 未经取色器与资源核对 |

未取色的原因：本机环境没有 PIL / numpy / ImageMagick，且无外网可安装，无法对 JPEG 做像素采样。颜色值需要在拿到 APK 资源或用取色器复核后修正。

既有约束仍然有效：`CircularProgress` 的 40dp/5dp、30dp/3dp、不确定态 800ms / 270° 弧长沿用上一轮 APK 结论（见 `2026-11-12-button-progress-toast-badge.md`），本轮只做增量参数，不改这些默认值。

## Alternatives considered

- 只给 `CircularProgress` 内嵌一个大号分支，不扩公开参数：缺口、光晕和环心内容会变成无法组合的硬编码分支，且原有 40dp/30dp 默认行为仍要保留。
- 线性进度用矩形叠加再单独补圆角：轨道与进度端部会在不同宽度下出现接缝，复用一次轮廓裁剪更稳。
- 为快捷动作网格新建独立组件：它只需要两列等分和现成的 Group 轮廓，独立组件会复制同一套卡片装配。
- 绿色预设复制整份色板：中性色和卡片色没有变化，只替换强调色相关角色即可，复制会制造两套需要同步的色值。

## Consequences

- `CircularProgress` 新增五个带默认值的参数，原有调用无需迁移；但内部由单个 `Canvas` 改为 `Box + Canvas(matchParentSize)`，调用方 `Modifier.size` 的覆盖行为不变。
- `glow` 开启时 `size` 的语义变为含光晕的整体尺寸，这一点写进了 KDoc 与使用页，属于需要使用者知晓的差异。
- 线性进度与快捷网格尚无原厂资源级依据，若后续取得 APK，应优先复核高度、圆角与绿色色值。
- 未执行 Gradle 构建与真机验收；本轮只做静态检查。
