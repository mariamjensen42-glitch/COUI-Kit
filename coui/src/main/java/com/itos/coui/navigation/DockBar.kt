package com.itos.coui.navigation

import android.graphics.BlendMode
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.LocalCouiBackdrop
import com.itos.coui.effect.CouiFloatingMaterial
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.CouiFloatingShadow
import com.itos.coui.effect.CouiFloatingSurface
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.effect.couiFloatingSurface
import com.itos.coui.effect.couiNavigationFeedback
import com.itos.coui.floatingactionbutton.CouiFabDefaults
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.couiTouchMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.motion.rememberCouiTouchMotion
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiTheme
import com.itos.coui.theme.ProvideCouiContent
import com.itos.coui.theme.CouiIcon as Icon
import com.itos.coui.theme.CouiText as Text

/** 悬浮底栏的胶囊高度、中央凸起量、圆钮尺寸与单项宽度。 */
object CouiDockBarDefaults {
    val capsuleHeight = 56.dp

    /** 圆钮露出胶囊顶部的距离；需要容纳 24dp 图标完整可见，所以取 40dp。 */
    val liftHeight = 40.dp
    val centerSize = 56.dp
    val itemWidth = 96.dp
    val horizontalPadding = 8.dp
    val iconSize = 24.dp
    val shape: Shape = CouiCapsuleShape
    val centerShape: Shape = CouiCapsuleShape

    /** 整条栏的总高度：胶囊 + 中央凸起。 */
    val height = capsuleHeight + liftHeight
}

/**
 * 中央凸起的悬浮底栏，对应 ColorOS 手机管家与实用工具页底部的胶囊 dock。
 *
 * 与 [NavigationBar] 的区别是导航项居中、附件以圆形凸出在胶囊上沿而不是排在同一行两端。
 * 胶囊与凸起圆钮各自持有独立的单主体材质：圆钮后绘制并完整覆盖在胶囊之上，圆形轮廓与边缘高光
 * 不会被融球合并掉，对应原厂「实心圆钮压在玻璃胶囊上」的层次。
 *
 * [items] 至少一项，[selectedIndex] 与 [onSelection] 由宿主持有；[centerIcon] 为空时不绘制凸起。
 * 底栏不处理业务跳转，也不维护选中状态。
 */
@RequiresApi(34)
@Composable
fun DockBar(
    modifier: Modifier = Modifier,
    backdrop: CouiBackdrop = checkNotNull(LocalCouiBackdrop.current) {
        "悬浮底栏需要显式 backdrop 或 COUI Scaffold 栏位提供的背景源"
    },
    renderer: CouiFloatingRenderer = remember { CouiFloatingRenderer.standalone() },
    items: List<CouiNavigationItem>,
    selectedIndex: Int,
    onSelection: (Int) -> Unit,
    centerIcon: ImageVector? = null,
    centerDescription: String? = null,
    onCenterClick: (() -> Unit)? = null,
    itemWidth: Dp = CouiDockBarDefaults.itemWidth,
    dark: Boolean = CouiTheme.isDark,
    lightColor: Color = Color.White,
    themeTint: Color = CouiTheme.materialTintFor(dark),
    centerContainerColor: Color = CouiTheme.colorSchemeFor(dark).accent,
    selectedColor: Color = (if (dark) Color.White else Color.Black).copy(alpha = 230f / 255f),
    unselectedColor: Color = (if (dark) Color.White else Color.Black).copy(alpha = 184f / 255f),
) {
    require(items.isNotEmpty() && selectedIndex in items.indices) {
        "底栏至少需要一项, 且选中下标必须在范围内"
    }
    val density = LocalDensity.current
    val contour = LocalCouiContourGlowEnabled.current
    val capsuleMaterial = remember(dark, lightColor, themeTint) {
        CouiFloatingMaterial(
            firstColor = if (dark) Color(0x80666666) else Color(0xA3666666),
            firstBlendMode = if (dark) BlendMode.OVERLAY else BlendMode.COLOR_DODGE,
            secondColor = if (dark) Color(0xB3262626) else Color(0x80F0F0F0),
            edgeAlpha = if (dark) 0.8f else 0.25f,
            innerGlowAlpha = if (dark) 0.03f else 0.1f,
            lightColor = lightColor,
            themeTint = themeTint,
        )
    }
    val capsuleShadow = remember(density, lightColor, dark, contour) {
        fun pixelSize(dp: Float): Float {
            val px = dp * density.density
            return (if (px >= 0f) px + 0.5f else px - 0.5f).toInt().toFloat()
        }
        val projectionColor = when {
            contour -> Color(0x2D000000)
            dark -> Color(0x78000000)
            else -> Color(0x28000000)
        }
        CouiFloatingShadow(
            elevation = pixelSize(if (contour) 24f else 350f), color = projectionColor,
            lightY = pixelSize(if (contour) -320f else -160f),
            lightZ = if (contour) 0f else pixelSize(10000f),
            lightRadius = pixelSize(if (contour) 1600f else 2000f),
            causticColor = lightColor.copy(alpha = 0x42 / 255f),
            layout = Rect(0f, 0f, 0f, pixelSize(4.9f)), sdf = pixelSize(10.4f), smoothCorner = true,
        )
    }
    val centerMaterial = remember(centerContainerColor, dark, density) {
        CouiFabDefaults.material(centerContainerColor, dark, with(density) { 4.dp.roundToPx() })
    }
    val centerShadow = remember(centerContainerColor, density, contour) {
        CouiFabDefaults.shadow(centerContainerColor, density.density, contour)
    }
    val capsuleLayer = rememberGraphicsLayer()
    val centerLayer = rememberGraphicsLayer()
    val centerRenderer = remember { CouiFloatingRenderer.standalone() }
    val capsuleMotion = rememberCouiTouchMotion(true)
    val centerMotion = rememberCouiTouchMotion(true)
    val capsulePress = rememberCouiPressMotion()
    val centerPress = rememberCouiPressMotion()
    var capsuleSize by remember { mutableStateOf(IntSize.Zero) }
    var centerSizePx by remember { mutableStateOf(IntSize.Zero) }
    fun capsuleSurface() = CouiFloatingSurface(
        Rect(Offset.Zero, Size(capsuleSize.width.toFloat(), capsuleSize.height.toFloat()))
            .translate(Offset(capsuleMotion.translationX.value, capsuleMotion.translationY.value)),
        capsuleMotion.scaleX.value + capsulePress.scale.value - 1f,
        capsuleMotion.scaleY.value + capsulePress.scale.value - 1f,
    )
    fun centerSurface() = CouiFloatingSurface(
        Rect(Offset.Zero, Size(centerSizePx.width.toFloat(), centerSizePx.height.toFloat()))
            .translate(Offset(centerMotion.translationX.value, centerMotion.translationY.value)),
        centerMotion.scaleX.value + centerPress.scale.value - 1f,
        centerMotion.scaleY.value + centerPress.scale.value - 1f,
        cornerWeight = 2f,
    )
    val capsuleWidth = itemWidth * items.size + CouiDockBarDefaults.horizontalPadding * 2
    Box(modifier.fillMaxWidth().height(CouiDockBarDefaults.height)) {
        Box(Modifier.align(Alignment.BottomCenter)
            .width(capsuleWidth).height(CouiDockBarDefaults.capsuleHeight)
            .onSizeChanged { capsuleSize = it }
            .couiFloatingSurface(backdrop = backdrop, renderer = renderer, material = capsuleMaterial,
                shadow = capsuleShadow, shape = CouiDockBarDefaults.shape, contentLayer = capsuleLayer,
                contourGlowEnabled = contour, surface = ::capsuleSurface)) {
            Row(Modifier.fillMaxSize()
                .graphicsLayer {
                    translationX = capsuleMotion.translationX.value
                    translationY = capsuleMotion.translationY.value
                    scaleX = capsuleMotion.scaleX.value + capsulePress.scale.value - 1f
                    scaleY = capsuleMotion.scaleY.value + capsulePress.scale.value - 1f
                }
                .couiTouchMotion(capsuleMotion, true, cancelOnConsumedMove = false, consumeDrag = true)
                .couiPressMotion(capsulePress, true)
                .drawWithContent { capsuleLayer.record { this@drawWithContent.drawContent() } }
                .clip(CouiDockBarDefaults.shape)
                .selectableGroup(),
                verticalAlignment = Alignment.CenterVertically) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }
                    val contentColor = if (selected) selectedColor else unselectedColor
                    Column(Modifier.weight(1f).height(CouiDockBarDefaults.capsuleHeight)
                        .couiNavigationFeedback(dark, interactionSource)
                        .selectable(selected = selected, interactionSource = interactionSource, indication = null,
                            role = Role.Tab, onClick = { if (!selected) onSelection(index) }),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Icon(modifier = Modifier.size(CouiDockBarDefaults.iconSize),
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = null, tint = contentColor)
                        Spacer(Modifier.height(2.dp))
                        ProvideCouiContent {
                            Text(text = item.title, color = contentColor, fontSize = 10.sp,
                                lineHeight = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
        if (centerIcon != null) {
            val interactionSource = remember { MutableInteractionSource() }
            // 先取出可空回调, 让 lambda 内的智能转换与是否安装点击语义保持一致。
            val centerClick = onCenterClick
            Box(Modifier.align(Alignment.TopCenter).size(CouiDockBarDefaults.centerSize)
                .onSizeChanged { centerSizePx = it }
                .couiFloatingSurface(backdrop = backdrop, renderer = centerRenderer, material = centerMaterial,
                    shadow = centerShadow, shape = CouiDockBarDefaults.centerShape, contentLayer = centerLayer,
                    contourGlowEnabled = contour,
                    // 只叠前景: 圆钮压在胶囊之上, 不能用 Src 回填擦掉下面的胶囊玻璃。
                    foregroundComposition = true, surface = ::centerSurface)) {
                Box(Modifier.fillMaxSize()
                    .graphicsLayer {
                        translationX = centerMotion.translationX.value
                        translationY = centerMotion.translationY.value
                        scaleX = centerMotion.scaleX.value + centerPress.scale.value - 1f
                        scaleY = centerMotion.scaleY.value + centerPress.scale.value - 1f
                    }
                    .couiTouchMotion(centerMotion, true, cancelOnConsumedMove = false, consumeDrag = true)
                    .couiPressMotion(centerPress, true)
                    .drawWithContent { centerLayer.record { this@drawWithContent.drawContent() } }
                    .clip(CouiDockBarDefaults.centerShape)
                    .then(if (centerClick != null) Modifier
                        .couiNavigationFeedback(dark, interactionSource)
                        .clickable(interactionSource = interactionSource, indication = null, role = Role.Button,
                            onClick = centerClick) else Modifier),
                    contentAlignment = Alignment.Center) {
                    Icon(imageVector = centerIcon, contentDescription = centerDescription,
                        modifier = Modifier.size(CouiDockBarDefaults.iconSize), tint = Color.White)
                }
            }
        }
    }
}
