# Button 原厂规格证据（com.coloros.phonemanager）

> 由 `tools/extract_component_spec.py` 只读解析 `decoded/res` 自动生成，请勿手改。
> 本文件是**原厂证据快照**，不是组件文档；实现常量以源码与 KDoc 为唯一维护位置。

## 来源

| 项 | 值 |
| --- | --- |
| 包名 | com.coloros.phonemanager |
| 版本 | 17.6.8 |
| 构建 | PKG110_17.0.0.105(CN01)，ColorOS V17.0.0，Android 17（API 37） |
| 设备 | OnePlus Ace 5（PKG110），serial 2ac81f7b |
| SHA-256 | `35ba028ca7843868d9e9c5a17ad99a8cb988525efafd6c4bb5024f374c21d530` |
| 解析配置 | values + values-vN(<=sdk) + values-night（目标 API 37） |
| 匹配规则 | `button\|btn` |
| 样式前缀 | `Widget.COUI.Button` |
| 排除规则 | `^(mtrl_\|btn_(radio\|checkbox)_\|abc_\|settingslib_\|privacy_)` |

计数：{"styles": 21, "theme_attrs": 390, "theme_attrs_coui": 187, "theme_attrs_used": 19, "state_selectors": 8, "colors": 34, "dimens": 12, "anims": 1, "interpolators": 0, "unresolved": 0, "related": {"styles": 176, "colors": 134, "dimens": 281}}

> 主题属性口径：本表按设备实际生效的配置叠加（`values` + `values-vN` + `values-night` 的颜色覆盖），
> 统计全部 item（含 `android:*`）；`DESIGN-TOKENS.md` 的 187 只数 `values` 下 `coui` 开头的属性，
> 两者不是同一口径，不要直接比大小。

## 1. 样式继承链与合并后的属性

每个属性给出原值、浅色与深色解析值；⚠ 表示引用链未闭合，需人工补证据。

### `Widget.COUI.Button`

- parent: `@style/Base.Widget.AppCompat.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button`
- 来源：`res/values/styles.xml:7369`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7370 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7372 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7371 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:7379 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7380 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large`
- 来源：`res/values/styles.xml:7384`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7392 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7391 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7372 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7371 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:7398 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7399 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large.ButtonNew`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew`
- 来源：`res/values/styles.xml:7403`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:7406 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7408 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7407 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7405 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7372 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7404 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `primary` | `primary` | `primary` | res/values/styles.xml:7409 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:7398 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7399 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large.ButtonNew.AiPayment`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew.AiPayment`
- 来源：`res/values/styles.xml:7411`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7392 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7391 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_ai_payment_text_color` | `#ffffff` | `#e6000000` | res/values/styles.xml:7412 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7371 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:7413 |
| `animType` | `1` | `1` | `1` | res/values/styles.xml:7414 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `aiPayment` | `aiPayment` | `aiPayment` | res/values/styles.xml:7415 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorAiPaymentPrimaryDisabled` | `#4d001d38` | `#4ddce7ff` | res/values/styles.xml:7416 |
| `drawableColor` | `?couiColorAiPaymentPrimary` | `#001d38` | `#dce7ff` | res/values/styles.xml:7417 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:7418 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:7419 |

### `Widget.COUI.Button.Large.ButtonNew.AiPaymentSecondary`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary → Widget.COUI.Button.Large.ButtonNew.AiPaymentSecondary`
- 来源：`res/values/styles.xml:7421`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:7406 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7408 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7407 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7405 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_ai_payment_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7422 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7404 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `aiPaymentSecondary` | `aiPaymentSecondary` | `aiPaymentSecondary` | res/values/styles.xml:7423 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorAiPaymentSecondaryDisabled` | `#14000000` | `#1affffff` | res/values/styles.xml:7424 |
| `drawableColor` | `?couiColorAiPaymentSecondary` | `#14000000` | `#1affffff` | res/values/styles.xml:7425 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:7426 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:7427 |

### `Widget.COUI.Button.Large.ButtonNew.HalfColor`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.HalfColor`
- 来源：`res/values/styles.xml:7429`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:7406 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7408 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7407 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7405 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7430 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7404 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `unknown` | `unknown` | `unknown` | res/values/styles.xml:7431 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7432 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:7433 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large.ButtonNew.Secondary`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary`
- 来源：`res/values/styles.xml:7435`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:7406 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7408 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7407 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7405 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7436 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7404 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:7437 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7438 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7439 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.Secondary → Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin`
- 来源：`res/values/styles.xml:7441`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:7406 |
| `android:layout_marginEnd` | `0.0dp` | `0.0dp` | `0.0dp` | res/values/styles.xml:7443 |
| `android:layout_marginStart` | `0.0dp` | `0.0dp` | `0.0dp` | res/values/styles.xml:7442 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7405 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7436 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7404 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:7437 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7438 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7439 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |
| `useMaterialEffect` | `forceDisable` | `forceDisable` | `forceDisable` | res/values/styles.xml:7444 |

### `Widget.COUI.Button.Large.Half`

- parent: `@style/Widget.COUI.Button.Large.ButtonNew.HalfColor`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.ButtonNew → Widget.COUI.Button.Large.ButtonNew.HalfColor → Widget.COUI.Button.Large.Half`
- 来源：`res/values/styles.xml:7446`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `44.0dp` | `44.0dp` | `44.0dp` | res/values/styles.xml:7406 |
| `android:layout_marginEnd` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7408 |
| `android:layout_marginStart` | `32.0dp` | `32.0dp` | `32.0dp` | res/values/styles.xml:7407 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7405 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7430 |
| `android:textSize` | `@dimen/coui_btn_text_size` | `16.0sp` | `16.0sp` | res/values/styles.xml:7404 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `buttonType` | `unknown` | `unknown` | `unknown` | res/values/styles.xml:7431 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7432 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:7433 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7400 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large.TinyFull`

- parent: `@style/Widget.COUI.Button.Large`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull`
- 来源：`res/values/styles.xml:7447`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7451 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:7455 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:7454 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7450 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:7452 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:7453 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7449 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:7448 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:7456 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:7457 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7399 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7458 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Large.TinyFull.Secondary`

- parent: `@style/Widget.COUI.Button.Large.TinyFull`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Large → Widget.COUI.Button.Large.TinyFull → Widget.COUI.Button.Large.TinyFull.Secondary`
- 来源：`res/values/styles.xml:7460`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7386 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7451 |
| `android:layout_marginEnd` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:7455 |
| `android:layout_marginStart` | `16.0dp` | `16.0dp` | `16.0dp` | res/values/styles.xml:7454 |
| `android:layout_width` | `match_parent` | `match_parent` | `match_parent` | res/values/styles.xml:7450 |
| `android:lineSpacingMultiplier` | `@dimen/coui_spacing_multiplier_button_l` | `1.1579` | `1.1579` | res/values/styles.xml:7395 |
| `android:maxLines` | `2` | `2` | `2` | res/values/styles.xml:7452 |
| `android:minHeight` | `@dimen/coui_btn_large_height_min` | `44.0dp` | `44.0dp` | res/values/styles.xml:7394 |
| `android:minWidth` | `@dimen/coui_btn_large_width_min` | `132.0dp` | `132.0dp` | res/values/styles.xml:7393 |
| `android:paddingBottom` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7390 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7387 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7389 |
| `android:paddingTop` | `@dimen/coui_btn_padding_vertical` | `8.0dp` | `8.0dp` | res/values/styles.xml:7388 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `false` | `false` | `false` | res/values/styles.xml:7453 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | `@style/couiTextAppearanceButtonL` | res/values/styles.xml:7385 |
| `android:textColor` | `@color/coui_btn_borderless_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7461 |
| `android:textSize` | `@dimen/coui_btn_text_size_tiny` | `16.0dp` | `16.0dp` | res/values/styles.xml:7448 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7396 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7397 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:7456 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:7462 |
| `drawableColor` | `@color/coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:7463 |
| `drawableRadius` | `@dimen/coui_btn_drawable_radius_large_tiny` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7458 |
| `isShowLoadingText` | `true` | `true` | `true` | res/values/styles.xml:7401 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small`

- parent: `@style/Widget.COUI.Button`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small`
- 来源：`res/values/styles.xml:7465`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7372 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:7467 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:7379 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7476 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.ButtonNew`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew`
- 来源：`res/values/styles.xml:7479`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7481 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7480 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7372 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:7467 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `buttonType` | `primary` | `primary` | `primary` | res/values/styles.xml:7482 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorDisable` | `#4d0080ff` | `#4d1a8cff` | res/values/styles.xml:7379 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7476 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.ButtonNew.AiPayment`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.AiPayment`
- 来源：`res/values/styles.xml:7484`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7481 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7480 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_ai_payment_text_color` | `#ffffff` | `#e6000000` | res/values/styles.xml:7485 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:7467 |
| `animEnable` | `true` | `true` | `true` | res/values/styles.xml:7486 |
| `animType` | `1` | `1` | `1` | res/values/styles.xml:7487 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `buttonType` | `aiPayment` | `aiPayment` | `aiPayment` | res/values/styles.xml:7488 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorAiPaymentPrimaryDisabled` | `#4d001d38` | `#4ddce7ff` | res/values/styles.xml:7489 |
| `drawableColor` | `?couiColorAiPaymentPrimary` | `#001d38` | `#dce7ff` | res/values/styles.xml:7490 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:7491 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:7492 |

### `Widget.COUI.Button.Small.ButtonNew.AiPaymentSecondary`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew.Secondary`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Secondary → Widget.COUI.Button.Small.ButtonNew.AiPaymentSecondary`
- 来源：`res/values/styles.xml:7494`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7481 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7480 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_ai_payment_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7495 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:7467 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `buttonType` | `aiPaymentSecondary` | `aiPaymentSecondary` | `aiPaymentSecondary` | res/values/styles.xml:7496 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorAiPaymentSecondaryDisabled` | `#14000000` | `#1affffff` | res/values/styles.xml:7497 |
| `drawableColor` | `?couiColorAiPaymentSecondary` | `#14000000` | `#1affffff` | res/values/styles.xml:7498 |
| `drawableMaterialEffect` | `true` | `true` | `true` | res/values/styles.xml:7499 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |
| `useMaterialEffect` | `followSystem` | `followSystem` | `followSystem` | res/values/styles.xml:7500 |

### `Widget.COUI.Button.Small.ButtonNew.HalfColor`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.HalfColor`
- 来源：`res/values/styles.xml:7502`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7481 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7480 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7503 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:7467 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `buttonType` | `unknown` | `unknown` | `unknown` | res/values/styles.xml:7504 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7505 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:7506 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.ButtonNew.Secondary`

- parent: `@style/Widget.COUI.Button.Small.ButtonNew`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.ButtonNew → Widget.COUI.Button.Small.ButtonNew.Secondary`
- 来源：`res/values/styles.xml:7508`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:layout_height` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7481 |
| `android:layout_width` | `wrap_content` | `wrap_content` | `wrap_content` | res/values/styles.xml:7480 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_secondary_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7509 |
| `android:textSize` | `@dimen/coui_btn_small_text_size` | `14.0sp` | `14.0sp` | res/values/styles.xml:7467 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `buttonType` | `secondary` | `secondary` | `secondary` | res/values/styles.xml:7510 |
| `couiRoundType` | `smooth` | `smooth` | `smooth` | res/values/styles.xml:7378 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7511 |
| `drawableColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7512 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.TinySmall`

- parent: `@style/Widget.COUI.Button.Small`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall`
- 来源：`res/values/styles.xml:7514`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_tiny_default_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7516 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:7515 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:7517 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:7518 |
| `drawableColor` | `?couiColorContainerTheme` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7476 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.TinySmall.HalfColor`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.HalfColor`
- 来源：`res/values/styles.xml:7520`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_tiny_halfcolor_text_color` | `#0080ff` | `#1a8cff` | res/values/styles.xml:7521 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:7515 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:7517 |
| `disabledColor` | `?couiColorContainer8` | `#14000000` | `#1affffff` | res/values/styles.xml:7522 |
| `drawableColor` | `?couiColorContainerThemeHalftone` | `#260080ff` | `#401a8cff` | res/values/styles.xml:7523 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.TinySmall.Secondary`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.Secondary`
- 来源：`res/values/styles.xml:7525`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_borderless_text_color` | `#e6000000` | `#e6ffffff` | res/values/styles.xml:7526 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:7515 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:7517 |
| `disabledColor` | `@color/coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/styles.xml:7527 |
| `drawableColor` | `@color/coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/styles.xml:7528 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |

### `Widget.COUI.Button.Small.TinySmall.Translate`

- parent: `@style/Widget.COUI.Button.Small.TinySmall`；链：`Base.Widget.AppCompat.Button → Widget.COUI.Button → Widget.COUI.Button.Small → Widget.COUI.Button.Small.TinySmall → Widget.COUI.Button.Small.TinySmall.Translate`
- 来源：`res/values/styles.xml:7530`

| 属性 | 原值 | 浅色 | 深色 | 来源 |
| --- | --- | --- | --- | --- |
| `android:ellipsize` | `end` | `end` | `end` | res/values/styles.xml:7373 |
| `android:gravity` | `center` | `center` | `center` | res/values/styles.xml:7374 |
| `android:minHeight` | `@dimen/coui_btn_small_height_min` | `28.0dp` | `28.0dp` | res/values/styles.xml:7473 |
| `android:minWidth` | `@dimen/coui_btn_small_width_min` | `52.0dp` | `52.0dp` | res/values/styles.xml:7472 |
| `android:paddingBottom` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7471 |
| `android:paddingLeft` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7468 |
| `android:paddingRight` | `12.0dp` | `12.0dp` | `12.0dp` | res/values/styles.xml:7470 |
| `android:paddingTop` | `4.0dp` | `4.0dp` | `4.0dp` | res/values/styles.xml:7469 |
| `android:scrollbars` | `none` | `none` | `none` | res/values/styles.xml:7375 |
| `android:singleLine` | `true` | `true` | `true` | res/values/styles.xml:7376 |
| `android:stateListAnimator` | `@null` | `@null` | `@null` | res/values/styles.xml:7377 |
| `android:textAppearance` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | `@style/couiTextAppearanceButton` | res/values/styles.xml:7466 |
| `android:textColor` | `@color/coui_btn_tiny_translate_text_color` | `#ffffff` | `#ffffff` | res/values/styles.xml:7531 |
| `android:textSize` | `@dimen/coui_btn_small_text_size_tiny` | `14.0dp` | `14.0dp` | res/values/styles.xml:7515 |
| `animEnable` | `@bool/coui_button_anim_enable` | `true` | `true` | res/values/styles.xml:7474 |
| `brightness` | `0.8` | `0.8` | `0.8` | res/values/styles.xml:7475 |
| `couiRoundType` | `common` | `common` | `common` | res/values/styles.xml:7517 |
| `disabledColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:7532 |
| `drawableColor` | `@color/coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/styles.xml:7533 |
| `drawableRadius` | `-1.0dp` | `-1.0dp` | `-1.0dp` | res/values/styles.xml:7477 |
| `isShowLoadingText` | `false` | `false` | `false` | res/values/styles.xml:7381 |
| `needVibrate` | `false` | `false` | `false` | res/values/styles.xml:7382 |
| `pressColor` | `16` | `16` | `16` | res/values/styles.xml:7534 |

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
| `GrayOpacity26` | `#42000000` | `#42000000` | res/values/colors.xml:6 |
| `GrayOpacity8` | `#14000000` | `#14000000` | res/values/colors.xml:9 |
| `GrayOpacity90` | `#e6000000` | `#e6000000` | res/values/colors.xml:10 |
| `White` | `#ffffff` | `#ffffff` | res/values/colors.xml:11 |
| `WhiteOpacity10` | `#1affffff` | `#1affffff` | res/values/colors.xml:12 |
| `WhiteOpacity26` | `#42ffffff` | `#42ffffff` | res/values/colors.xml:16 |
| `WhiteOpacity54` | `#8affffff` | `#8affffff` | res/values/colors.xml:19 |
| `WhiteOpacity90` | `#e6ffffff` | `#e6ffffff` | res/values/colors.xml:21 |
| `coui_ai_payment_content_text_primary` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:1367 |
| `coui_btn_ai_payment_primary_text_disabled` | `#8affffff` | `#42000000` | res/values/colors.xml:1408 |
| `coui_btn_ai_payment_primary_text_enabled` | `#ffffff` | `#e6000000` | res/values/colors.xml:1409 |
| `coui_btn_ai_payment_secondary_container` | `#14000000` | `#1affffff` | res/values/colors.xml:1410 |
| `coui_btn_ai_payment_secondary_container_disabled` | `#14000000` | `#1affffff` | res/values/colors.xml:1411 |
| `coui_btn_ai_payment_secondary_text_disabled` | `#42000000` | `#42ffffff` | res/values/colors.xml:1412 |
| `coui_btn_ai_payment_secondary_text_enabled` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:1413 |
| `coui_btn_ai_payment_solid_no_material` | `#001d38` | `#dce7ff` | res/values/colors.xml:1414 |
| `coui_btn_ai_payment_solid_no_material_disabled` | `#4d001d38` | `#4ddce7ff` | res/values/colors.xml:1416 |
| `coui_btn_default_text_color_disable` | `#8affffff` | `#28ffffff` | res/values/colors.xml:1429 |
| `coui_btn_secondary_text_color_disable` | `#42000000` | `#28ffffff` | res/values/colors.xml:1459 |
| `coui_button_neutral_color_disable` | `#0f000000` | `#4cffffff` | res/values/colors.xml:1469 |
| `coui_button_neutral_color_light` | `#0f000000` | `#0f000000` | res/values/colors.xml:1472 |
| `coui_button_translate_color` | `#33ffffff` | `#33ffffff` | res/values/colors.xml:1488 |
| `coui_color_container8` | `#14000000` | `#1affffff` | res/values/colors.xml:1616 |
| `coui_color_container_theme_blue` | `#0080ff` | `#1a8cff` | res/values/colors.xml:1646 |
| `coui_color_container_theme_halftone_blue` | `#260080ff` | `#401a8cff` | res/values/colors.xml:1652 |
| `coui_color_disable_blue` | `#4d0080ff` | `#4d1a8cff` | res/values/colors.xml:1692 |
| `coui_color_hint_neutral` | `#42000000` | `#4dffffff` | res/values/colors.xml:1764 |
| `coui_color_label_on_color` | `#ffffff` | `#ffffff` | res/values/colors.xml:1770 |
| `coui_color_label_primary` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:1773 |
| `coui_color_label_tertiary` | `#42000000` | `#4dffffff` | res/values/colors.xml:1788 |
| `coui_color_label_tertiary_dark` | `#4dffffff` | `#4dffffff` | res/values/colors.xml:1789 |
| `coui_color_label_theme_blue` | `#0080ff` | `#1a8cff` | res/values/colors.xml:1791 |
| `coui_color_primary_neutral` | `#e6000000` | `#e6ffffff` | res/values/colors.xml:1836 |
| `coui_main_button_text_color_pressed` | `#ffffffff` | `#ffffffff` | res/values/colors.xml:2029 |

## 4. 几何与其它数值

| 名称 | 类型 | 浅色 | 深色 | 限定符变体 | 来源 |
| --- | --- | --- | --- | --- | --- |
| `coui_button_anim_enable` | bool | `true` | `true` | — | res/values/bools.xml:8 |
| `coui_btn_drawable_radius_large_tiny` | dimen | `-1.0dp` | `-1.0dp` | — | res/values/dimens.xml:883 |
| `coui_btn_large_height_min` | dimen | `44.0dp` | `44.0dp` | — | res/values/dimens.xml:888 |
| `coui_btn_large_width_min` | dimen | `132.0dp` | `132.0dp` | — | res/values/dimens.xml:889 |
| `coui_btn_padding_vertical` | dimen | `8.0dp` | `8.0dp` | — | res/values/dimens.xml:891 |
| `coui_btn_small_height_min` | dimen | `28.0dp` | `28.0dp` | — | res/values/dimens.xml:892 |
| `coui_btn_small_text_size` | dimen | `14.0sp` | `14.0sp` | — | res/values/dimens.xml:893 |
| `coui_btn_small_text_size_tiny` | dimen | `14.0dp` | `14.0dp` | — | res/values/dimens.xml:894 |
| `coui_btn_small_width_min` | dimen | `52.0dp` | `52.0dp` | — | res/values/dimens.xml:895 |
| `coui_btn_text_size` | dimen | `16.0sp` | `16.0sp` | — | res/values/dimens.xml:896 |
| `coui_btn_text_size_tiny` | dimen | `16.0dp` | `16.0dp` | — | res/values/dimens.xml:897 |
| `coui_spacing_multiplier_button_l` | dimen | `1.1579` | `1.1579` | — | res/values-v35/dimens.xml:8 |

## 5. 动效资源

### anim / animator

| 文件 | 根标签 | 参数 | 来源 |
| --- | --- | --- | --- |
| `m3_btn_state_list_anim.xml` | set | android:duration=@integer/m3_btn_anim_duration_ms,0,@integer/m3_btn_anim_duration_ms,0,0,0; android:valueTo=@dimen/m3_btn_translation_z_hovered,@dimen/m3_btn_elevation,@dimen/m3_btn_translation_z_base,@dimen/m3_btn_elevation,@dimen/m3_btn_disabled_translation_z,@dimen/m3_btn_disabled_elevation; android:propertyName=translationZ,elevation,translationZ,elevation,translationZ,elevation | res/animator/m3_btn_state_list_anim.xml |

## 6. 未闭合引用

无。

## 7. 同族但未在本组件样式链中引用的资源

仅列名称作为定点取证线索，**不是本组件已确认使用的规格**；使用前需先找到消费者。

### styles（176）

`Base.TextAppearance.AppCompat.Button`, `Base.TextAppearance.AppCompat.Widget.Button`, `Base.TextAppearance.AppCompat.Widget.Button.Borderless.Colored`, `Base.TextAppearance.AppCompat.Widget.Button.Colored`, `Base.TextAppearance.AppCompat.Widget.Button.Inverse`, `Base.TextAppearance.MaterialComponents.Button`, `Base.Widget.AppCompat.ActionButton`, `Base.Widget.AppCompat.ActionButton.CloseMode`, `Base.Widget.AppCompat.ActionButton.Overflow`, `Base.Widget.AppCompat.Button`, `Base.Widget.AppCompat.Button.Borderless`, `Base.Widget.AppCompat.Button.Borderless.Colored`, `Base.Widget.AppCompat.Button.ButtonBar.AlertDialog`, `Base.Widget.AppCompat.Button.Small`, `Base.Widget.AppCompat.ButtonBar`, `Base.Widget.AppCompat.ButtonBar.AlertDialog`, `Base.Widget.AppCompat.CompoundButton.CheckBox`, `Base.Widget.AppCompat.CompoundButton.RadioButton`, `Base.Widget.AppCompat.CompoundButton.Switch`, `Base.Widget.AppCompat.ImageButton`, `Base.Widget.AppCompat.Toolbar.Button.Navigation`, `Base.Widget.Material3.CompoundButton.CheckBox`, `Base.Widget.Material3.CompoundButton.RadioButton`, `Base.Widget.Material3.CompoundButton.Switch`, `Base.Widget.Material3.ExtendedFloatingActionButton`, `Base.Widget.Material3.ExtendedFloatingActionButton.Icon`, `Base.Widget.Material3.FloatingActionButton`, `Base.Widget.Material3.FloatingActionButton.Large`, `Base.Widget.Material3.FloatingActionButton.Small`, `Base.Widget.Material3.MaterialCalendar.NavigationButton`, `Base.Widget.MaterialComponents.MaterialCalendar.HeaderToggleButton`, `Base.Widget.MaterialComponents.MaterialCalendar.NavigationButton`, `BottomButtonStyle`, `ButtonBar.COUI`, `COUIAlertDialogBottomButton`, `COUIAlertDialogBottomButton.Bottom`, `COUIAlertDialogBottomButton.Bottom.Last`, `COUIAlertDialogBottomButton.Bottom.Tiny`, `COUIAlertDialogBottomButtonDivider`, `COUIAlertDialogBottomButtonDivider.Tiny`, `COUIAlertDialogBottomButtonNewNormal`, `COUIAlertDialogBottomButtonNewNormal.Last`, `COUIAlertDialogBottomButtonNewNormal.Recommend`, `COUIAlertDialogBottomButtonNewNormal.Recommend.AiPayment`, `COUIAlertDialogBottomButtonNewNormal.Recommend.Normal`, `COUIAlertDialogBottomButtonSecondary`, `COUIAlertDialogBottomButtonSecondary.AiPayment`, `COUIAlertDialogBottomButtonSecondary.Last`, `COUIAlertDialogTinyButton`, `COUIAlertDialogTinyButton.Last`, `COUIAlertDialogTinyButton.Normal`, `CustomLargeButtonStyle`, `Preference.COUI.COUIButtonPreference`, `Preference.COUITiny.COUIButtonPreference`, `RtlOverlay.Widget.AppCompatSupport.Toolbar.Button.Navigation`, `SecurityGuardBottomButtonStyle`, `SegmentButton`, `ShapeAppearance.M3.Comp.FilledButton.Container.Shape`, `ShapeAppearance.M3.Comp.TextButton.Container.Shape`, `ShapeAppearanceOverlay.MaterialComponents.ExtendedFloatingActionButton`, `ShapeAppearanceOverlay.MaterialComponents.FloatingActionButton`, `TextAppearance.AppCompat.Button`, `TextAppearance.AppCompat.Widget.Button`, `TextAppearance.AppCompat.Widget.Button.Borderless.Colored`, `TextAppearance.AppCompat.Widget.Button.Colored`, `TextAppearance.AppCompat.Widget.Button.Inverse`, `TextAppearance.MaterialComponents.Button`, `ThemeOverlay.Material3.Button`, `ThemeOverlay.Material3.Button.IconButton`, `ThemeOverlay.Material3.Button.IconButton.Filled`, `ThemeOverlay.Material3.Button.IconButton.Filled.Tonal`, `ThemeOverlay.Material3.Button.TextButton`, `ThemeOverlay.Material3.Button.TextButton.Snackbar`, `ThemeOverlay.Material3.Button.TonalButton`, `ThemeOverlay.Material3.ExtendedFloatingActionButton.Primary`, `ThemeOverlay.Material3.ExtendedFloatingActionButton.Secondary`, `ThemeOverlay.Material3.ExtendedFloatingActionButton.Surface`, `ThemeOverlay.Material3.ExtendedFloatingActionButton.Tertiary`, `ThemeOverlay.Material3.FloatingActionButton.Primary`, `ThemeOverlay.Material3.FloatingActionButton.Secondary` … 其余 96 项见 JSON

### colors（134）

`audio_play_button_bg`, `audio_play_button_shadow`, `button_material_dark`, `button_material_light`, `clear_advice_card_btn_disable_color`, `clear_btn_color_normal`, `clear_btn_color_red`, `color_land_button_disable`, `common_ad_btn_stroke_background_color`, `common_ad_btn_text_color`, `common_alert_button_color`, `common_card_button_green`, `common_card_button_red`, `coui_alert_dialog_button_color_pressed`, `coui_alert_dialog_button_color_tiny`, `coui_alert_dialog_button_text_color_tiny`, `coui_borderless_button_solid_color_disabled`, `coui_borderless_button_solid_color_normal`, `coui_borderless_button_solid_dark_color_disabled`, `coui_borderless_button_solid_dark_color_normal`, `coui_borderless_button_text_color`, `coui_borderless_button_text_color_disabled`, `coui_borderless_button_text_dark_color`, `coui_borderless_button_text_dark_color_disabled`, `coui_bottom_alert_dialog_button_text_color`, `coui_bottom_alert_dialog_button_text_color_dark`, `coui_bottom_alert_dialog_button_warning_color`, `coui_bottom_assignment_alert_dialog_button_text_color`, `coui_bottom_assignment_alert_dialog_button_text_color_dark`, `coui_bottom_assignment_alert_dialog_button_text_color_light`, `coui_btn_ai_payment_solid_no_material_dark`, `coui_btn_ai_payment_solid_no_material_disabled_dark`, `coui_btn_ai_payment_solid_no_material_disabled_light`, `coui_btn_ai_payment_solid_no_material_light`, `coui_btn_check_color_off_disabled`, `coui_btn_check_color_off_normal`, `coui_btn_check_color_on_disabled`, `coui_btn_check_dark_color_off_disabled`, `coui_btn_check_dark_color_off_normal`, `coui_btn_check_dark_color_on_disabled`, `coui_btn_check_inner_color_on_disabled`, `coui_btn_check_inner_color_on_normal`, `coui_btn_check_inner_dark_color_on_disabled`, `coui_btn_default_text_color_disable_dark`, `coui_btn_default_text_color_disable_light`, `coui_btn_default_text_disabled_color`, `coui_btn_default_text_normal_color`, `coui_btn_default_text_pressed_color`, `coui_btn_drawable_color_disabled`, `coui_btn_eye_color_disabled`, `coui_btn_eye_color_normal`, `coui_btn_eye_color_pressed`, `coui_btn_eye_dark_color_normal`, `coui_btn_eye_light_color_normal`, `coui_btn_eye_slash_color_left_top`, `coui_btn_eye_slash_dark_color_left_top`, `coui_btn_eye_slash_light_color_left_top`, `coui_btn_part_check_color_on_disabled`, `coui_btn_part_check_inner_color_on_disabled`, `coui_btn_part_check_inner_color_on_normal`, `coui_btn_part_check_inner_dark_color_on_disabled`, `coui_btn_part_check_inner_light_color_on_disabled`, `coui_btn_radio_background_color`, `coui_btn_radio_background_dark_color`, `coui_btn_radio_color_on`, `coui_btn_radio_color_on_disabled`, `coui_btn_radio_dark_color_disabled`, `coui_btn_radio_dark_color_off`, `coui_btn_radio_dark_color_on`, `coui_btn_radio_dark_color_on_disabled`, `coui_btn_radio_light_color_on_disabled`, `coui_btn_radio_on_disabled_bg`, `coui_btn_secondary_text_color_disable_dark`, `coui_btn_secondary_text_color_disable_light`, `coui_btn_stress_text_disabled_color`, `coui_btn_stress_text_normal_color`, `coui_btn_stress_text_pressed_color`, `coui_button_drawable_color`, `coui_button_main_disable`, `coui_button_main_disable_text` … 其余 54 项见 JSON

### dimens（281）

`ad_native_banner_btn_marginTop`, `button_fill_alpha`, `circle_playback_button_margin_bottom`, `circle_record_button_diam`, `circle_record_button_radius`, `clear_advice_btn_start_clean_height`, `clear_advice_btn_start_clean_marginTop`, `clear_advice_btn_start_clean_width`, `clear_advice_start_clear_btn_margin_bottom`, `clear_app_uninstall_app_info__bottom_button_top_margin`, `clear_button_margin_top`, `clear_button_margin_top_max`, `clear_scan_result_manual_button_width`, `common_bottom_button15_width`, `common_bottom_button_bottom_margin`, `common_bottom_button_height`, `common_bottom_button_layout_divider_height`, `common_bottom_button_top_margin`, `common_bottom_button_width`, `common_inside_button15_width`, `common_psw_simple_delete_btn_height`, `common_psw_simple_delete_btn_margin_bottom`, `common_psw_simple_delete_btn_width`, `common_psw_simple_delete_button_width`, `common_stage_button_min_width`, `compat_button_inset_horizontal_material`, `compat_button_inset_vertical_material`, `compat_button_padding_horizontal_material`, `compat_button_padding_vertical_material`, `couiToolTipsCancelButtonInsects`, `coui_alert_dialog_builder_with_button_min_height`, `coui_alert_dialog_builder_with_button_min_height_hor`, `coui_alert_dialog_button_height`, `coui_alert_dialog_button_horizontal_padding`, `coui_alert_dialog_button_horizontal_padding_with_recommend`, `coui_alert_dialog_button_min_height_multiline`, `coui_alert_dialog_button_padding`, `coui_alert_dialog_button_padding_bottom`, `coui_alert_dialog_button_padding_top`, `coui_alert_dialog_button_parent_padding_bottom`, `coui_alert_dialog_button_parent_padding_horizontal`, `coui_alert_dialog_button_parent_padding_horizontal_inside`, `coui_alert_dialog_button_parent_padding_vertical`, `coui_alert_dialog_button_secondary_horizontal_padding`, `coui_alert_dialog_button_secondary_spacing`, `coui_alert_dialog_button_secondary_vertical_padding`, `coui_alert_dialog_button_vertical_padding`, `coui_alert_dialog_button_vertical_padding_with_recommend`, `coui_alert_dialog_neg_vertical_button_padding_vertical_bottom`, `coui_alert_dialog_neg_vertical_button_padding_vertical_top`, `coui_alert_dialog_vertical_button_min_height`, `coui_bordless_btn_stroke_width`, `coui_bottom_alert_dialog_button_recommend_height`, `coui_bottom_alert_dialog_buttonbar_margintop`, `coui_bottom_alert_dialog_horizontal_button_margin_default`, `coui_bottom_alert_dialog_horizontal_button_margin_recommend`, `coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_divider_new`, `coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_new`, `coui_bottom_alert_dialog_horizontal_button_padding_top_extra_new`, `coui_bottom_alert_dialog_recommend_button_center_margin`, `coui_bottom_alert_dialog_recommend_button_edge_margin`, `coui_bottom_alert_dialog_recommend_button_padding_vertical`, `coui_bottom_alert_dialog_recommend_button_padding_vertical_multi_line`, `coui_bottom_alert_dialog_vertical_button_margin_nonrecommend`, `coui_bottom_alert_dialog_vertical_button_margin_recommend`, `coui_bottom_alert_dialog_vertical_button_padding_bottom_extra_new`, `coui_bottom_alert_dialog_vertical_button_padding_top_extra_new`, `coui_bottom_alert_dialog_vertical_button_padding_vertical`, `coui_bottom_alert_dialog_vertical_button_padding_vertical_new`, `coui_bottom_dialog_button_panel_text_size`, `coui_btn_checkbox_padding_left`, `coui_btn_desc_height_min`, `coui_btn_desc_padding_horizontal`, `coui_btn_desc_padding_vertical`, `coui_btn_desc_sub_text_size`, `coui_btn_desc_text_size`, `coui_btn_desc_top_margin`, `coui_btn_drawable_expand_offset`, `coui_btn_drawable_radius_large`, `coui_btn_drawable_radius_small` … 其余 201 项见 JSON


## 8. 静态资源不覆盖、必须另取的部分

以下是写组件库时仍需补齐的证据，均**不在 `res/` 里**，脚本无法抽取：

1. 按压 / 回弹的弹簧数值（response、bounce、初速度、结束阈值）与曲线控制点。
2. 材质模糊半径映射、光效 / 阴影的运行时参数与 AGSL 常量。
3. 状态机：进入事件、前置条件、中断 / 反向 / CANCEL 的收尾、输入启用时机。
4. 深浅色、动画等级、光效开关等运行门控对上面各项的分支影响。
5. 真机多状态对照截图（浅 / 深 / 各交互态 / 关键屏幕限定符）。

按 `docs/development/replication-guide.md` 的追踪顺序定点取证，并把结论写进对应 Note；
不要用当前实现反推原厂，也不要把未取到的值补成猜测值。
