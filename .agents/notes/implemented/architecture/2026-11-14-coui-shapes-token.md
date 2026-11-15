# Agent Note: 轮廓 token 集中到主题，组件默认读 `CouiTheme.shapes`

Status: implemented

## Problem

轮廓此前散落在三十多个组件的 `Defaults` 里，每处都是独立的 `CouiShape(...)` 字面量：卡片 24dp、菜单 28dp、弹窗 45dp、复选框 4dp、头像 999dp……同一套原厂刻度被复制了几十遍。

后果有三个：

- 宿主想整体换成更方的外观（例如嵌进别的设计体系），必须逐个组件传 `shape`，漏一个就出现两种圆角并存的页面；
- 没有任何一处能回答"这个库的圆角有哪几档"，使用方只能翻组件页反查；
- 嵌套 `CouiTheme`（局部换配色）与轮廓没有关系，但代码里也读不到这个区分，改动时容易把两者绑死。

## Decision

新增 `com.itos.coui.shape.CouiShapes`，作为组件默认读取的轮廓集合，由 `CouiTheme` 通过 `LocalCouiShapes` 提供。

`CouiShapes` 是 `@Immutable data class`，十一档，取值来自原厂 `coui_round_corner_*` 刻度：

| 字段 | 原生值 | 使用者 |
| --- | --- | --- |
| `extraSmall` | `CouiShape(4.dp)` | 复选框 |
| `small` | `CouiShape(8.dp)` | 占位块 |
| `medium` | `CouiShape(12.dp)` | 气泡 |
| `large` | `CouiShape(16.dp)` | FAB 圆角方形形态 |
| `extraLarge` | `CouiShape(20.dp)` | 模式选择卡 |
| `card` | `CouiShape(24.dp, 3f)` | 分组卡片、操作卡、通知卡、快捷动作卡 |
| `menu` | `CouiShape(28.dp)` | 菜单及其条目 |
| `panel` | 上两角 `32.dp` 的 `RoundedCornerShape` | 底部面板 |
| `dialog` | `CouiShape(45.dp, 3f)` | 弹窗 |
| `capsule` | `CouiCapsuleShape` | 按钮、开关、滑块、搜索栏、底栏、顶栏胶囊按钮 |
| `circle` | `CouiShape(999.dp, 2f)` | 头像、圆形 FAB、顶栏圆形按钮 |

接线分三层，职责不重叠：

- `CouiTheme.shapes` 是唯一读取入口，`fun CouiTheme(...)` 新增 `shapes: CouiShapes = LocalCouiShapes.current`。**默认沿用外层 token**，嵌套主题只改配色不重置轮廓。
- 组件参数默认值写成 `CouiTheme.shapes.xxx`，显式传参仍然优先。
- 各组件 `Defaults` 保留同值基线（`val shape: Shape = CouiShapes.Default.card` 等），供文档引用、`copy` 覆盖和没有主题的独立使用。`Defaults` 与 token 是同一份值的两个出口，不是两套默认。

无 `shape` 参数的组件（`Tooltip`、`BottomSheet`、`SearchBar`、`ChoiceCardGroup`、`DockBar`、`SegmentedControl` 容器）在函数体内 `val shape = CouiTheme.shapes.xxx` 读取一次，不改签名。

`CouiShapes` 不区分深浅色：原厂圆角刻度只有一套，深浅差异只体现在颜色和材质上，因此不提供 `shapesFor(dark)`。

## 不纳入 token 的轮廓

以下留原样，理由各自成立：

- **输入框 10dp、轻提示 12.67dp**：取值来自组件私有资源（`coui_textinput_corner_radius`、`coui_snack_bar_*`），不是通用圆角刻度。收进 token 会把私有资源语义提升成公共契约。
- **控制中心磁贴与档位条（`CircleShape`）**：正圆与胶囊是这两个组件的几何本身（62dp 正方形、138×62），换圆角等于换组件形态。
- **`CouiDialogButton` 的 999dp**：弹窗内部按钮的自动圆角，随按钮尺寸走，不是可独立替换的外观。
- **`SegmentedControl.indicatorShape` 的 `CouiShape(999.dp, 2f)`**：`SegmentIndicatorDrawing.update(next: Size, shape: CouiShape, …)` 与 `drawSegmentIndicator(rect, containerHeight, shape: CouiShape, …)` 的签名要求具体 `CouiShape` 才能复用原厂轮廓绘制，换成任意 `Shape` 会丢掉绘制路径。
- **`Switch` 滑块轮廓**：`ThumbOutlineCache` 直接调 `CouiCapsuleShape.createOutline`，与原厂一致地固定胶囊，不随轨道 token 变化。

## Alternatives considered

- **Material `Shapes` 式的 `extraSmall/small/medium/large/extraLarge` 五档命名** — 这是业界最容易被认出的形状 token 形状，宿主迁过来几乎零学习成本。但 COUI 的圆角档位是十一档且带语义（卡片、菜单、面板、弹窗、胶囊、圆形），压成五档必须做有损合并：24dp 卡片和 28dp 菜单会挤进同一档，45dp 弹窗无处安放。语义命名保留了每档的用途，代价是不与 M3 对齐。
- **把 `shape` 参数从所有组件删掉，只留主题 token** — 表面最干净，调用方也不必再想轮廓。但组件级覆盖是现有契约（`Fab` 圆角方形、`NotificationCard` 与 `couiFloatingSurface` 必须同 shape），删参数会逼宿主去开局部 `CouiTheme` 才能改一张卡，比传一个参数重得多。
- **`Defaults` 改成直接读 `CouiTheme.shapes` 的 `@Composable` getter** — 能保证只有一个来源。但 `Defaults` 被大量非组合上下文引用（文档、`copy` 调用、`remember` 的 key），改成 `@Composable` 会让这些用法全部失效，也破坏了"无状态预设归 Defaults"的既有约定。
- **给 `CouiShapes` 加深浅色分支（`shapesFor(dark)`）** — 与颜色 token 对称，看起来更整齐。但原厂圆角刻度只有一套，深浅模式没有不同的圆角值，加了就是没有消费者的开关。

## Consequences

- **收益**：宿主改一行 `CouiTheme(shapes = …)` 就能整体换轮廓；"这个库有几档圆角"有了唯一答案；嵌套主题的语义变清楚了——配色和轮廓可以各自独立替换。
- **代价与已知上限**：轮廓值现在有两处同值声明（token 与 `Defaults`），新增组件时必须同时更新，否则显式覆盖与主题替换会分叉。`choicecard/ThumbnailChoiceCard.kt` 的 `navigationShape = CouiShape(20.dp, 3f)` 暂未接入 `extraLarge`——它的权重是 3，与 `extraLarge` 的默认权重不同，强行合并会改变现有外观，留作后续单独判断。
- 触发重访的信号：出现第二个需要"同一档圆角、不同权重"的组件时，说明 token 需要拆出权重维度，而不是继续在组件里写字面量。

## Verification

`rg -n 'CouiShape\(' coui/src/main/java` 只剩 4 处组件私有轮廓（`CouiCheckboxDefaults.borderShape`、`ThumbnailChoiceCard.navigationShape`、`CouiDialogButton` 内部按钮、`SegmentedControl.indicatorShape`）与 `CouiShape.kt`、`CouiShapes.kt` 自身。`rg -n 'CouiTheme.shapes' coui/src/main/java` 覆盖全部有 `shape` 参数的组件默认值。
