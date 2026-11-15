package com.itos.coui.demo

import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.couiBackdrop
import com.itos.coui.backdrop.rememberCouiBackdrop
import com.itos.coui.demo.ui.Text
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.CouiFloatingSurface
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.effect.SpotlightPreset
import com.itos.coui.effect.couiFloatingSurface
import com.itos.coui.effect.drawCouiGradientBackdrop
import com.itos.coui.effect.rememberCouiSpotlight
import com.itos.coui.floatingactionbutton.CouiFabDefaults
import com.itos.coui.layout.Divider
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.couiTouchMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.motion.rememberCouiTouchMotion
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.rememberCouiTextStyle
import com.itos.coui.theme.CouiTheme
import kotlin.math.roundToInt

/** 装饰图案源：固定日夜色组铺底，供模糊与浮动表面采样。 */
@Composable
private fun BackdropPattern(modifier: Modifier = Modifier, backdrop: CouiBackdrop) {
    val colors = CouiTheme.colorScheme
    val palette = DemoPalette.illustration
    Canvas(modifier.couiBackdrop(backdrop)) {
        drawRect(colors.container)
        palette.forEachIndexed { index, color ->
            drawCircle(color, radius = size.minDimension * 0.26f,
                center = Offset(size.width * (0.22f + index * 0.28f), size.height * (0.3f + index * 0.18f)))
        }
    }
}

/** 顶栏式渐进模糊：只模糊背景，按 fadeHeight 从顶部向下淡出。 */
@RequiresApi(33)
@Composable
internal fun GradientBlurSample() {
    var fadeHeight by rememberSaveable { mutableFloatStateOf(96f) }
    var radius by rememberSaveable { mutableFloatStateOf(24f) }
    val density = LocalDensity.current.density
    val backdrop = rememberCouiBackdrop()
    val colors = CouiTheme.colorScheme
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(220.dp).clip(DemoShapes.card)) {
        BackdropPattern(Modifier.matchParentSize(), backdrop)
        Box(Modifier.matchParentSize().drawCouiGradientBackdrop(backdrop,
            fadeHeightPx = fadeHeight * density, referenceBlurRadiusPx = radius * density))
        Text(stringResource(R.string.effect_gradient_overlay),
            Modifier.align(Alignment.TopStart).padding(16.dp),
            style = rememberCouiTextStyle(CouiTextKind.ItemTitle), color = colors.onSurface)
    }
    Divider()
    DemoSliderPreference(title = stringResource(R.string.effect_gradient_fade), value = fadeHeight,
        onValueChange = { fadeHeight = it }, valueText = fadeHeight.roundToInt().toString(),
        valueRange = 24f..220f)
    Divider()
    DemoSliderPreference(title = stringResource(R.string.blur_radius), value = radius,
        onValueChange = { radius = it }, valueText = radius.roundToInt().toString(),
        valueRange = 0f..48f)
    Divider()
    EffectText(stringResource(R.string.intro_effect_gradient))
}

/** 不透明表面上的跟手光：先画底色，再画光，最后画内容。 */
@RequiresApi(33)
@Composable
internal fun LightingSample(dark: Boolean) {
    var presses by rememberSaveable { mutableIntStateOf(0) }
    val colors = CouiTheme.colorScheme
    val interactions = remember { MutableInteractionSource() }
    val (lightInput, drawLight) = rememberCouiSpotlight(interactive = true,
        preset = SpotlightPreset.OpaqueMedium1, dark = dark)
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(140.dp)
        .background(colors.container, DemoShapes.card).clip(DemoShapes.card)
        .then(lightInput)
        .drawBehind { drawLight?.invoke(this) }
        .clickable(interactionSource = interactions, indication = null, role = Role.Button) { presses++ },
        contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.effect_lighting_press, presses),
            style = rememberCouiTextStyle(CouiTextKind.ItemTitle), color = colors.onSurface)
    }
    EffectText(stringResource(R.string.intro_effect_lighting))
}

/** 按压与触摸形变：按住缩放，横向拖动改变主体形状，松手回弹。 */
@Composable
internal fun MotionSample() {
    val press = rememberCouiPressMotion()
    val motion = rememberCouiTouchMotion(expanded = true)
    val colors = CouiTheme.colorScheme
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(200.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(120.dp).graphicsLayer {
                translationX = motion.translationX.value
                translationY = motion.translationY.value
                scaleX = motion.scaleX.value + press.scale.value - 1f
                scaleY = motion.scaleY.value + press.scale.value - 1f
            }
            .couiTouchMotion(motion, expanded = true)
            .couiPressMotion(press)
            .background(colors.accent, DemoShapes.couiCircle))
    }
    EffectText(stringResource(R.string.intro_effect_motion))
}

/** 浮动表面低层入口：宿主自备采样源、材质、投影与绘制层，组件只负责合成。 */
@RequiresApi(33)
@Composable
internal fun FloatingSurfaceSample(dark: Boolean) {
    val backdrop = rememberCouiBackdrop()
    val colors = CouiTheme.colorScheme
    val contour = LocalCouiContourGlowEnabled.current
    val density = LocalDensity.current.density
    val creation = remember { runCatching { CouiFloatingRenderer.standalone() } }
    val renderer = creation.getOrNull()
    if (renderer == null) {
        EffectText(stringResource(R.string.renderer_error,
            creation.exceptionOrNull()?.message ?: creation.exceptionOrNull()?.javaClass?.simpleName.orEmpty()))
        return
    }
    val material = remember(colors.accent, dark) { CouiFabDefaults.material(colors.accent, dark) }
    val shadow = remember(material, density, contour) {
        CouiFabDefaults.shadow(material.firstColor, density, contour)
    }
    val contentLayer = rememberGraphicsLayer()
    var bounds by remember { mutableStateOf(IntSize.Zero) }
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(220.dp).clip(DemoShapes.card)) {
        BackdropPattern(Modifier.matchParentSize(), backdrop)
        Box(Modifier.align(Alignment.Center).size(width = 168.dp, height = 56.dp)
            .onSizeChanged { bounds = it }
            .couiFloatingSurface(backdrop, renderer, material, shadow, DemoShapes.capsule, contentLayer, contour) {
                CouiFloatingSurface(
                    Rect(Offset.Zero, Size(bounds.width.toFloat(), bounds.height.toFloat())),
                    cornerWeight = 2f,
                )
            }) {
            Box(Modifier.fillMaxSize()
                .drawWithContent { contentLayer.record { this@drawWithContent.drawContent() } }
                .clip(DemoShapes.capsule), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.effect_floating_content),
                    style = rememberCouiTextStyle(CouiTextKind.ItemTitle), color = CouiFabDefaults.contentColor)
            }
        }
    }
    EffectText(stringResource(R.string.intro_effect_floating))
}
