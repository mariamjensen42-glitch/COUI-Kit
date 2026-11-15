package com.itos.coui.effect

import android.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * 原厂 `COUIMaterialBlurEffect.BlurEffectType` 的 Compose 对应项。
 *
 * 命名去掉 `TYPE_` 前缀，顺序与原厂枚举一致；`NoEffect` 对应清空渲染效果的分支。
 */
enum class CouiMaterialType {
    NoEffect,
    TopBar,
    SearchBarNoMaterial,
    TopBarBlur,
    TopBar1,
    BottomBar,
    SecondaryButton,
    TransparentButton,
    SegmentButton,
    ChipTab,
    ChipTabSelected,
    ChipTabNoBlur,
    ChipTabSelectedNoBlur,
}

/**
 * COUI 17 材质参数表。
 *
 * 依据 `Settings 17.0.0` 的 `com.coui.appcompat.COUIMaterialBlurEffect` 静态块：
 * 每种类型给出模糊半径、两层颜色和两层 `BlendMode`，由
 * `setViewBlurEffect(view, radius, color1, mode1, color2, mode2)` 按
 * 模糊 → layer1 → layer2 的顺序合成。`TopBar1` 在原厂 `getBlurParamsByType` 里
 * 不区分深浅色，所以 [of] 对它忽略 `dark`。
 */
object CouiMaterialDefaults {

    private val srcOver = BlendMode.SRC_OVER
    private val overlayMode = BlendMode.OVERLAY
    private val colorDodge = BlendMode.COLOR_DODGE
    private val luminosity = BlendMode.LUMINOSITY

    private val TopBarLight = preset(0f, 0x66333333L, colorDodge, 0x99F5F5F5L, luminosity)
    private val TopBarDark = preset(0f, 0xCC262626L, srcOver, 0xB37F7F7FL, overlayMode)
    private val SearchBarLight = preset(0f, 0xD6333333L, colorDodge, 0x66F5F5F5L, luminosity)
    private val SearchBarDark = preset(0f, 0xCC262626L, srcOver, 0xB37F7F7FL, overlayMode)
    private val TopBarBlurLight = preset(20f, 0x66333333L, colorDodge, 0x99F5F5F5L, luminosity)
    private val TopBarBlurDark = preset(20f, 0xCC262626L, srcOver, 0xB37F7F7FL, overlayMode)
    private val TopBar1Params = preset(25f, 0x1A6E6E6EL, colorDodge, 0x24F1F1F1L, luminosity)
    private val BottomBarLight = preset(25f, 0xB3666666L, colorDodge, 0x99F5F5F5L, luminosity)
    private val BottomBarDark = preset(25f, 0x80666666L, overlayMode, 0xB3262626L, luminosity)
    private val SecondaryButtonLight = preset(25f, 0xCCFFFFFFL, srcOver, 0x00FFFFFFL, srcOver)
    private val SecondaryButtonDark = preset(25f, 0x33FFFFFFL, srcOver, 0x00FFFFFFL, srcOver)
    private val TransparentButtonLight = preset(25f, 0x3F333333L, overlayMode, 0x3FFFFFFFL, luminosity)
    private val TransparentButtonDark = preset(25f, 0x3F333333L, overlayMode, 0x3FFFFFFFL, luminosity)
    private val SegmentButtonLight = preset(25f, 0x7F333333L, colorDodge, 0x99EFEFEFL, luminosity)
    private val SegmentButtonDark = preset(25f, 0x7F7F7F7FL, overlayMode, 0x99262626L, luminosity)
    private val ChipTabLight = preset(40f, 0x144E4E4EL, colorDodge, 0x14606060L, luminosity)
    private val ChipTabDark = preset(40f, 0x33B2B2B2L, luminosity, 0x33303030L, overlayMode)
    private val ChipTabSelectedLight = preset(40f, 0x66A3A1A1L, colorDodge, 0x99F5F5F5L, luminosity)
    private val ChipTabSelectedDark = preset(40f, 0xA6505050L, luminosity, 0x4CA7A7A7L, overlayMode)
    private val ChipTabNoBlurLight = preset(0f, 0x9A9B9B9BL, colorDodge, 0x9ACECECEL, luminosity)
    private val ChipTabNoBlurDark = preset(0f, 0xEE474747L, luminosity, 0xCE8D8D8DL, overlayMode)
    private val ChipTabSelectedNoBlurLight = preset(0f, 0x66A3A3A3L, colorDodge, 0xCEF5F5F5L, luminosity)
    private val ChipTabSelectedNoBlurDark = preset(0f, 0xDA888888L, luminosity, 0x80A8A8A8L, overlayMode)
    private val NoEffectParams = preset(0f, 0x00000000L, srcOver, 0x00000000L, srcOver)

    fun of(type: CouiMaterialType, dark: Boolean): CouiMaterialPreset = when (type) {
        CouiMaterialType.NoEffect -> NoEffectParams
        CouiMaterialType.TopBar -> if (dark) TopBarDark else TopBarLight
        CouiMaterialType.SearchBarNoMaterial -> if (dark) SearchBarDark else SearchBarLight
        CouiMaterialType.TopBarBlur -> if (dark) TopBarBlurDark else TopBarBlurLight
        // 原厂不区分深浅色，直接返回同一份参数。
        CouiMaterialType.TopBar1 -> TopBar1Params
        CouiMaterialType.BottomBar -> if (dark) BottomBarDark else BottomBarLight
        CouiMaterialType.SecondaryButton -> if (dark) SecondaryButtonDark else SecondaryButtonLight
        CouiMaterialType.TransparentButton -> if (dark) TransparentButtonDark else TransparentButtonLight
        CouiMaterialType.SegmentButton -> if (dark) SegmentButtonDark else SegmentButtonLight
        CouiMaterialType.ChipTab -> if (dark) ChipTabDark else ChipTabLight
        CouiMaterialType.ChipTabSelected -> if (dark) ChipTabSelectedDark else ChipTabSelectedLight
        CouiMaterialType.ChipTabNoBlur -> if (dark) ChipTabNoBlurDark else ChipTabNoBlurLight
        CouiMaterialType.ChipTabSelectedNoBlur ->
            if (dark) ChipTabSelectedNoBlurDark else ChipTabSelectedNoBlurLight
    }

    /**
     * 两个状态之间按进度插值，对应原厂
     * `setViewBlurEffect(view, startParams, endParams, blendModeParams, progress)`：
     * 半径线性插值，颜色按 ARGB 逐通道插值（原厂用 `ArgbEvaluator`），
     * 两层混合模式始终取 [blendModes]，不跟随两端参数。
     *
     * `Color.lerp` 对 sRGB 8 位颜色即逐通道线性，与 `ArgbEvaluator` 只差量化舍入。
     */
    fun interpolate(
        start: CouiMaterialPreset,
        end: CouiMaterialPreset,
        blendModes: CouiMaterialPreset,
        progress: Float,
    ): CouiMaterialPreset {
        require(progress.isFinite()) { "材质进度必须为有限值" }
        val fraction = progress.coerceIn(0f, 1f)
        val radius = start.blurRadiusPx + (end.blurRadiusPx - start.blurRadiusPx) * fraction
        return CouiMaterialPreset(
            blurRadiusPx = radius,
            first = CouiMaterialLayer(
                lerp(start.first.color, end.first.color, fraction),
                blendModes.first.blendMode,
            ),
            second = CouiMaterialLayer(
                lerp(start.second.color, end.second.color, fraction),
                blendModes.second.blendMode,
            ),
        )
    }

    private fun preset(
        radius: Float,
        firstColor: Long,
        firstMode: BlendMode,
        secondColor: Long,
        secondMode: BlendMode,
    ) = CouiMaterialPreset(
        blurRadiusPx = radius,
        first = CouiMaterialLayer(Color(firstColor), firstMode),
        second = CouiMaterialLayer(Color(secondColor), secondMode),
    )
}
