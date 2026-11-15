package com.itos.coui.topappbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp as lerpFloat
import com.itos.coui.R
import com.itos.coui.effect.CouiMaskType
import com.itos.coui.effect.couiStateMask
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.theme.CouiText
import com.itos.coui.theme.CouiTheme

/**
 * 用户主页顶栏：圆形头像、主副标题与关注按钮。对应原厂 `COUIUserFollowView`。
 *
 * 副标题为空时只显示主标题并把两者整体垂直居中；有关注态变化时按钮底色、内容色与字号
 * 在 [CouiUserFollowDefaults.transitionDurationMillis] 内过渡。[fill] 为 true 时按钮贴到末端，
 * 标题与副标题占满中间空间；为 false 时按钮紧跟文字。
 *
 * @param avatar 头像内容，默认是一个空白的圆形占位。只渲染一个布局节点。
 */
@Composable
fun UserFollowBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    following: Boolean = false,
    onFollowingChange: (Boolean) -> Unit = {},
    fill: Boolean = false,
    avatar: (@Composable () -> Unit)? = null,
    followLabel: String = stringResource(R.string.coui_user_follow_label),
    followingLabel: String = stringResource(R.string.coui_user_follow_following_label),
    enabled: Boolean = true,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiUserFollowColors = CouiUserFollowDefaults.colors(dark),
    metrics: CouiUserFollowMetrics = CouiUserFollowDefaults.metrics,
) {
    val spec = tween<Float>(
        durationMillis = CouiUserFollowDefaults.transitionDurationMillis,
        easing = CouiUserFollowDefaults.transitionEasing,
    )
    val followFraction by animateFloatAsState(if (following) 1f else 0f, spec, label = "follow")
    val fillFraction by animateFloatAsState(if (fill) 1f else 0f, spec, label = "fill")
    val showSubtitle = !subtitle.isNullOrEmpty()
    val subtitleFraction by animateFloatAsState(
        targetValue = if (showSubtitle) 1f else 0f,
        animationSpec = spec,
        label = "subtitle",
    )

    val titleStyle = CouiUserFollowDefaults.titleTextStyle()
    val subtitleStyle = CouiUserFollowDefaults.subtitleTextStyle()
    val buttonTextStyle = CouiUserFollowDefaults.buttonTextStyle(
        lerpFloat(metrics.followTextSize.value, metrics.followingTextSize.value, followFraction).sp,
    )
    val alpha = if (enabled) 1f else 0.3f

    Layout(
        modifier = modifier.heightIn(min = metrics.height).graphicsLayer { this.alpha = alpha },
        content = {
            UserFollowAvatar(avatar, metrics, colors)
            CouiText(
                text = title,
                color = colors.title,
                style = titleStyle,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
            CouiText(
                text = subtitle.orEmpty(),
                color = colors.subtitle,
                style = subtitleStyle,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.graphicsLayer { this.alpha = subtitleFraction },
            )
            FollowButton(
                label = if (following) followingLabel else followLabel,
                container = lerp(colors.buttonContainer, colors.buttonFollowedContainer, followFraction),
                content = lerp(colors.buttonContent, colors.buttonFollowedContent, followFraction),
                style = buttonTextStyle,
                metrics = metrics,
                enabled = enabled,
                dark = dark,
                onClick = { onFollowingChange(!following) },
            )
        },
    ) { measurables, constraints ->
        val horizontalPadding = metrics.horizontalPadding.roundToPx()
        val avatarSize = metrics.avatarSize.roundToPx()
        val avatarGap = metrics.avatarGap.roundToPx()
        val textGap = metrics.textGap.roundToPx()
        val buttonWidth = metrics.buttonWidth.roundToPx()
        val buttonHeight = metrics.buttonHeight.roundToPx()
        val available = (constraints.maxWidth - horizontalPadding * 2).coerceAtLeast(0)
        val textMaxWidth = (available - avatarSize - avatarGap - textGap - buttonWidth).coerceAtLeast(0)

        val avatarPlaceable = measurables[0].measure(Constraints.fixed(avatarSize, avatarSize))
        val titlePlaceable = measurables[1].measure(Constraints(maxWidth = textMaxWidth))
        val subtitlePlaceable = measurables[2].measure(Constraints(maxWidth = textMaxWidth))
        val buttonPlaceable = measurables[3].measure(Constraints.fixed(buttonWidth, buttonHeight))

        val blockHeight = titlePlaceable.height + subtitlePlaceable.height * subtitleFraction
        val minHeight = metrics.height.roundToPx()
        val contentHeight = maxOf(minHeight, blockHeight.toInt(), avatarSize, buttonHeight)
        val height = if (constraints.hasFixedHeight) constraints.maxHeight
        else contentHeight.coerceAtMost(constraints.maxHeight)
        val width = if (constraints.hasFixedWidth) constraints.maxWidth
        else (constraints.minWidth).coerceAtLeast(
            horizontalPadding * 2 + avatarSize + avatarGap + textGap + buttonWidth +
                maxOf(titlePlaceable.width, subtitlePlaceable.width),
        ).coerceAtMost(constraints.maxWidth)

        layout(width, height) {
            val centerY = height / 2
            avatarPlaceable.place(horizontalPadding, centerY - avatarSize / 2)
            val textX = horizontalPadding + avatarSize + avatarGap
            val titleY = (centerY - blockHeight / 2f).toInt()
            titlePlaceable.place(textX, titleY)
            if (showSubtitle) subtitlePlaceable.place(textX, titleY + titlePlaceable.height)

            val textRight = textX + maxOf(titlePlaceable.width, subtitlePlaceable.width)
            val packedX = (textRight + textGap).coerceAtMost(width - horizontalPadding - buttonWidth)
            val endX = width - horizontalPadding - buttonWidth
            val buttonX = lerpFloat(packedX.toFloat(), endX.toFloat(), fillFraction)
            buttonPlaceable.place(buttonX.toInt(), centerY - buttonHeight / 2)
        }
    }
}

@Composable
private fun UserFollowAvatar(
    avatar: (@Composable () -> Unit)?,
    metrics: CouiUserFollowMetrics,
    colors: CouiUserFollowColors,
) {
    Box(
        modifier = Modifier
            .size(metrics.avatarSize)
            .clip(CouiCapsuleShape)
            .background(colors.avatarBackground)
            .border(metrics.avatarBorderWidth, colors.avatarBorder, CouiCapsuleShape),
    ) {
        if (avatar != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { avatar() }
        }
    }
}

@Composable
private fun FollowButton(
    label: String,
    container: Color,
    content: Color,
    style: TextStyle,
    metrics: CouiUserFollowMetrics,
    enabled: Boolean,
    dark: Boolean,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(metrics.buttonWidth, metrics.buttonHeight)
            .clip(CouiCapsuleShape)
            .background(container)
            .couiStateMask(source, dark, enabled, CouiMaskType.HoverPress, CouiCapsuleShape)
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CouiText(text = label, color = content, style = style, maxLines = 1, overflow = TextOverflow.Clip)
    }
}
