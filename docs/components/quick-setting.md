# 🎛️控制中心磁贴

控制中心的两个控件：

- `QuickSettingTile`：快捷开关磁贴，分小磁贴（62dp 正圆 + 下方标签）与大磁贴（138×62dp 胶囊，标签在磁贴内）。
- `LevelSlider`：竖排档位条，62×138dp 胶囊，底部按比例填充，图标固定在底部。

它们与 [Slider](slider.md) 不是同一个控件：`Slider` 是横向细轨道加圆钮，带刻度吸附、触感与 RTL 镜像；档位条是整块胶囊，填充量即当前值，竖直方向不参与 RTL 镜像。

## 网格

数值取自 ColorOS 17 控制中心的真机布局树（一加 Ace 5，1264×2780 @560dpi）：

| 项 | 值 | 依据 |
| --- | --- | --- |
| 列数 | 4 | `[124,1554][341,1771]` 等四列 |
| 单元 | 62dp | 217px |
| 间距 | 14dp | 49px |
| 大磁贴 | 138 × 62dp | 483×217px，跨 2 列 |
| 档位条 | 62 × 138dp | `vertical_seekbar` 217×483px，跨 2 行 |
| 小磁贴图标 | 28dp | `qs_icon` 98×98px |
| 大磁贴图标底盘 | 42dp | `oplus_qs_tile_icon_bg` 147×147px |
| 大磁贴图标 | 24dp | `qs_icon` 84×84px |
| 小磁贴标签 | 10sp medium，距磁贴 4dp，宽 76dp | `oplus_qs_tile_text_size` 10dp + `couiTextAppearanceTag`；容器宽 266px = 76dp（单元 62 + 间距 14），长标签可使用相邻间距 |
| 大磁贴标签 | 14sp medium，距底盘 6dp | `couiTextAppearanceButton` 14sp medium；文字行高 58px ≈ 16.8dp；左侧留白 10dp、右缘 15dp |

138 = 2 × 62 + 14，所以大磁贴正好是两列加一个间距。网格排版由宿主用 `Row` + `CouiQuickSettingDefaults.gap` 组合，库不提供跨行跨列的网格容器——原厂小磁贴的标签在磁贴外、大磁贴的标签在磁贴内，两者行高规则不同（小磁贴行距 92dp，大磁贴行高 62dp），硬做统一网格会出错。

**轮廓不是卡片圆角**：小磁贴是正圆，大磁贴和档位条是整条胶囊（半径 = 短边一半）。`CouiQuickSettingDefaults.shape` 与 `CouiLevelSliderDefaults.shape` 都使用 `CircleShape`；在 138×62、62×138 的矩形上 `CircleShape` 就是胶囊，不需要另写 RoundedCornerShape。

SystemUI 里的 `status_bar_qs_tile_bg_color_active`（`#1875f5`）和 `status_bar_qs_tile_bg_color_inactive`（浅色 `#ffffff`、深色 `#33ffffff`）服务于 11dp 圆角的 classic 卡片，不是全展开控制中心的圆形/胶囊磁贴。后者开启态取真机截图的白色，关闭态取 `#33ffffff` 叠加背景模糊，两者不能直接套用 classic 的颜色资源。

## 最小调用

~~~kotlin
Row(horizontalArrangement = Arrangement.spacedBy(CouiQuickSettingDefaults.gap)) {
    QuickSettingTile(
        label = "WLAN",
        icon = { Icon(CouiIcons.Filled.CloudLink, null, Modifier.size(24.dp)) },
        checked = wlan,
        onToggle = { wlan = it },
        size = CouiQuickSettingSize.Large,
    )
    QuickSettingTile(
        label = "蓝牙",
        icon = { Icon(CouiIcons.Filled.Bluetooth, null, Modifier.size(24.dp)) },
        checked = bluetooth,
        onToggle = { bluetooth = it },
        size = CouiQuickSettingSize.Large,
    )
}

LevelSlider(value = brightness, onValueChange = { brightness = it },
    icon = { Icon(CouiIcons.Filled.DarkLight, null, Modifier.size(24.dp)) })
~~~

## 参数

`QuickSettingTile`

- `label` / `icon`：必填，小磁贴标签 10sp medium、大磁贴 14sp medium；图标由调用方提供，库不做 tint。小磁贴图标传 28dp，大磁贴传 24dp。
- `checked` / `onToggle`：受控开关，整卡（含小磁贴下方标签）承担点击并暴露 `Role.Switch`。
- `size`：`Small` 或 `Large`，只改尺寸与标签位置，不改交互。
- `containerColor`：关闭态底色。深色默认 `#33ffffff`（与 `coui_color_card_pressed_dark` 一致），浅色默认 `colors.card`；传 `Color.Transparent` 后由宿主用 `Modifier.couiFloatingSurface` 包裹接入玻璃，`shape` 需两处一致。开启态要玻璃时同时把 `checkedContainerColor` 传 `Color.Transparent`。
- `checkedContainerColor`：开启态底色，默认 `Color.White`。原厂开启态是白色磁贴 + 强调色图标，不是强调色填充；宿主可通过该参数覆盖。
- `iconContainerColor`：大磁贴图标底盘颜色。默认关闭态 `20%` 白色（真机采样约 +18% 亮度），开启态不绘制底盘；传 `Color.Transparent` 可关闭底盘。
- `shape`：默认 `CircleShape`，在两种尺寸上分别得到正圆与胶囊。

`LevelSlider`

- `value` / `onValueChange`：0f..1f，按下与拖动都直接给出对应位置的值。
- `fillColor`：填充色。深色下默认 `Color.White`（原厂控制中心），浅色下默认强调色，避免白色填充与浅色轨道混在一起。
- `containerColor` / `shape`：默认卡片色与胶囊；材质由宿主包裹。图标槽 36dp、距底 10dp，图标本身建议 24dp。

## 编辑态形变与重排

SystemUI 在编辑态用 `DeformationFrameLayout` 让磁贴在 `1×1` 与 `2×1` 之间形变，周围磁贴按 `SpanSize` 重排，尺寸与位置走弹簧过渡，选中的磁贴叠一圈描边并在右缘给出竖向拖动把手。`DeformationTileGrid` 覆盖这一层。

~~~kotlin
var spans by remember { mutableStateOf(List(6) { CouiDeformationSpan.OneByOne }) }
DeformationTileGrid(
    items = labels.indices.toList(),
    spanOf = { spans[it] },
    editing = editing,
    onSpanChange = { index, span -> spans = spans.toMutableList().also { it[index] = span } },
) { index, span, isEditing ->
    // 宿主绘制磁贴内容，span 为当前跨度，isEditing 与网格的 editing 一致
}
~~~

### 尺寸与参数

| 项 | 值 | 依据 |
| --- | --- | --- |
| 列数 | 4 | `CouiDeformationTileDefaults.columns` |
| 间距 | 8dp | `gap` |
| 圆角 | 20dp | `qs_tile_corner_radius_default` |
| 编辑态描边 | 2dp；按住把手时 4dp | `qs_edit_tile_stroke_width_default` / `_press` |
| 把手 | 12 × 28dp、6dp 圆角 | `DragBarView` 的拖拽条 |
| 切跨度阈值 | 一格宽度的 0.35 | `spanToggleFraction` |
| 弹簧 | response 0.35、无回弹 | `reshapeResponse` |

- `editing`：由宿主控制（长按进入、退出编辑）。为真时逐块叠描边并显示把手。
- `spanOf` / `onSpanChange`：读取与提交跨度；磁贴内容仍由 `tile` 绘制，第三个参数就是 `editing`。
- 重排规则：按列顺序落位，`2×1` 放不下当前行剩余列时整体换行；行高等于单元高度，网格高度随行数弹簧过渡。

### 与原厂的差异

1. `DeformationFrameLayout` 属于 SystemUI，依赖 `CellCalculatorManager`、`SpanCache` 与 `springtransition`（`BoundsTransition`、`DrawableStrokeWidthProperty`）等私有类型；移植侧用 Compose 弹簧与 `BoxWithConstraints` 重算单元格代替，不是同一套测量与过渡实现。
2. 原厂的跨行跨度（1×2、2×2）与「拖到其他磁贴位置交换」未移植，只提供 `1×1` / `2×1` 与拖动切换跨度。
3. 原厂描边由 `StrokeDrawable` 向外扩一圈绘制、把手是 `DragBarView` 自绘；移植侧描边内缩在磁贴内，把手是独立的可拖动胶囊。
4. 原厂高亮描边随选中态切换（`highlightDragBarAndStroke`）；移植侧只用按住把手时的 2dp→4dp 过渡表达。
5. 原厂重排还联动 QS 面板的展开与分页；移植侧只做本网格内部的重排。

## 未覆盖

原厂磁贴还有长按进入设置、二级页展开、状态副标题、不可用态与跨页轮播；档位条还有自动亮度/静音的点按切换图标。这些属于 SystemUI 的交互外壳，不在组件范围内。大磁贴开启态、浅色主题、RTL 与真机玻璃叠加尚未验收。

源码见 [QuickSettingTile](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/quicksetting/QuickSettingTile.kt)、[LevelSlider](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/quicksetting/LevelSlider.kt) 与 [DeformationTileGrid](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/cycling/coui/quicksetting/DeformationTileGrid.kt)。
