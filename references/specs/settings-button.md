# Button 原厂规格证据（com.android.settings）

> 由 `tools/extract_component_spec.py` 只读解析 `decoded/res` 自动生成，请勿手改。
> 本文件是**原厂证据快照**，不是组件文档；实现常量以源码与 KDoc 为唯一维护位置。

## 来源

| 项 | 值 |
| --- | --- |
| 包名 | com.android.settings |
| 版本 | 17.0.0 |
| 构建 | PKG110_17.0.0.105(CN01)，ColorOS V17.0.0，Android 17（API 37） |
| 设备 | OnePlus Ace 5（PKG110），serial 2ac81f7b |
| SHA-256 | `de544baddb0e0326345bc71070724b55fe21f5d3abc87b759c93871db9e9e407` |
| 解析配置 | values + values-vN(<=sdk) + values-night（目标 API 37） |
| 匹配规则 | `button\|btn` |
| 样式前缀 | `Widget.COUI.Button` |
| 排除规则 | `^(mtrl_\|btn_(radio\|checkbox)_\|abc_\|settingslib_\|privacy_)` |

计数：{"styles": 21, "theme_attrs": 390, "theme_attrs_coui": 187, "theme_attrs_used": 19, "state_selectors": 8, "colors": 35, "dimens": 12, "anims": 4, "interpolators": 0, "unresolved": 0, "related": {"styles": 405, "colors": 149, "dimens": 488}}

> 主题属性口径：本表按设备实际生效的配置叠加（`values` + `values-vN` + `values-night` 的颜色覆盖），
> 统计全部 item（含 `android:*`）；`DESIGN-TOKENS.md` 的 187 只数 `values` 下 `coui` 开头的属性，
> 两者不是同一口径，不要直接比大小。

## 1. 样式继承链与合并后的属性

每个属性给出原值、浅色与深色解析值；⚠ 表示引用链未闭合，需人工补证据。

### `Widget.COUI.Button`

- parent: `@style/Base.Widget.AppCompat.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button`
- 来源：`res/values/styles.xml:14239`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14240 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14242 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14241 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14249 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14250 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Dark`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Dark`
- 来源：`res/values/styles.xml:14254`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14240 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14255 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14241 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14249 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14250 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Large`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large`
- 来源：`res/values/styles.xml:14257`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14265 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14264 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14242 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14241 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14271 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14272 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14273 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Large.ButtonNew`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew`
- 来源：`res/values/styles.xml:14276`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:14279 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:14281 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:14280 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:14278 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14242 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14277 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `buttonType` | `primary` | `primary` | `primary` | res/values/styles.xml:14282 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14271 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14272 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14273 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Large.ButtonNew.AiPayment`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew.AiPayment`
- 来源：`res/values/styles.xml:14284`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14265 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14264 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_ai_payment_text_color` | `#ffffff` | `#e6000000` | res/values/styles.xml:14285 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14241 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:14286 |
| `animType` | `1` | `1` | `1` | res/values/styles.xml:14287 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `buttonType` | `aiPayment` | `aiPayment` | `aiPayment` | res/values/styles.xml:14288 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorAiPaymentPrimaryDisabled` | `#4d001d38` | `#4ddce7ff` | res/values/styles.xml:14289 |
| `drawableColor` | `?couiColorAiPaymentPrimary` | `#001d38` | `#dce7ff` | res/values/styles.xml:14290 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:14291 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14273 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:14292 |

### `Widget.COUI.Button.Large.ButtonNew.AiPaymentSecondary`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary → Widget.COUI.Button.Large.ButtonNew.AiPaymentSecondary`
- 来源：`res/values/styles.xml:14294`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:14279 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:14281 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:14280 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:14278 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_ai_payment_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14295 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14277 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `buttonType` | `aiPaymentSecondary` | `aiPaymentSecondary` | `aiPaymentSecondary` | res/values/styles.xml:14296 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorAiPaymentSecondaryDisabled` | `#14000000` | `#1affffff` | res/values/styles.xml:14297 |
| `drawableColor` | `?couiColorAiPaymentSecondary` | `#14000000` | `#1affffff` | res/values/styles.xml:14298 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:14299 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14273 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:14300 |

### `Widget.COUI.Button.Large.ButtonNew.Secondary`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary`
- 来源：`res/values/styles.xml:14302`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:14279 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:14281 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:14280 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:14278 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14303 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14277 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:14304 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14305 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14306 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14273 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary → Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin`
- 来源：`res/values/styles.xml:14308`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:14279 |
| `android:layout_marginEnd` | `0.0dp` | `0.0dp` | `0.0dp` | res/values/styles.xml:14310 |
| `android:layout_marginStart` | `0.0dp` | `0.0dp` | `0.0dp` | res/values/styles.xml:14309 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:14278 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14303 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14277 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:14304 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14305 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14306 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14273 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `useMaterialEffect` | `forceDisable` | `forceDisable` | `forceDisable` | res/values/styles.xml:14311 |

### `Widget.COUI.Button.Large.Share`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.Share`
- 来源：`res/values/styles.xml:14313`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14315 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14319 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14318 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14320 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14316 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14317 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14314 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14242 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:14241 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14321 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14322 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14323 |
| `drawableColor` | `@color/share_button_color` | `#0066ff` | `#0066ff` | res/values/styles.xml:14324 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14325 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14326 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Large.TinyFull`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull`
- 来源：`res/values/styles.xml:14328`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14332 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:14336 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:14335 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:14331 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:14333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:14334 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14330 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:14329 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:14337 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:14338 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14272 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14339 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Large.TinyFull.Secondary`

- parent: `@style/Widget.COUI.Button.Large.TinyFull`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull → Widget.COUI.Button.Large.TinyFull.Secondary`
- 来源：`res/values/styles.xml:14341`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14259 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14332 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:14336 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:14335 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:14331 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:14268 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:14333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:14267 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:14266 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14263 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14260 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14262 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:14261 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:14334 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:14258 |
| `android:textColor` | `@color/coui_btn_borderless_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14342 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:14329 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14269 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14270 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:14337 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:14343 |
| `drawableColor` | `@color/coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:14344 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14339 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:14274 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small`
- 来源：`res/values/styles.xml:14346`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14242 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:14348 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14249 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14357 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small.ButtonNew`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew`
- 来源：`res/values/styles.xml:14360`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14362 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14361 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14242 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:14348 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `buttonType` | `primary` | `primary` | `primary` | res/values/styles.xml:14363 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:14249 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14357 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small.ButtonNew.AiPayment`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.AiPayment`
- 来源：`res/values/styles.xml:14365`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14362 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14361 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_ai_payment_text_color` | `#ffffff` | `#e6000000` | res/values/styles.xml:14366 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:14348 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:14367 |
| `animType` | `1` | `1` | `1` | res/values/styles.xml:14368 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `buttonType` | `aiPayment` | `aiPayment` | `aiPayment` | res/values/styles.xml:14369 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorAiPaymentPrimaryDisabled` | `#4d001d38` | `#4ddce7ff` | res/values/styles.xml:14370 |
| `drawableColor` | `?couiColorAiPaymentPrimary` | `#001d38` | `#dce7ff` | res/values/styles.xml:14371 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:14372 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:14373 |

### `Widget.COUI.Button.Small.ButtonNew.AiPaymentSecondary`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Secondary → Widget.COUI.Button.Small.ButtonNew.AiPaymentSecondary`
- 来源：`res/values/styles.xml:14375`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14362 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14361 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_ai_payment_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14376 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:14348 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `buttonType` | `aiPaymentSecondary` | `aiPaymentSecondary` | `aiPaymentSecondary` | res/values/styles.xml:14377 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorAiPaymentSecondaryDisabled` | `#14000000` | `#1affffff` | res/values/styles.xml:14378 |
| `drawableColor` | `?couiColorAiPaymentSecondary` | `#14000000` | `#1affffff` | res/values/styles.xml:14379 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:14380 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:14381 |

### `Widget.COUI.Button.Small.ButtonNew.Secondary`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Secondary`
- 来源：`res/values/styles.xml:14383`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14362 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14361 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14384 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:14348 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:14385 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14386 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14387 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small.ButtonNew.Translate`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Translate`
- 来源：`res/values/styles.xml:14389`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14362 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:14361 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_tiny_translate_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14390 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:14348 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `buttonType` | `transparent` | `transparent` | `transparent` | res/values/styles.xml:14391 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:14248 |
| `disabledColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:14392 |
| `drawableColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:14393 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `pressColor` | `16` | `16` | `16` | res/values/styles.xml:14394 |

### `Widget.COUI.Button.Small.TinySmall`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall`
- 来源：`res/values/styles.xml:14396`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14398 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:14397 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:14399 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:14400 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14357 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small.TinySmall.HalfColor`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.HalfColor`
- 来源：`res/values/styles.xml:14402`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:14403 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:14397 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:14399 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:14404 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:14405 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small.TinySmall.Secondary`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.Secondary`
- 来源：`res/values/styles.xml:14407`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_borderless_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:14408 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:14397 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:14399 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:14409 |
| `drawableColor` | `@color/coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:14410 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |

### `Widget.COUI.Button.Small.TinySmall.Translate`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.Translate`
- 来源：`res/values/styles.xml:14412`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:14243 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:14244 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:14354 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:14353 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14352 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14349 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:14351 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:14350 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:14245 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:14246 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:14247 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:14347 |
| `android:textColor` | `@color/coui_btn_tiny_translate_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:14413 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:14397 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:14355 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:14356 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:14399 |
| `disabledColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:14414 |
| `drawableColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:14415 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:14358 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:14251 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:14252 |
| `pressColor` | `16` | `16` | `16` | res/values/styles.xml:14416 |

## 2. 状态色（ColorStateList）

状态按 selector 的 item 顺序归并为 disabled / pressed / selected / checked / focused / default。

### `coui_btn_ai_payment_secondary_text_color`

来源：`res/color/coui_btn_ai_payment_secondary_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiColorAiPaymentSecondaryTextDisabled` | `#42000000` | `#42ffffff` |
| default | `?couiColorAiPaymentSecondaryText` | `#e6000000` | `#e6ffffff` |

### `coui_btn_ai_payment_text_color`

来源：`res/color/coui_btn_ai_payment_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiColorAiPaymentPrimaryTextDisabled` | `#8affffff` | `#42000000` |
| default | `?couiColorAiPaymentPrimaryText` | `#ffffff` | `#e6000000` |

### `coui_btn_borderless_text_color`

来源：`res/color/coui_btn_borderless_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `@color/coui_color_label_tertiary` | `#42000000` | `#4dffffff` |
| pressed | `?couiColorPrimaryNeutral` | `#e6000000` | `#e6ffffff` |
| selected | `?couiColorPrimaryNeutral` | `#e6000000` | `#e6ffffff` |
| default | `?couiColorPrimaryNeutral` | `#e6000000` | `#e6ffffff` |

### `coui_btn_default_text_color`

来源：`res/color/coui_btn_default_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiBtnDefaultTextColorDisable` | `#8affffff` | `#28ffffff` |
| pressed | `?couiColorLabelOnColor` | `#ffffff` | `#ffffff` |
| selected | `?couiColorLabelOnColor` | `#ffffff` | `#ffffff` |
| default | `?couiColorLabelOnColor` | `#ffffff` | `#ffffff` |

### `coui_btn_secondary_text_color`

来源：`res/color/coui_btn_secondary_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiBtnSecondaryTextColorDisable` | `#42000000` | `#28ffffff` |
| pressed | `?couiColorLabelPrimary` | `#e6000000` | `#e6ffffff` |
| selected | `?couiColorLabelPrimary` | `#e6000000` | `#e6ffffff` |
| default | `?couiColorLabelPrimary` | `#e6000000` | `#e6ffffff` |

### `coui_btn_tiny_default_text_color`

来源：`res/color/coui_btn_tiny_default_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiColorHintNeutral` | `#42000000` | `#4dffffff` |
| pressed | `@color/coui_main_button_text_color_pressed` | `#ffffffff` | `#ffffffff` |
| selected | `@color/coui_color_label_on_color` | `#ffffff` | `#ffffff` |
| default | `@color/coui_color_label_on_color` | `#ffffff` | `#ffffff` |

### `coui_btn_tiny_halfcolor_text_color`

来源：`res/color/coui_btn_tiny_halfcolor_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiBtnSecondaryTextColorDisable` | `#42000000` | `#28ffffff` |
| pressed | `?couiColorLabelTheme` | `#0080ff` | `#1a8cff` |
| selected | `?couiColorLabelTheme` | `#0080ff` | `#1a8cff` |
| default | `?couiColorLabelTheme` | `#0080ff` | `#1a8cff` |

### `coui_btn_tiny_translate_text_color`

来源：`res/color/coui_btn_tiny_translate_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `@color/coui_color_label_tertiary_dark` | `#4dffffff` | `#4dffffff` |
| pressed | `?couiColorLabelOnColor` | `#ffffff` | `#ffffff` |
| selected | `?couiColorLabelOnColor` | `#ffffff` | `#ffffff` |
| default | `?couiColorLabelOnColor` | `#ffffff` | `#ffffff` |

## 3. 颜色资源

| 名称 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- |
| `GrayOpacity26` | `#42000000` | `#42000000` | res/values/colors.xml:49 |
| `GrayOpacity8` | `#14000000` | `#14000000` | res/values/colors.xml:54 |
| `GrayOpacity90` | `#e6000000` | `#e6000000` | res/values/colors.xml:55 |
| `White` | `#ffffff` | `#ffffff` | res/values/colors.xml:119 |
| `WhiteOpacity10` | `#1affffff` | `#1affffff` | res/values/colors.xml:120 |
| `WhiteOpacity26` | `#42ffffff` | `#42ffffff` | res/values/colors.xml:124 |
| `WhiteOpacity54` | `#8affffff` | `#8affffff` | res/values/colors.xml:127 |
| `WhiteOpacity90` | `#e6ffffff` | `#e6ffffff` | res/values/colors.xml:129 |
| `coui_ai_payment_content_text_primary` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:711 |
| `coui_btn_ai_payment_primary_text_disabled` | `#8affffff` | `#42000000` | res/values/colors.xml:752 |
| `coui_btn_ai_payment_primary_text_enabled` | `#ffffff` | `#e6000000` | res/values/colors.xml:753 |
| `coui_btn_ai_payment_secondary_container` | `#14000000` | `#1affffff` | res/values/colors.xml:754 |
| `coui_btn_ai_payment_secondary_container_disabled` | `#14000000` | `#1affffff` | res/values/colors.xml:755 |
| `coui_btn_ai_payment_secondary_text_disabled` | `#42000000` | `#42ffffff` | res/values/colors.xml:756 |
| `coui_btn_ai_payment_secondary_text_enabled` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:757 |
| `coui_btn_ai_payment_solid_no_material` | `#001d38` | `#dce7ff` | res/values/colors.xml:758 |
| `coui_btn_ai_payment_solid_no_material_disabled` | `#4d001d38` | `#4ddce7ff` | res/values/colors.xml:760 |
| `coui_btn_default_text_color_disable` | `#8affffff` | `#28ffffff` | res/values/colors.xml:773 |
| `coui_btn_secondary_text_color_disable` | `#42000000` | `#28ffffff` | res/values/colors.xml:803 |
| `coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/colors.xml:812 |
| `coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/colors.xml:815 |
| `coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/colors.xml:831 |
| `coui_color_container8` | `#14000000` | `#1affffff` | res/values/colors.xml:959 |
| `coui_color_container_theme_blue` | `#0080ff` | `#1a8cff` | res/values/colors.xml:989 |
| `coui_color_container_theme_halftone_blue` | `#260080ff` | `#401a8cff` | res/values/colors.xml:995 |
| `coui_color_disable_blue` | `#4d0080ff` | `#4d1a8cff` | res/values/colors.xml:1035 |
| `coui_color_hint_neutral` | `#42000000` | `#4dffffff` | res/values/colors.xml:1107 |
| `coui_color_label_on_color` | `#ffffff` | `#ffffff` | res/values/colors.xml:1113 |
| `coui_color_label_primary` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:1116 |
| `coui_color_label_tertiary` | `#42000000` | `#4dffffff` | res/values/colors.xml:1131 |
| `coui_color_label_tertiary_dark` | `#4dffffff` | `#4dffffff` | res/values/colors.xml:1132 |
| `coui_color_label_theme_blue` | `#0080ff` | `#1a8cff` | res/values/colors.xml:1134 |
| `coui_color_primary_neutral` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:1179 |
| `coui_main_button_text_color_pressed` | `#ffffffff` | `#ffffffff` | res/values/colors.xml:1390 |
| `share_button_color` | `#0066ff` | `#0066ff` | res/values/colors.xml:2894 |

## 4. 几何与其它数值

| 名称 | 类型 | 浅色 | 深色 | 限定符变体 | 来源 |
| --- | --- | --- | --- | --- | --- |
| `coui_button_anim_enable` | bool | `true` | `true` | — | res/values/bools.xml:147 |
| `coui_btn_drawable_radius_large_tiny` | dimen | `-1.0dp` | `-1.0dp` | — | res/values/dimens.xml:769 |
| `coui_btn_large_height_min` | dimen | `44.0dp` | `44.0dp` | — | res/values/dimens.xml:774 |
| `coui_btn_large_width_min` | dimen | `132.0dp` | `132.0dp` | — | res/values/dimens.xml:775 |
| `coui_btn_padding_vertical` | dimen | `8.0dp` | `8.0dp` | — | res/values/dimens.xml:777 |
| `coui_btn_small_height_min` | dimen | `28.0dp` | `28.0dp` | — | res/values/dimens.xml:778 |
| `coui_btn_small_text_size` | dimen | `14.0sp` | `14.0sp` | — | res/values/dimens.xml:779 |
| `coui_btn_small_text_size_tiny` | dimen | `14.0dp` | `14.0dp` | — | res/values/dimens.xml:780 |
| `coui_btn_small_width_min` | dimen | `52.0dp` | `52.0dp` | — | res/values/dimens.xml:781 |
| `coui_btn_text_size` | dimen | `16.0sp` | `16.0sp` | — | res/values/dimens.xml:782 |
| `coui_btn_text_size_tiny` | dimen | `16.0dp` | `16.0dp` | — | res/values/dimens.xml:783 |
| `coui_spacing_multiplier_button_l` | dimen | `1.1579` | `1.1579` | — | res/values-v35/dimens.xml:8 |

## 5. 动效资源

### anim / animator

| 文件 | 根标签 | 参数 | 来源 |
| --- | --- | --- | --- |
| `m3_btn_elevated_btn_state_list_anim.xml` | set | android:duration=@integer/m3_btn_anim_duration_ms,0,@integer/m3_btn_anim_duration_ms,0,0,0; android:valueTo=@dimen/m3_btn_elevated_translation_z_hovered,@dimen/m3_comp_button_elevated_container_elevation,@dimen/m3_btn_translation_z_base,@dimen/m3_comp_button_elevated_container_elevation,@dimen/m3_btn_disabled_translation_z,@dimen/m3_comp_button_elevated_disabled_container_elevation; android:propertyName=translationZ,elevation,translationZ,elevation,translationZ,elevation | res/animator/m3_btn_elevated_btn_state_list_anim.xml |
| `m3_btn_state_list_anim.xml` | set | android:duration=@integer/m3_btn_anim_duration_ms,0,@integer/m3_btn_anim_duration_ms,0,0,0; android:valueTo=@dimen/m3_btn_translation_z_hovered,@dimen/m3_comp_button_filled_container_elevation,@dimen/m3_btn_translation_z_base,@dimen/m3_comp_button_filled_container_elevation,@dimen/m3_btn_disabled_translation_z,@dimen/m3_comp_button_filled_disabled_container_elevation; android:propertyName=translationZ,elevation,translationZ,elevation,translationZ,elevation | res/animator/m3_btn_state_list_anim.xml |
| `m3_split_button_chevron_reverse_rotation.xml` | set | android:interpolator=@animator/m3_split_button_chevron_overshoot_interpolator; android:duration=200; android:valueFrom=0; android:valueTo=-180; android:propertyName=rotation | res/animator/m3_split_button_chevron_reverse_rotation.xml |
| `m3_split_button_chevron_rotation.xml` | set | android:interpolator=@animator/m3_split_button_chevron_overshoot_interpolator; android:duration=200; android:valueFrom=0; android:valueTo=180; android:propertyName=rotation | res/animator/m3_split_button_chevron_rotation.xml |

## 6. 未闭合引用

无。

## 7. 同族但未在本组件样式链中引用的资源

仅列名称作为定点取证线索，**不是本组件已确认使用的规格**；使用前需先找到消费者。

### styles（405）

`AccessibilityDialogButton`, `AccessibilityDialogButtonBarSpace`, `AccessibilityDialogButtonList`, `ActionPrimaryButton`, `ActionSecondaryButton`, `AiSearchButtonStyle`, `AlertDialog.Button.SettingsLib`, `AppApplicationButtonStyle`, `AppApplicationFirstButtonStyle`, `AppInfoButtonStyle`, `ApplyButtonStyle`, `AutoclickDialogRadioButton`, `Banner.ButtonText.SettingsLib`, `Banner.NegativeButton.SettingsLib.Expressive`, `Banner.PositiveButton.SettingsLib.Expressive`, `Base.TextAppearance.AppCompat.Button`, `Base.TextAppearance.AppCompat.Widget.Button`, `Base.TextAppearance.AppCompat.Widget.Button.Borderless.Colored`, `Base.TextAppearance.AppCompat.Widget.Button.Colored`, `Base.TextAppearance.AppCompat.Widget.Button.Inverse`, `Base.TextAppearance.MaterialComponents.Button`, `Base.Widget.AppCompat.ActionButton`, `Base.Widget.AppCompat.ActionButton.CloseMode`, `Base.Widget.AppCompat.ActionButton.Overflow`, `Base.Widget.AppCompat.Button`, `Base.Widget.AppCompat.Button.Borderless`, `Base.Widget.AppCompat.Button.Borderless.Colored`, `Base.Widget.AppCompat.Button.ButtonBar.AlertDialog`, `Base.Widget.AppCompat.Button.Colored`, `Base.Widget.AppCompat.Button.Small`, `Base.Widget.AppCompat.ButtonBar`, `Base.Widget.AppCompat.ButtonBar.AlertDialog`, `Base.Widget.AppCompat.CompoundButton.CheckBox`, `Base.Widget.AppCompat.CompoundButton.RadioButton`, `Base.Widget.AppCompat.CompoundButton.Switch`, `Base.Widget.AppCompat.ImageButton`, `Base.Widget.AppCompat.Toolbar.Button.Navigation`, `Base.Widget.Material3.CompoundButton.CheckBox`, `Base.Widget.Material3.CompoundButton.RadioButton`, `Base.Widget.Material3.CompoundButton.Switch`, `Base.Widget.Material3.ExtendedFloatingActionButton`, `Base.Widget.Material3.ExtendedFloatingActionButton.Icon`, `Base.Widget.Material3.ExtendedFloatingActionButton.Large`, `Base.Widget.Material3.ExtendedFloatingActionButton.Medium`, `Base.Widget.Material3.ExtendedFloatingActionButton.Small`, `Base.Widget.Material3.FloatingActionButton`, `Base.Widget.Material3.FloatingActionButton.Large`, `Base.Widget.Material3.FloatingActionButton.Medium`, `Base.Widget.Material3.FloatingActionButton.Small`, `Base.Widget.Material3.MaterialCalendar.NavigationButton`, `Base.Widget.MaterialComponents.MaterialCalendar.HeaderToggleButton`, `Base.Widget.MaterialComponents.MaterialCalendar.NavigationButton`, `BorderlessButton`, `BroadcastActionButton`, `ButtonBar.COUI`, `COUIAlertDialogBottomButton`, `COUIAlertDialogBottomButton.Bottom`, `COUIAlertDialogBottomButton.Bottom.Last`, `COUIAlertDialogBottomButton.Bottom.Tiny`, `COUIAlertDialogBottomButtonDivider`, `COUIAlertDialogBottomButtonDivider.Tiny`, `COUIAlertDialogBottomButtonNewNormal`, `COUIAlertDialogBottomButtonNewNormal.Last`, `COUIAlertDialogBottomButtonNewNormal.Recommend`, `COUIAlertDialogBottomButtonNewNormal.Recommend.AiPayment`, `COUIAlertDialogBottomButtonNewNormal.Recommend.Normal`, `COUIAlertDialogBottomButtonSecondary`, `COUIAlertDialogBottomButtonSecondary.AiPayment`, `COUIAlertDialogBottomButtonSecondary.Last`, `COUIAlertDialogTinyButton`, `COUIAlertDialogTinyButton.Last`, `COUIAlertDialogTinyButton.Normal`, `CardPreferenceBorderlessButton`, `CardPreferencePrimaryButton`, `ConditionCardBorderlessButton`, `ConditionFullCardBorderlessButton`, `ConditionHalfCardBorderlessButton`, `ContextualCardDismissalButton`, `CredentialManagerChangeButton`, `CredentialManagerOpenButton` … 其余 325 项见 JSON

### colors（149）

`ai_search_button_background_color`, `button_material_dark`, `button_material_light`, `button_press`, `color_delete_alert_dialog_button_color_warning`, `color_delete_alert_dialog_button_warning_color`, `color_delete_alert_dialog_button_warning_color_disable`, `color_delete_alert_dialog_button_warning_disable_color`, `coui_alert_dialog_button_color_pressed`, `coui_alert_dialog_button_color_tiny`, `coui_alert_dialog_button_text_color_tiny`, `coui_borderless_button_solid_color_disabled`, `coui_borderless_button_solid_color_normal`, `coui_borderless_button_solid_dark_color_disabled`, `coui_borderless_button_solid_dark_color_normal`, `coui_borderless_button_text_color`, `coui_borderless_button_text_color_disabled`, `coui_borderless_button_text_dark_color`, `coui_borderless_button_text_dark_color_disabled`, `coui_bottom_alert_dialog_button_text_color`, `coui_bottom_alert_dialog_button_text_color_dark`, `coui_bottom_alert_dialog_button_warning_color`, `coui_bottom_assignment_alert_dialog_button_text_color`, `coui_bottom_assignment_alert_dialog_button_text_color_dark`, `coui_bottom_assignment_alert_dialog_button_text_color_light`, `coui_btn_ai_payment_solid_no_material_dark`, `coui_btn_ai_payment_solid_no_material_disabled_dark`, `coui_btn_ai_payment_solid_no_material_disabled_light`, `coui_btn_ai_payment_solid_no_material_light`, `coui_btn_check_color_off_disabled`, `coui_btn_check_color_off_normal`, `coui_btn_check_color_on_disabled`, `coui_btn_check_dark_color_off_disabled`, `coui_btn_check_dark_color_off_normal`, `coui_btn_check_dark_color_on_disabled`, `coui_btn_check_inner_color_on_disabled`, `coui_btn_check_inner_color_on_normal`, `coui_btn_check_inner_dark_color_on_disabled`, `coui_btn_default_text_color_disable_dark`, `coui_btn_default_text_color_disable_light`, `coui_btn_default_text_disabled_color`, `coui_btn_default_text_normal_color`, `coui_btn_default_text_pressed_color`, `coui_btn_drawable_color_disabled`, `coui_btn_eye_color_disabled`, `coui_btn_eye_color_normal`, `coui_btn_eye_color_pressed`, `coui_btn_eye_dark_color_normal`, `coui_btn_eye_light_color_normal`, `coui_btn_eye_slash_color_left_top`, `coui_btn_eye_slash_dark_color_left_top`, `coui_btn_eye_slash_light_color_left_top`, `coui_btn_part_check_color_on_disabled`, `coui_btn_part_check_inner_color_on_disabled`, `coui_btn_part_check_inner_color_on_normal`, `coui_btn_part_check_inner_dark_color_on_disabled`, `coui_btn_part_check_inner_light_color_on_disabled`, `coui_btn_radio_background_color`, `coui_btn_radio_background_dark_color`, `coui_btn_radio_color_on`, `coui_btn_radio_color_on_disabled`, `coui_btn_radio_dark_color_disabled`, `coui_btn_radio_dark_color_off`, `coui_btn_radio_dark_color_on`, `coui_btn_radio_dark_color_on_disabled`, `coui_btn_radio_light_color_on_disabled`, `coui_btn_radio_on_disabled_bg`, `coui_btn_secondary_text_color_disable_dark`, `coui_btn_secondary_text_color_disable_light`, `coui_btn_stress_text_disabled_color`, `coui_btn_stress_text_normal_color`, `coui_btn_stress_text_pressed_color`, `coui_button_main_disable`, `coui_button_main_disable_text`, `coui_button_neutral_color`, `coui_button_neutral_color_disable_dark`, `coui_button_neutral_color_disable_light`, `coui_button_second_disable_text`, `coui_button_sub`, `coui_button_sub_disable` … 其余 69 项见 JSON

### dimens（488）

`accessibility_button_preview_height`, `accessibility_tts_play_btn_width`, `action_button_icon_size`, `ai_search_button_corner_radius`, `ai_search_button_icon_size`, `ai_search_button_icon_size_searching`, `ai_search_button_margin_top`, `ai_search_button_padding_bottom`, `ai_search_button_padding_end`, `ai_search_button_padding_start`, `ai_search_button_padding_top`, `ai_search_button_stroke`, `ai_search_button_text_and_icon_spacing`, `ai_search_button_text_size`, `app_volume_guide_button_margin_top`, `apply_button_margin_bottom_in_gesture`, `biometric_enroll_bottom_btn_width`, `bluetooth_find_broadcast_button_one_size`, `bluetooth_find_broadcast_button_start_margin`, `bluetooth_find_broadcast_button_two_size`, `bubble_preference_button_padding`, `button_corner_radius`, `button_fill_alpha`, `combination_animation_button_padding`, `compat_button_inset_horizontal_material`, `compat_button_inset_vertical_material`, `compat_button_padding_horizontal_material`, `compat_button_padding_vertical_material`, `contextual_card_dismissal_button_margin_end`, `contextual_card_dismissal_button_margin_start`, `contrast_button_horizontal_spacing`, `contrast_button_inner_size`, `contrast_button_radius`, `contrast_button_stroke_width`, `contrast_button_text_size`, `contrast_button_text_spacing`, `contrast_button_total_size`, `couiToolTipsCancelButtonInsects`, `coui_alert_dialog_builder_with_button_min_height`, `coui_alert_dialog_builder_with_button_min_height_hor`, `coui_alert_dialog_button_height`, `coui_alert_dialog_button_horizontal_padding`, `coui_alert_dialog_button_horizontal_padding_with_recommend`, `coui_alert_dialog_button_min_height_multiline`, `coui_alert_dialog_button_padding`, `coui_alert_dialog_button_padding_bottom`, `coui_alert_dialog_button_padding_top`, `coui_alert_dialog_button_parent_padding_bottom`, `coui_alert_dialog_button_parent_padding_horizontal`, `coui_alert_dialog_button_parent_padding_horizontal_inside`, `coui_alert_dialog_button_parent_padding_vertical`, `coui_alert_dialog_button_secondary_horizontal_padding`, `coui_alert_dialog_button_secondary_spacing`, `coui_alert_dialog_button_secondary_vertical_padding`, `coui_alert_dialog_button_vertical_padding`, `coui_alert_dialog_button_vertical_padding_with_recommend`, `coui_alert_dialog_neg_vertical_button_padding_vertical_bottom`, `coui_alert_dialog_neg_vertical_button_padding_vertical_top`, `coui_alert_dialog_vertical_button_min_height`, `coui_bordless_btn_stroke_width`, `coui_bottom_alert_dialog_button_recommend_height`, `coui_bottom_alert_dialog_buttonbar_margintop`, `coui_bottom_alert_dialog_horizontal_button_margin_default`, `coui_bottom_alert_dialog_horizontal_button_margin_recommend`, `coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_divider_new`, `coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_new`, `coui_bottom_alert_dialog_horizontal_button_padding_top_extra_new`, `coui_bottom_alert_dialog_recommend_button_center_margin`, `coui_bottom_alert_dialog_recommend_button_edge_margin`, `coui_bottom_alert_dialog_recommend_button_padding_vertical`, `coui_bottom_alert_dialog_recommend_button_padding_vertical_multi_line`, `coui_bottom_alert_dialog_vertical_button_margin_nonrecommend`, `coui_bottom_alert_dialog_vertical_button_margin_recommend`, `coui_bottom_alert_dialog_vertical_button_padding_bottom_extra_new`, `coui_bottom_alert_dialog_vertical_button_padding_top_extra_new`, `coui_bottom_alert_dialog_vertical_button_padding_vertical`, `coui_bottom_alert_dialog_vertical_button_padding_vertical_new`, `coui_bottom_dialog_button_panel_text_size`, `coui_btn_checkbox_padding_left`, `coui_btn_desc_height_min` … 其余 408 项见 JSON


## 8. 静态资源不覆盖、必须另取的部分

以下是写组件库时仍需补齐的证据，均**不在 `res/` 里**，脚本无法抽取：

1. 按压 / 回弹的弹簧数值（response、bounce、初速度、结束阈值）与曲线控制点。
2. 材质模糊半径映射、光效 / 阴影的运行时参数与 AGSL 常量。
3. 状态机：进入事件、前置条件、中断 / 反向 / CANCEL 的收尾、输入启用时机。
4. 深浅色、动画等级、光效开关等运行门控对上面各项的分支影响。
5. 真机多状态对照截图（浅 / 深 / 各交互态 / 关键屏幕限定符）。

按 `docs/development/replication-guide.md` 的追踪顺序定点取证，并把结论写进对应 Note；
不要用当前实现反推原厂，也不要把未取到的值补成猜测值。
