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

## 未覆盖

原厂磁贴还有长按进入设置、二级页展开、状态副标题、不可用态与跨页轮播；档位条还有自动亮度/静音的点按切换图标。这些属于 SystemUI 的交互外壳，不在组件范围内。大磁贴开启态、浅色主题、RTL 与真机玻璃叠加尚未验收。

源码见 [QuickSettingTile](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/quicksetting/QuickSettingTile.kt) 与 [LevelSlider](https://github.com/mariamjensen42-glitch/COUI-Kit/blob/main/coui/src/main/java/com/itos/coui/quicksetting/LevelSlider.kt)。
