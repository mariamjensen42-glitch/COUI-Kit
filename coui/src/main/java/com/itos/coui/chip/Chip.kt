package com.itos.coui.chip

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import com.itos.coui.badge.CouiBadgeDefaults
import com.itos.coui.effect.couiPressRipple
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * COUIChip 的 Compose 入口。
 *
 * 受控组件：选中值、关闭与点击都由宿主决定。[onSelectedChange] 非空时组件可选中，
 * 否则只响应 [onClick]。[onClose] 非空时显示关闭图标；图标宽度、透明度与文字位移按
 * 原厂 `TintAnimation` / `CloseIconAnimation`（response 0.3、bounce 0）过渡。
 *
 * 宽度按 [metrics] 计算并夹在 `minWidth` 与 `maxWidth` 之间，文本超出 `textMaxWidth`
 * 按 ellipsize 截断；高度不低于 `minHeight`。默认胶囊轮廓，`shape` 可覆盖。
 * 整块可点击，关闭图标有独立命中区；`enabled=false` 时两者都不响应。
 * [leadingIcon] 参与布局测量，须只渲染一个布局节点（尺寸由 [CouiChipMetrics.iconSize] 约束）。
 */
@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: CouiChipStyle = CouiChipStyle.Suggestion,
    metrics: CouiChipMetrics = CouiChipDefaults.metrics(style),
    colors: CouiChipColors = CouiChipDefaults.colors(
        CouiTheme.isDark, CouiTheme.colorScheme.accent, CouiTheme.colorScheme.onAccent,
    ),
    textStyle: TextStyle = CouiChipDefaults.textStyle(style),
    contentColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    count: String? = null,
    showRedDot: Boolean = false,
    onClose: (() -> Unit)? = null,
    closeContentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    onSelectedChange: ((Boolean) -> Unit)? = null,
    shape: Shape = CouiCapsuleShape,
) {
    val checkable = onSelectedChange != null
    val resolvedContentColor = contentColor ?: when {
        !enabled -> if (selected) colors.checkedDisabledContent else colors.uncheckedDisabledContent
        selected -> colors.checkedContent
        else -> colors.uncheckedContent
    }
    val tint by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = chipSpring(),
        visibilityThreshold = .001f,
        label = "chip-tint",
    )
    val closeFraction by animateFloatAsState(
        targetValue = if (onClose != null) 1f else 0f,
        animationSpec = chipSpring(),
        visibilityThreshold = .001f,
        label = "chip-close",
    )
    val background = lerpColor(
        if (enabled) colors.uncheckedBackground else colors.uncheckedDisabledBackground,
        if (enabled) colors.checkedBackground else colors.checkedDisabledBackground,
        tint,
    )
    val press = rememberCouiPressMotion()
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val action: (() -> Unit)? = if (checkable) {
        { onSelectedChange?.invoke(!selected) }
    } else onClick
    val interactive = enabled && action != null
    val m = metrics
    val showClose = onClose != null

    val iconSizePx = with(density) { m.iconSize?.toPx() }
    val iconStartPx = with(density) { m.iconStartPadding.toPx() }
    val iconEndPx = with(density) { m.iconEndPadding.toPx() }
    val startPaddingPx = with(density) { m.startPadding.toPx() }
    val endPaddingPx = with(density) { m.endPadding.toPx() }
    val textStartPx = with(density) { m.textStartPadding.toPx() }
    val textEndPx = with(density) { m.textEndPadding.toPx() }
    val countStartPx = with(density) { m.countMarginStart.toPx() }
    val countEndPx = with(density) { m.countMarginEnd.toPx() }
    val closeSizePx = with(density) { m.closeIconSize.toPx() }
    val closeStartPx = with(density) { m.closeIconStartPadding.toPx() }
    val closeEndPx = with(density) { m.closeIconEndPadding.toPx() }
    val closeTouchPx = with(density) { m.closeIconTouchBoundsSize.toPx() }
    val minWidthPx = with(density) { m.minWidth.toPx() }
    val maxWidthPx = with(density) { m.maxWidth.toPx() }
    val minHeightPx = with(density) { m.minHeight.toPx() }
    val textMaxWidthPx = with(density) { m.textMaxWidth.toPx() }
    val redDotSizeDp = CouiChipDefaults.redDotSize
    val (redDotColor, _) = CouiBadgeDefaults.colors(CouiTheme.isDark)

    Layout(
        content = {
            ChipBackground(background, shape)
            if (leadingIcon != null) leadingIcon()
            CouiText(
                text = text,
                color = resolvedContentColor,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (count != null) {
                CouiText(count, color = colors.countText, style = CouiChipDefaults.countTextStyle, maxLines = 1)
            }
            if (showClose) {
                ChipCloseButton(
                    size = m.closeIconSize,
                    touchSize = m.closeIconTouchBoundsSize,
                    color = resolvedContentColor,
                    enabled = interactive,
                    alpha = closeFraction,
                    iconScale = .3f + .7f * closeFraction,
                    contentDescription = closeContentDescription,
                    onClick = onClose ?: {},
                )
            }
            if (showRedDot) Box(Modifier.size(redDotSizeDp).background(redDotColor, CircleShape))
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = press.scale.value
                scaleY = press.scale.value
            }
            .couiPressMotion(press, enabled = interactive)
            .couiPressRipple(interactionSource, enabled = interactive, shape = shape)
            .then(
                when {
                    !interactive -> Modifier
                    checkable -> Modifier.selectable(
                        selected = selected,
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = true,
                        role = Role.Checkbox,
                        onClick = action ?: {},
                    )
                    else -> Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = action ?: {},
                    )
                },
            ),
    ) { measurables, constraints ->
        var index = 1
        val backgroundPlaceable = measurables[0]
        val iconPlaceable = if (leadingIcon != null) measurables[index++] else null
        val textPlaceable = measurables[index++]
        val countPlaceable = if (count != null) measurables[index++] else null
        val closePlaceable = if (showClose) measurables[index++] else null
        val redDotPlaceable = if (showRedDot) measurables[index] else null

        val iconWidth = if (iconPlaceable == null) 0f else iconSizePx ?: iconPlaceable.maxIntrinsicWidth.toFloat()
        val iconTotal = if (iconPlaceable == null) 0f else iconStartPx + iconWidth + iconEndPx
        val closeWidth = closeStartPx + closeSizePx + closeEndPx
        val closeTouchWidth = max(closeTouchPx, closeWidth)
        val maxAvailable = min(constraints.maxWidth.toFloat(), maxWidthPx)

        val textMaxWidth = max(
            0f,
            min(
                textMaxWidthPx,
                maxAvailable - startPaddingPx - iconTotal - textStartPx - textEndPx -
                    (if (countPlaceable != null) countStartPx + countEndPx else 0f) -
                    (if (showClose) closeWidth else endPaddingPx),
            ),
        )
        val textPlace = textPlaceable.measure(Constraints(maxWidth = textMaxWidth.roundToInt()))
        val countPlace = countPlaceable?.measure(Constraints())
        val countWidth = if (countPlace == null) 0f else countStartPx + countPlace.width + countEndPx

        val contentWithClose = startPaddingPx + iconTotal + textStartPx + textPlace.width + textEndPx +
            countWidth + closeWidth
        val contentWithoutClose = startPaddingPx + iconTotal + textStartPx + textPlace.width + textEndPx +
            countWidth + endPaddingPx
        val widthWithClose = min(max(contentWithClose, minWidthPx), maxAvailable)
        val widthWithoutClose = min(max(contentWithoutClose, minWidthPx), maxAvailable)
        val width = if (showClose) widthWithClose else widthWithoutClose

        val contentHeight = maxOf(
            textPlace.height.toFloat(),
            countPlace?.height?.toFloat() ?: 0f,
            iconPlaceable?.let { iconWidth } ?: 0f,
            if (showClose) closeTouchPx else 0f,
        )
        val height = max(contentHeight, minHeightPx).let {
            if (constraints.maxHeight == Constraints.Infinity) it else min(it, constraints.maxHeight.toFloat())
        }
        val widthPx = width.roundToInt()
        val heightPx = height.roundToInt()

        val backgroundWidthPx = (widthWithoutClose + (widthWithClose - widthWithoutClose) * closeFraction)
            .roundToInt().coerceIn(0, widthPx)

        fun textStart(width: Float, trailing: Float): Float {
            val extra = width - (startPaddingPx + iconTotal) - trailing - textPlace.width
            return startPaddingPx + iconTotal + textStartPx + max(0f, extra) / 2f
        }

        val textStartVisible = textStart(widthWithClose, textEndPx + countWidth + closeWidth)
        val textStartInvisible = textStart(widthWithoutClose, textEndPx + countWidth + endPaddingPx)
        val textX = textStartVisible + (textStartInvisible - textStartVisible) * (1f - closeFraction)

        val backgroundPlace = backgroundPlaceable.measure(Constraints.fixed(backgroundWidthPx, heightPx))
        val iconPlace = iconPlaceable?.measure(Constraints(maxWidth = iconWidth.roundToInt()))
        val closePlace = closePlaceable?.measure(Constraints.fixed(closeTouchWidth.roundToInt(), heightPx))
        val dotOffsetX = with(density) { CouiChipDefaults.redDotOffset.x.toPx() }
        val dotOffsetY = with(density) { CouiChipDefaults.redDotOffset.y.toPx() }
        val backgroundX = if (layoutDirection == LayoutDirection.Rtl) widthPx - backgroundWidthPx else 0
        val closeX = if (layoutDirection == LayoutDirection.Rtl) 0 else widthPx - closeTouchWidth.roundToInt()

        layout(widthPx, heightPx) {
            backgroundPlace.place(backgroundX, 0)
            if (iconPlace != null) {
                iconPlace.placeRelative(
                    (startPaddingPx + iconStartPx).roundToInt(),
                    ((heightPx - iconPlace.height) / 2f).roundToInt(),
                )
            }
            textPlaceable.placeRelative(textX.roundToInt(), ((heightPx - textPlace.height) / 2f).roundToInt())
            if (countPlace != null) {
                countPlace.placeRelative(
                    (textX + textPlace.width + countStartPx).roundToInt(),
                    ((heightPx - countPlace.height) / 2f).roundToInt(),
                )
            }
            closePlace?.place(closeX, 0)
            redDotPlaceable?.placeRelative(
                (textX + textPlace.width + dotOffsetX).roundToInt(),
                dotOffsetY.roundToInt(),
            )
        }
    }
}

/** 背景节点；宽度由父布局按关闭动画逐帧设定，颜色由选中进度插值。 */
@Composable
private fun ChipBackground(color: Color, shape: Shape) {
    Box(
        Modifier
            .zIndex(-1f)
            .drawWithContent { drawShape(color, shape) },
    )
}

internal fun DrawScope.drawShape(color: Color, shape: Shape) {
    when (val outline = shape.createOutline(Size(size.width, size.height), layoutDirection, this)) {
        is Outline.Rectangle -> drawRect(color, size = size)
        is Outline.Rounded -> drawRoundRect(
            color,
            topLeft = outline.roundRect.topLeft,
            size = outline.roundRect.size,
            cornerRadius = outline.roundRect.topLeftCornerRadius,
        )
        is Outline.Generic -> drawPath(outline.path, color)
    }
}

@Composable
private fun ChipCloseButton(
    size: Dp,
    touchSize: Dp,
    color: Color,
    enabled: Boolean,
    alpha: Float,
    iconScale: Float,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    val path = remember { PathParser().parsePathString(CouiChipPaths.close).toPath() }
    Box(
        Modifier
            .size(maxOf(touchSize, size))
            .graphicsLayer { this.alpha = alpha }
            .semantics { if (contentDescription != null) this.contentDescription = contentDescription }
            .clickable(enabled = enabled && alpha > 0f, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size).graphicsLayer { scaleX = iconScale; scaleY = iconScale }) {
            val factor = this.size.width / CouiChipPaths.closeViewport
            scale(factor, factor, pivot = Offset.Zero) { drawPath(path, color) }
        }
    }
}

/** 原厂 `TintAnimation` / `CloseIconAnimation` 的弹簧：response 0.3、bounce 0。 */
internal fun chipSpring() = spring<Float>(
    dampingRatio = 1f,
    stiffness = CouiChipDefaults.stiffness(CouiChipDefaults.tintResponse),
)

/** 原厂 `ArgbEvaluator` 的逐通道插值，不做色彩空间转换。 */
internal fun lerpColor(from: Color, to: Color, fraction: Float): Color = Color(
    red = from.red + (to.red - from.red) * fraction,
    green = from.green + (to.green - from.green) * fraction,
    blue = from.blue + (to.blue - from.blue) * fraction,
    alpha = from.alpha + (to.alpha - from.alpha) * fraction,
)
