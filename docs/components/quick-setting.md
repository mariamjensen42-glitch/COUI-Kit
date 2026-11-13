# 🎛️控制中心磁贴

控制中心的两个控件：

- `QuickSettingTile`：快捷开关磁贴，分小磁贴（62dp 方块 + 下方标签）与大磁贴（138×62dp 横条，标签在磁贴内）。
- `LevelSlider`：竖排档位条，62×138dp，底部按比例填充，图标固定在底部。

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

138 = 2 × 62 + 14，所以大磁贴正好是两列加一个间距。网格排版由宿主用 `Row` + `CouiQuickSettingDefaults.gap` 组合，库不提供跨行跨列的网格容器——原厂小磁贴的标签在磁贴外、大磁贴的标签在磁贴内，两者行高规则不同（小磁贴行距 92dp，大磁贴行高 62dp），硬做统一网格会出错。

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

- `label` / `icon`：必填，图标由调用方提供，库不做 tint。
- `checked` / `onToggle`：受控开关，整卡承担点击并暴露 `Role.Switch`。
- `size`：`Small` 或 `Large`，只改尺寸与标签位置，不改交互。
- `containerColor`：默认按 `checked` 取强调色或卡片色；传 `Color.Transparent` 后由宿主用 `Modifier.couiFloatingSurface` 包裹接入玻璃，`shape` 需两处一致。

`LevelSlider`

- `value` / `onValueChange`：0f..1f，按下与拖动都直接给出对应位置的值。
- `fillColor`：填充色，默认取强调色。原厂深色控制中心用**白色填充**，需要还原时显式传 `Color.White`；但白色在浅色主题的白底轨道上不可见，浅色下不要用。
- `containerColor` / `shape`：同磁贴，材质由宿主包裹。

## 未覆盖

原厂磁贴还有长按进入设置、二级页展开、状态副标题、不可用态与跨页轮播；档位条还有自动亮度/静音的点按切换图标。这些属于 SystemUI 的交互外壳，不在组件范围内。真机叠加、浅色模式与 RTL 尚未验收。

源码见 [QuickSettingTile](../../coui/src/main/java/com/itos/coui/quicksetting/QuickSettingTile.kt) 与 [LevelSlider](../../coui/src/main/java/com/itos/coui/quicksetting/LevelSlider.kt)。
