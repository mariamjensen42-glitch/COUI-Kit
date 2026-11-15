# Button 原厂规格证据（com.android.systemui）

> 由 `tools/extract_component_spec.py` 只读解析 `decoded/res` 自动生成，请勿手改。
> 本文件是**原厂证据快照**，不是组件文档；实现常量以源码与 KDoc 为唯一维护位置。

## 来源

| 项 | 值 |
| --- | --- |
| 包名 | com.android.systemui |
| 版本 | 17.99.02 |
| 构建 | PKG110_17.0.0.105(CN01)，ColorOS V17.0.0，Android 17（API 37） |
| 设备 | OnePlus Ace 5（PKG110），serial 2ac81f7b |
| SHA-256 | `360fecabbf6e24db44f187cb6a72882bfc7163d146352d6d9923f4d51194b622` |
| 解析配置 | values + values-vN(<=sdk) + values-night（目标 API 37） |
| 匹配规则 | `button\|btn` |
| 样式前缀 | `Widget.COUI.Button` |
| 排除规则 | `^(mtrl_\|btn_(radio\|checkbox)_\|abc_\|settingslib_\|privacy_)` |

计数：{"styles": 29, "theme_attrs": 390, "theme_attrs_coui": 187, "theme_attrs_used": 22, "state_selectors": 9, "colors": 39, "dimens": 12, "anims": 12, "interpolators": 0, "unresolved": 0, "related": {"styles": 315, "colors": 97, "dimens": 421}}

> 主题属性口径：本表按设备实际生效的配置叠加（`values` + `values-vN` + `values-night` 的颜色覆盖），
> 统计全部 item（含 `android:*`）；`DESIGN-TOKENS.md` 的 187 只数 `values` 下 `coui` 开头的属性，
> 两者不是同一口径，不要直接比大小。

## 1. 样式继承链与合并后的属性

每个属性给出原值、浅色与深色解析值；⚠ 表示引用链未闭合，需人工补证据。

### `Widget.COUI.Button`

- parent: `@style/Base.Widget.AppCompat.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button`
- 来源：`res/values/styles.xml:12304`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12305 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12306 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12314 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12315 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Dark`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Dark`
- 来源：`res/values/styles.xml:12319`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12305 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12320 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12306 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12314 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12315 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large`
- 来源：`res/values/styles.xml:12322`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12330 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12329 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12306 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12336 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12337 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.Borderless`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.Borderless`
- 来源：`res/values/styles.xml:12341`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:background` | `@null` | `@null` | `@null` | res/values/styles.xml:12343 |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12330 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12329 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_all_secondary_text_color` | `#8a000000` | `#8affffff` | res/values/styles.xml:12342 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12306 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:12344 |
| `animType` | `0` | `0` | `0` | res/values/styles.xml:12345 |
| `brightness` | `1.0` | `1.0` | `1.0` | res/values/styles.xml:12346 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12347 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12348 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `strokeColor` | `?couiColorDivider` | `#1f000000` | `#33ffffff` | res/values/styles.xml:12349 |

### `Widget.COUI.Button.Large.ButtonNew`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew`
- 来源：`res/values/styles.xml:12351`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:12354 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12356 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12355 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12353 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12352 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `primary` | `primary` | `primary` | res/values/styles.xml:12357 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12336 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12337 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.ButtonNew.AiPayment`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew.AiPayment`
- 来源：`res/values/styles.xml:12359`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12330 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12329 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_ai_payment_text_color` | `#ffffff` | `#e6000000` | res/values/styles.xml:12360 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12306 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:12361 |
| `animType` | `1` | `1` | `1` | res/values/styles.xml:12362 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `aiPayment` | `aiPayment` | `aiPayment` | res/values/styles.xml:12363 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorAiPaymentPrimaryDisabled` | `#4d001d38` | `#4ddce7ff` | res/values/styles.xml:12364 |
| `drawableColor` | `?couiColorAiPaymentPrimary` | `#001d38` | `#dce7ff` | res/values/styles.xml:12365 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:12366 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:12367 |

### `Widget.COUI.Button.Large.ButtonNew.AiPaymentSecondary`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary → Widget.COUI.Button.Large.ButtonNew.AiPaymentSecondary`
- 来源：`res/values/styles.xml:12369`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:12354 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12356 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12355 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12353 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_ai_payment_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12370 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12352 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `aiPaymentSecondary` | `aiPaymentSecondary` | `aiPaymentSecondary` | res/values/styles.xml:12371 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorAiPaymentSecondaryDisabled` | `#14000000` | `#1affffff` | res/values/styles.xml:12372 |
| `drawableColor` | `?couiColorAiPaymentSecondary` | `#14000000` | `#1affffff` | res/values/styles.xml:12373 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:12374 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:12375 |

### `Widget.COUI.Button.Large.ButtonNew.HalfColor`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.HalfColor`
- 来源：`res/values/styles.xml:12377`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:12354 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12356 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12355 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12353 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12378 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12352 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `unknown` | `unknown` | `unknown` | res/values/styles.xml:12379 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12380 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:12381 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.ButtonNew.Secondary`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary`
- 来源：`res/values/styles.xml:12383`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:12354 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12356 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12355 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12353 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12384 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12352 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:12385 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12386 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12387 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary → Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin`
- 来源：`res/values/styles.xml:12389`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:12354 |
| `android:layout_marginEnd` | `0.0dp` | `0.0dp` | `0.0dp` | res/values/styles.xml:12391 |
| `android:layout_marginStart` | `0.0dp` | `0.0dp` | `0.0dp` | res/values/styles.xml:12390 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12353 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12384 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12352 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:12385 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12386 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12387 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `useMaterialEffect` | `forceDisable` | `forceDisable` | `forceDisable` | res/values/styles.xml:12392 |

### `Widget.COUI.Button.Large.ButtonNew.Translate`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Translate`
- 来源：`res/values/styles.xml:12394`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:12354 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12356 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:12355 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12353 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_tiny_translate_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12395 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12352 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `buttonType` | `transparent` | `transparent` | `transparent` | res/values/styles.xml:12396 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:12397 |
| `drawableColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:12398 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `pressColor` | `16` | `16` | `16` | res/values/styles.xml:12399 |

### `Widget.COUI.Button.Large.Light`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.Light`
- 来源：`res/values/styles.xml:12401`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12330 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12329 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:12306 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12402 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12336 |
| `drawableColor` | `?couiColorSecondary` | `#260080ff` | `#401a8cff` | res/values/styles.xml:12403 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12338 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.TinyFull`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull`
- 来源：`res/values/styles.xml:12405`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12409 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12413 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12412 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12408 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:12410 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:12411 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12407 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:12406 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12414 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:12415 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12337 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12416 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.TinyFull.Dark`

- parent: `@style/Widget.COUI.Button.Large.TinyFull`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull → Widget.COUI.Button.Large.TinyFull.Dark`
- 来源：`res/values/styles.xml:12418`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12409 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12413 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12412 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12408 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:12410 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:12411 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12407 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:12406 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12414 |
| `disabledColor` | `@color/coui_button_neutral_color_disable_dark` | `#4cffffff` | `#4cffffff` | res/values/styles.xml:12419 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12337 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12416 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.TinyFull.Light`

- parent: `@style/Widget.COUI.Button.Large.TinyFull`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull → Widget.COUI.Button.Large.TinyFull.Light`
- 来源：`res/values/styles.xml:12421`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12409 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12413 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12412 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12408 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:12410 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:12411 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12407 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:12406 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12414 |
| `disabledColor` | `@color/coui_button_neutral_color_disable_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:12422 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12337 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12416 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Large.TinyFull.Secondary`

- parent: `@style/Widget.COUI.Button.Large.TinyFull`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull → Widget.COUI.Button.Large.TinyFull.Secondary`
- 来源：`res/values/styles.xml:12424`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12324 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12409 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12413 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:12412 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:12408 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:12333 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:12410 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:12332 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:12331 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12328 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12325 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12327 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:12326 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:12411 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:12323 |
| `android:textColor` | `@color/coui_btn_borderless_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12425 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:12406 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12334 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12335 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12414 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:12426 |
| `drawableColor` | `@color/coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:12427 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12416 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:12339 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small`
- 来源：`res/values/styles.xml:12429`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12314 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12440 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.Borderless`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.Borderless`
- 来源：`res/values/styles.xml:12443`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:background` | `@null` | `@null` | `@null` | res/values/styles.xml:12445 |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_all_secondary_text_color` | `#8a000000` | `#8affffff` | res/values/styles.xml:12444 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:12446 |
| `animType` | `0` | `0` | `0` | res/values/styles.xml:12447 |
| `brightness` | `1.0` | `1.0` | `1.0` | res/values/styles.xml:12448 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12449 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12450 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `strokeColor` | `?couiColorDivider` | `#1f000000` | `#33ffffff` | res/values/styles.xml:12451 |

### `Widget.COUI.Button.Small.ButtonNew`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew`
- 来源：`res/values/styles.xml:12453`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12455 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12454 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `buttonType` | `primary` | `primary` | `primary` | res/values/styles.xml:12456 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12314 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12440 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.ButtonNew.AiPayment`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.AiPayment`
- 来源：`res/values/styles.xml:12458`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12455 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12454 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_ai_payment_text_color` | `#ffffff` | `#e6000000` | res/values/styles.xml:12459 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:12460 |
| `animType` | `1` | `1` | `1` | res/values/styles.xml:12461 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `buttonType` | `aiPayment` | `aiPayment` | `aiPayment` | res/values/styles.xml:12462 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorAiPaymentPrimaryDisabled` | `#4d001d38` | `#4ddce7ff` | res/values/styles.xml:12463 |
| `drawableColor` | `?couiColorAiPaymentPrimary` | `#001d38` | `#dce7ff` | res/values/styles.xml:12464 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:12465 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:12466 |

### `Widget.COUI.Button.Small.ButtonNew.AiPaymentSecondary`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Secondary → Widget.COUI.Button.Small.ButtonNew.AiPaymentSecondary`
- 来源：`res/values/styles.xml:12468`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12455 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12454 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_ai_payment_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12469 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `buttonType` | `aiPaymentSecondary` | `aiPaymentSecondary` | `aiPaymentSecondary` | res/values/styles.xml:12470 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorAiPaymentSecondaryDisabled` | `#14000000` | `#1affffff` | res/values/styles.xml:12471 |
| `drawableColor` | `?couiColorAiPaymentSecondary` | `#14000000` | `#1affffff` | res/values/styles.xml:12472 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:12473 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:12474 |

### `Widget.COUI.Button.Small.ButtonNew.HalfColor`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.HalfColor`
- 来源：`res/values/styles.xml:12476`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12455 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12454 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12477 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `buttonType` | `unknown` | `unknown` | `unknown` | res/values/styles.xml:12478 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12479 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:12480 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.ButtonNew.Secondary`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Secondary`
- 来源：`res/values/styles.xml:12482`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12455 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12454 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12483 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:12484 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12485 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12486 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.ButtonNew.Translate`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Translate`
- 来源：`res/values/styles.xml:12488`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12455 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:12454 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_tiny_translate_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12489 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `buttonType` | `transparent` | `transparent` | `transparent` | res/values/styles.xml:12490 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:12491 |
| `drawableColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:12492 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `pressColor` | `16` | `16` | `16` | res/values/styles.xml:12493 |

### `Widget.COUI.Button.Small.Light`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.Light`
- 来源：`res/values/styles.xml:12495`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:forceDarkAllowed` | `false` | `false` | `false` | res/values/styles.xml:12496 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12307 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:12431 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12497 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:12313 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:12314 |
| `drawableColor` | `?couiColorSecondary` | `#260080ff` | `#401a8cff` | res/values/styles.xml:12498 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.TinySmall`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall`
- 来源：`res/values/styles.xml:12500`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12502 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:12501 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12503 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:12504 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12440 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.TinySmall.HalfColor`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.HalfColor`
- 来源：`res/values/styles.xml:12506`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:12507 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:12501 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12503 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:12508 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:12509 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.TinySmall.Secondary`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.Secondary`
- 来源：`res/values/styles.xml:12511`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_borderless_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:12512 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:12501 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12503 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:12513 |
| `drawableColor` | `@color/coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:12514 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |

### `Widget.COUI.Button.Small.TinySmall.Translate`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.Translate`
- 来源：`res/values/styles.xml:12516`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:12308 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:12309 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:12437 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:12436 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12435 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12432 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:12434 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:12433 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:12310 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:12311 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:12312 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:12430 |
| `android:textColor` | `@color/coui_btn_tiny_translate_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:12517 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:12501 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:12438 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:12439 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:12503 |
| `disabledColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:12518 |
| `drawableColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:12519 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:12441 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:12316 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:12317 |
| `pressColor` | `16` | `16` | `16` | res/values/styles.xml:12520 |

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

### `coui_btn_all_secondary_text_color`

来源：`res/color/coui_btn_all_secondary_text_color.xml`

| 状态 | 原值 | 浅色 | 深色 |
| --- | --- | --- | --- |
| disabled | `?couiBtnSecondaryTextColorDisable` | `#42000000` | `#28ffffff` |
| pressed | `?couiColorSecondNeutral` | `#8a000000` | `#8affffff` |
| selected | `?couiColorSecondNeutral` | `#8a000000` | `#8affffff` |
| default | `?couiColorSecondNeutral` | `#8a000000` | `#8affffff` |

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
| `GrayOpacity26` | `#42000000` | `#42000000` | res/values/colors.xml:14 |
| `GrayOpacity8` | `#14000000` | `#14000000` | res/values/colors.xml:15 |
| `GrayOpacity90` | `#e6000000` | `#e6000000` | res/values/colors.xml:16 |
| `White` | `#ffffff` | `#ffffff` | res/values/colors.xml:17 |
| `WhiteOpacity10` | `#1affffff` | `#1affffff` | res/values/colors.xml:18 |
| `WhiteOpacity26` | `#42ffffff` | `#42ffffff` | res/values/colors.xml:19 |
| `WhiteOpacity54` | `#8affffff` | `#8affffff` | res/values/colors.xml:21 |
| `WhiteOpacity90` | `#e6ffffff` | `#e6ffffff` | res/values/colors.xml:22 |
| `coui_ai_payment_content_text_primary` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:480 |
| `coui_btn_ai_payment_primary_text_disabled` | `#8affffff` | `#42000000` | res/values/colors.xml:490 |
| `coui_btn_ai_payment_primary_text_enabled` | `#ffffff` | `#e6000000` | res/values/colors.xml:491 |
| `coui_btn_ai_payment_secondary_container` | `#14000000` | `#1affffff` | res/values/colors.xml:492 |
| `coui_btn_ai_payment_secondary_container_disabled` | `#14000000` | `#1affffff` | res/values/colors.xml:493 |
| `coui_btn_ai_payment_secondary_text_disabled` | `#42000000` | `#42ffffff` | res/values/colors.xml:494 |
| `coui_btn_ai_payment_secondary_text_enabled` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:495 |
| `coui_btn_ai_payment_solid_no_material` | `#001d38` | `#dce7ff` | res/values/colors.xml:496 |
| `coui_btn_ai_payment_solid_no_material_disabled` | `#4d001d38` | `#4ddce7ff` | res/values/colors.xml:498 |
| `coui_btn_default_text_color_disable` | `#8affffff` | `#28ffffff` | res/values/colors.xml:503 |
| `coui_btn_secondary_text_color_disable` | `#42000000` | `#28ffffff` | res/values/colors.xml:516 |
| `coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/colors.xml:520 |
| `coui_button_neutral_color_disable_dark` | `#4cffffff` | `#4cffffff` | res/values/colors.xml:521 |
| `coui_button_neutral_color_disable_light` | `#0f000000` | `#0f000000` | res/values/colors.xml:522 |
| `coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/colors.xml:523 |
| `coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/colors.xml:533 |
| `coui_color_container8` | `#14000000` | `#1affffff` | res/values/colors.xml:599 |
| `coui_color_container_theme_blue` | `#0080ff` | `#1a8cff` | res/values/colors.xml:630 |
| `coui_color_container_theme_halftone_blue` | `#260080ff` | `#401a8cff` | res/values/colors.xml:636 |
| `coui_color_disable_blue` | `#4d0080ff` | `#4d1a8cff` | res/values/colors.xml:676 |
| `coui_color_divider` | `#1f000000` | `#33ffffff` | res/values/colors.xml:694 |
| `coui_color_hint_neutral` | `#42000000` | `#4dffffff` | res/values/colors.xml:748 |
| `coui_color_label_on_color` | `#ffffff` | `#ffffff` | res/values/colors.xml:754 |
| `coui_color_label_primary` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:757 |
| `coui_color_label_tertiary` | `#42000000` | `#4dffffff` | res/values/colors.xml:772 |
| `coui_color_label_tertiary_dark` | `#4dffffff` | `#4dffffff` | res/values/colors.xml:773 |
| `coui_color_label_theme_blue` | `#0080ff` | `#1a8cff` | res/values/colors.xml:775 |
| `coui_color_primary_neutral` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:820 |
| `coui_color_secondary_blue` | `#260080ff` | `#401a8cff` | res/values/colors.xml:882 |
| `coui_color_secondary_neutral` | `#8a000000` | `#8affffff` | res/values/colors.xml:888 |
| `coui_main_button_text_color_pressed` | `#ffffffff` | `#ffffffff` | res/values/colors.xml:983 |

## 4. 几何与其它数值

| 名称 | 类型 | 浅色 | 深色 | 限定符变体 | 来源 |
| --- | --- | --- | --- | --- | --- |
| `coui_button_anim_enable` | bool | `true` | `true` | — | res/values/bools.xml:115 |
| `coui_btn_drawable_radius_large_tiny` | dimen | `-1.0dp` | `-1.0dp` | — | res/values/dimens.xml:882 |
| `coui_btn_large_height_min` | dimen | `44.0dp` | `44.0dp` | — | res/values/dimens.xml:883 |
| `coui_btn_large_width_min` | dimen | `132.0dp` | `132.0dp` | — | res/values/dimens.xml:884 |
| `coui_btn_padding_vertical` | dimen | `8.0dp` | `8.0dp` | — | res/values/dimens.xml:885 |
| `coui_btn_small_height_min` | dimen | `28.0dp` | `28.0dp` | — | res/values/dimens.xml:886 |
| `coui_btn_small_text_size` | dimen | `14.0sp` | `14.0sp` | — | res/values/dimens.xml:887 |
| `coui_btn_small_text_size_tiny` | dimen | `14.0dp` | `14.0dp` | — | res/values/dimens.xml:888 |
| `coui_btn_small_width_min` | dimen | `52.0dp` | `52.0dp` | — | res/values/dimens.xml:889 |
| `coui_btn_text_size` | dimen | `16.0sp` | `16.0sp` | — | res/values/dimens.xml:890 |
| `coui_btn_text_size_tiny` | dimen | `16.0dp` | `16.0dp` | — | res/values/dimens.xml:891 |
| `coui_spacing_multiplier_button_l` | dimen | `1.1579` | `1.1579` | — | res/values/dimens.xml:1457 |

## 5. 动效资源

### anim / animator

| 文件 | 根标签 | 参数 | 来源 |
| --- | --- | --- | --- |
| `media_button_state_list_animator.xml` | selector | android:interpolator=@interpolator/control_state,@interpolator/control_state,@interpolator/control_state,@interpolator/control_state; android:duration=50,50,250,250; android:valueTo=0.9,0.9,1,1; android:propertyName=scaleX,scaleY,scaleX,scaleY | res/anim/media_button_state_list_animator.xml |
| `top_button_enter_bottom.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=0.8; android:toXScale=1.0; android:fromYScale=0.8; android:toYScale=1.0; android:fromAlpha=0.0; android:toAlpha=1.0 | res/anim/top_button_enter_bottom.xml |
| `top_button_enter_left.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=0.8; android:toXScale=1.0; android:fromYScale=0.8; android:toYScale=1.0; android:fromAlpha=0.0; android:toAlpha=1.0 | res/anim/top_button_enter_left.xml |
| `top_button_enter_right.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=0.8; android:toXScale=1.0; android:fromYScale=0.8; android:toYScale=1.0; android:fromAlpha=0.0; android:toAlpha=1.0 | res/anim/top_button_enter_right.xml |
| `top_button_enter_top.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=0.8; android:toXScale=1.0; android:fromYScale=0.8; android:toYScale=1.0; android:fromAlpha=0.0; android:toAlpha=1.0 | res/anim/top_button_enter_top.xml |
| `top_button_exit_bottom.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=1.0; android:toXScale=0.8; android:fromYScale=1.0; android:toYScale=0.8; android:fromAlpha=1.0; android:toAlpha=0.0 | res/anim/top_button_exit_bottom.xml |
| `top_button_exit_left.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=1.0; android:toXScale=0.8; android:fromYScale=1.0; android:toYScale=0.8; android:fromAlpha=1.0; android:toAlpha=0.0 | res/anim/top_button_exit_left.xml |
| `top_button_exit_right.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=1.0; android:toXScale=0.8; android:fromYScale=1.0; android:toYScale=0.8; android:fromAlpha=1.0; android:toAlpha=0.0 | res/anim/top_button_exit_right.xml |
| `top_button_exit_top.xml` | set | android:interpolator=@anim/fast_out_slow_in; android:duration=300; android:fromXScale=1.0; android:toXScale=0.8; android:fromYScale=1.0; android:toYScale=0.8; android:fromAlpha=1.0; android:toAlpha=0.0 | res/anim/top_button_exit_top.xml |
| `m3_btn_elevated_btn_state_list_anim.xml` | set | android:duration=@integer/m3_btn_anim_duration_ms,0,@integer/m3_btn_anim_duration_ms,0,0,0; android:valueTo=@dimen/m3_btn_elevated_translation_z_hovered,@dimen/m3_comp_button_elevated_container_elevation,@dimen/m3_btn_translation_z_base,@dimen/m3_comp_button_elevated_container_elevation,@dimen/m3_btn_disabled_translation_z,@dimen/m3_comp_button_elevated_disabled_container_elevation; android:propertyName=translationZ,elevation,translationZ,elevation,translationZ,elevation | res/animator/m3_btn_elevated_btn_state_list_anim.xml |
| `m3_btn_state_list_anim.xml` | set | android:duration=@integer/m3_btn_anim_duration_ms,0,@integer/m3_btn_anim_duration_ms,0,0,0; android:valueTo=@dimen/m3_btn_translation_z_hovered,@dimen/m3_comp_button_filled_container_elevation,@dimen/m3_btn_translation_z_base,@dimen/m3_comp_button_filled_container_elevation,@dimen/m3_btn_disabled_translation_z,@dimen/m3_comp_button_filled_disabled_container_elevation; android:propertyName=translationZ,elevation,translationZ,elevation,translationZ,elevation | res/animator/m3_btn_state_list_anim.xml |
| `tv_window_menu_action_button_animator.xml` | selector | android:duration=200,200,200,200,200,200; android:valueTo=1.0,1.0,1.1,1.1,1.0,1.0; android:propertyName=scaleX,scaleY,scaleX,scaleY,scaleX,scaleY; android:valueFrom=1.0,1.0,1.1,1.1 | res/animator/tv_window_menu_action_button_animator.xml |

## 6. 未闭合引用

无。

## 7. 同族但未在本组件样式链中引用的资源

仅列名称作为定点取证线索，**不是本组件已确认使用的规格**；使用前需先找到消费者。

### styles（315）

`AuthCredentialBackButton`, `AuthCredentialContentViewMoreOptionsButtonStyle`, `AuthCredentialNegativeButtonStyle`, `AuthNonBioCredentialEmergencyButtonStyle`, `AuthNonBioCredentialFallbackButtonStyle`, `Banner.ButtonText.SettingsLib`, `Banner.NegativeButton.SettingsLib.Expressive`, `Banner.PositiveButton.SettingsLib.Expressive`, `Base.TextAppearance.AppCompat.Button`, `Base.TextAppearance.AppCompat.Widget.Button`, `Base.TextAppearance.AppCompat.Widget.Button.Borderless.Colored`, `Base.TextAppearance.AppCompat.Widget.Button.Colored`, `Base.TextAppearance.AppCompat.Widget.Button.Inverse`, `Base.TextAppearance.MaterialComponents.Button`, `Base.Widget.AppCompat.ActionButton`, `Base.Widget.AppCompat.ActionButton.CloseMode`, `Base.Widget.AppCompat.ActionButton.Overflow`, `Base.Widget.AppCompat.Button`, `Base.Widget.AppCompat.Button.Borderless`, `Base.Widget.AppCompat.Button.Borderless.Colored`, `Base.Widget.AppCompat.Button.ButtonBar.AlertDialog`, `Base.Widget.AppCompat.Button.Colored`, `Base.Widget.AppCompat.Button.Small`, `Base.Widget.AppCompat.ButtonBar`, `Base.Widget.AppCompat.ButtonBar.AlertDialog`, `Base.Widget.AppCompat.CompoundButton.CheckBox`, `Base.Widget.AppCompat.CompoundButton.RadioButton`, `Base.Widget.AppCompat.CompoundButton.Switch`, `Base.Widget.AppCompat.ImageButton`, `Base.Widget.AppCompat.Toolbar.Button.Navigation`, `Base.Widget.Material3.CompoundButton.CheckBox`, `Base.Widget.Material3.CompoundButton.RadioButton`, `Base.Widget.Material3.CompoundButton.Switch`, `Base.Widget.Material3.ExtendedFloatingActionButton`, `Base.Widget.Material3.ExtendedFloatingActionButton.Icon`, `Base.Widget.Material3.ExtendedFloatingActionButton.Large`, `Base.Widget.Material3.ExtendedFloatingActionButton.Medium`, `Base.Widget.Material3.ExtendedFloatingActionButton.Small`, `Base.Widget.Material3.FloatingActionButton`, `Base.Widget.Material3.FloatingActionButton.Large`, `Base.Widget.Material3.FloatingActionButton.Medium`, `Base.Widget.Material3.FloatingActionButton.Small`, `Base.Widget.Material3.MaterialCalendar.NavigationButton`, `Base.Widget.MaterialComponents.MaterialCalendar.HeaderToggleButton`, `Base.Widget.MaterialComponents.MaterialCalendar.NavigationButton`, `BluetoothTileDialog.AudioSharingButton`, `BorderlessButton`, `ButtonBar.COUI`, `ButtonBarStyle`, `COUIAlertDialogBottomButton`, `COUIAlertDialogBottomButton.Bottom`, `COUIAlertDialogBottomButton.Bottom.Last`, `COUIAlertDialogBottomButton.Bottom.Tiny`, `COUIAlertDialogBottomButtonDivider`, `COUIAlertDialogBottomButtonDivider.Tiny`, `COUIAlertDialogBottomButtonNewNormal`, `COUIAlertDialogBottomButtonNewNormal.Last`, `COUIAlertDialogBottomButtonNewNormal.Recommend`, `COUIAlertDialogBottomButtonNewNormal.Recommend.AiPayment`, `COUIAlertDialogBottomButtonNewNormal.Recommend.Normal`, `COUIAlertDialogBottomButtonSecondary`, `COUIAlertDialogBottomButtonSecondary.AiPayment`, `COUIAlertDialogBottomButtonSecondary.Last`, `COUIAlertDialogTinyButton`, `COUIAlertDialogTinyButton.Last`, `COUIAlertDialogTinyButton.Normal`, `CaptionButtonStyle`, `DesktopModeHandleMenuActionButton`, `DesktopModeHandleMenuActionButtonImage`, `DesktopModeHandleMenuActionButtonTextView`, `DesktopModeHandleMenuWindowingButton`, `DialogButtonNegative`, `DialogButtonPositive`, `EditTextActivityButton`, `Keyguard.TextView.EmergencyButton`, `LandscapeGestureHintBtn`, `MediaOutput.Dialog.FixPermissionsButton`, `MediaOutput.Dialog.QuickAccessButton`, `MediaPlayer.PageButton`, `MediaPlayer.SolidButton` … 其余 235 项见 JSON

### colors（97）

`animated_action_button_stroke_color`, `bubble_settings_button_text_color`, `bubble_settings_button_text_color_n`, `button_material_dark`, `button_material_light`, `coui_alert_dialog_button_color_tiny`, `coui_alert_dialog_button_text_color_tiny`, `coui_btn_ai_payment_solid_no_material_dark`, `coui_btn_ai_payment_solid_no_material_disabled_dark`, `coui_btn_ai_payment_solid_no_material_disabled_light`, `coui_btn_ai_payment_solid_no_material_light`, `coui_btn_check_inner_color_on_disabled`, `coui_btn_default_text_color_disable_dark`, `coui_btn_default_text_color_disable_light`, `coui_btn_default_text_normal_color`, `coui_btn_default_text_pressed_color`, `coui_btn_drawable_color_disabled`, `coui_btn_part_check_inner_color_on_disabled`, `coui_btn_part_check_inner_color_on_normal`, `coui_btn_part_check_inner_dark_color_on_disabled`, `coui_btn_part_check_inner_light_color_on_disabled`, `coui_btn_radio_color_on_disabled`, `coui_btn_radio_dark_color_on`, `coui_btn_radio_light_color_on_disabled`, `coui_btn_secondary_text_color_disable_dark`, `coui_btn_secondary_text_color_disable_light`, `coui_button_neutral_color`, `coui_button_sub`, `coui_button_sub_disable`, `coui_button_sub_disable_light`, `coui_button_sub_press`, `coui_button_sub_text`, `coui_button_sub_text_disable`, `coui_button_sub_text_disable_light`, `coui_button_sub_text_light`, `coui_button_sub_text_press`, `coui_button_translate_color_lightOS`, `coui_color_segment_button_background`, `coui_color_segment_button_indicator`, `coui_dark_btn_drawable_color_disabled`, `coui_light_btn_drawable_color_disabled`, `coui_segment_button_shadow_color`, `coui_segment_button_shadow_end_color`, `coui_shape_btn_check_fill_color_off_normal`, `coui_shape_btn_check_stroke_color_off_normal`, `desktop_mode_caption_button`, `desktop_mode_caption_button_on_hover_dark`, `desktop_mode_caption_button_on_hover_light`, `divider_meun_button_background_color`, `divider_meun_button_shadow_color`, `flash_item_button_background_color`, `flash_item_button_text_color`, `global_actions_lite_button_background_focused`, `keyguard_privacy_input_password_button_bg`, `keyguard_privacy_input_password_button_text_color_default`, `keyguard_privacy_input_password_button_text_color_disabled`, `kgd_edit_text_done_button_text_color`, `kgd_forget_password_button_text_color`, `kgd_olock_button_color`, `kgd_olock_button_forget_appeal`, `kgd_pictorial_tb_more_btn`, `kgd_security_button_text_color`, `kgd_show_password_button_background_normal_color`, `kgd_show_password_button_background_pressed_color`, `kgd_show_password_button_text_bg_color`, `kgd_show_password_button_text_normal_color`, `kgd_show_password_button_text_pressed_color`, `media_dialog_button_background`, `media_dialog_solid_button_text`, `media_outlined_button_background`, `media_seedling_button_color`, `media_seedling_button_second_color`, `nav_key_button_shadow_color`, `notification_expand_button_pill_color_new`, `notification_material_blur_action_btn_bg`, `notification_material_blur_color_button_text`, `notification_material_blur_expand_button_area`, `op_control_goto_button_dark`, `oplus_biometric_dialog_button_text_color`, `oplus_biometric_retry_btn_bg_color` … 其余 17 项见 JSON

### dimens（421）

`animated_action_button_corner_radius`, `animated_action_button_glow_radius`, `animated_action_button_glow_radius_emphasized`, `animated_action_button_inset_vertical`, `animated_action_button_outline_stroke_width`, `animated_action_button_outline_stroke_width_emphasized`, `aod_action_button_size`, `biometric_auth_back_button_size`, `borderless_button_radius`, `btn_max_width`, `bubble_manage_button_height`, `bubble_manage_button_margin`, `bubble_manage_button_touch_area_height`, `bubble_settings_button_text_size`, `button_corner_radius`, `button_height`, `caption_left_buttons_width`, `caption_right_buttons_width`, `chipbar_end_button_vertical_negative_margin`, `chipbar_end_button_vertical_padding`, `clean_all_btn_center_offset`, `compact_button_fullscreen_margin_top_horizontal`, `compact_button_fullscreen_margin_top_vertical`, `compact_button_margin_vertical_16_9`, `compact_operate_button_margin_horizontal`, `compact_operate_button_margin_vertical`, `compact_operate_button_margin_vertical4_3`, `compact_window_info_btn_h_bottom`, `compact_window_info_btn_h_right`, `compact_window_info_btn_v_bottom`, `compact_window_info_btn_v_right`, `compat_button_inset_horizontal_material`, `compat_button_inset_vertical_material`, `compat_button_margin`, `compat_button_padding_horizontal_material`, `compat_button_padding_vertical_material`, `controls_header_menu_button_size`, `couiToolTipsCancelButtonInsects`, `coui_alert_dialog_button_height`, `coui_alert_dialog_button_horizontal_padding`, `coui_alert_dialog_button_min_height_multiline`, `coui_alert_dialog_button_padding_bottom`, `coui_alert_dialog_button_padding_top`, `coui_alert_dialog_button_parent_padding_bottom`, `coui_alert_dialog_button_parent_padding_horizontal`, `coui_alert_dialog_button_parent_padding_horizontal_inside`, `coui_alert_dialog_button_parent_padding_vertical`, `coui_alert_dialog_vertical_button_min_height`, `coui_bordless_btn_stroke_width`, `coui_bottom_alert_dialog_buttonbar_margintop`, `coui_bottom_alert_dialog_horizontal_button_margin_default`, `coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_divider_new`, `coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_new`, `coui_bottom_alert_dialog_horizontal_button_padding_top_extra_new`, `coui_bottom_alert_dialog_recommend_button_padding_vertical`, `coui_bottom_alert_dialog_vertical_button_padding_bottom_extra_new`, `coui_bottom_alert_dialog_vertical_button_padding_top_extra_new`, `coui_bottom_alert_dialog_vertical_button_padding_vertical_new`, `coui_btn_checkbox_padding_left`, `coui_btn_desc_height_min`, `coui_btn_desc_padding_horizontal`, `coui_btn_desc_padding_vertical`, `coui_btn_desc_sub_text_size`, `coui_btn_desc_text_size`, `coui_btn_desc_top_margin`, `coui_button_radius_offset`, `coui_delete_alert_dialog_button_height`, `coui_delete_alert_dialog_divider_height_verticalbutton`, `coui_dialog_button_text_size`, `coui_full_page_statement_button_button_width_landlong`, `coui_full_page_statement_button_margin`, `coui_full_page_statement_button_margin_bottom_landlong`, `coui_full_page_statement_button_margin_center_landlong`, `coui_full_page_statement_button_margin_horizontal_landlong`, `coui_full_page_statement_button_margin_top`, `coui_full_page_statement_button_margin_top_landlong`, `coui_full_page_statement_button_text_size`, `coui_full_page_statement_button_width`, `coui_full_page_statement_exit_button_margin_bottom`, `coui_full_page_statement_text_button_padding` … 其余 341 项见 JSON


## 8. 静态资源不覆盖、必须另取的部分

以下是写组件库时仍需补齐的证据，均**不在 `res/` 里**，脚本无法抽取：

1. 按压 / 回弹的弹簧数值（response、bounce、初速度、结束阈值）与曲线控制点。
2. 材质模糊半径映射、光效 / 阴影的运行时参数与 AGSL 常量。
3. 状态机：进入事件、前置条件、中断 / 反向 / CANCEL 的收尾、输入启用时机。
4. 深浅色、动画等级、光效开关等运行门控对上面各项的分支影响。
5. 真机多状态对照截图（浅 / 深 / 各交互态 / 关键屏幕限定符）。

按 `docs/development/replication-guide.md` 的追踪顺序定点取证，并把结论写进对应 Note；
不要用当前实现反推原厂，也不要把未取到的值补成猜测值。
