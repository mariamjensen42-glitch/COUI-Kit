package com.cycling.coui.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.cycling.coui.R
import com.cycling.coui.backdrop.CouiBackdrop
import com.cycling.coui.pageindicator.PageIndicator
import com.cycling.coui.theme.CouiTheme
import kotlinx.coroutines.launch

/** 引导页内容。图片由宿主提供可组合内容，按原厂高度裁切。 */
@Immutable
data class CouiGuidePage(
    val title: String,
    val description: String? = null,
    val image: (@Composable () -> Unit)? = null,
)

/**
 * 多页引导弹窗。底部主按钮在末页切换为「知道了」，末页之前的次级按钮为「跳过」。
 *
 * 卡片底部操作区随翻页进度展开：主按钮从半宽长到整行，次级按钮同步淡出。
 * 按钮宽度、进度插值与弹簧参数取自原厂 `COUIGuideDialogButtonLayout`。
 */
@Composable
fun GuideDialog(
    pages: List<CouiGuidePage>,
    onFinish: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onSkip: (() -> Unit)? = null,
    onPageChange: ((Int) -> Unit)? = null,
    skipLabel: String = stringResource(R.string.coui_dialog_guide_skip),
    nextLabel: String = stringResource(R.string.coui_dialog_guide_next),
    finishLabel: String = stringResource(R.string.coui_dialog_guide_finish),
    imageHeight: Dp = CouiGuideDialogDefaults.imageHeight,
    dark: Boolean = CouiTheme.isDark,
    colors: CouiDialogColors = CouiDialogDefaults.themeColors(dark),
    accent: Color = CouiTheme.colorSchemeFor(dark).accent,
    shape: Shape = CouiGuideDialogDefaults.shape,
    cornerRadius: Dp = Dp.Unspecified,
    material: CouiDialogMaterial? = null,
    backdrop: CouiBackdrop? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    visible: Boolean? = null,
) {
    require(pages.isNotEmpty()) { "引导弹窗至少需要一页" }
    CouiDialogWindow(modifier, onDismissRequest, colors, dark, CouiDialogPlacement.Bottom, shape,
        cornerRadius, properties, material, backdrop, visible) {
        val pager = rememberPagerState(pageCount = { pages.size })
        val scope = rememberCoroutineScope()
        val single = pages.size == 1
        val lastIndex = pages.size - 1
        val lastPage = pager.currentPage == lastIndex
        // 原厂取「距末页」的滚动进度，先过 ease 再由弹簧收敛；宽度与按钮文字共用该进度。
        val target = CouiGuideDialogDefaults.ease(
            ((pager.currentPage + pager.currentPageOffsetFraction - (pages.size - 2))).coerceIn(0f, 1f)
        )
        val progress = remember { Animatable(0f) }
        LaunchedEffect(target) {
            progress.animateTo(target, spring(dampingRatio = 1f,
                stiffness = CouiGuideDialogDefaults.stiffness(CouiGuideDialogDefaults.buttonSpringResponse),
                visibilityThreshold = .001f))
        }
        val fraction = progress.value
        val skipAlpha = ((CouiGuideDialogDefaults.secondaryFadeEndProgress - fraction) /
            CouiGuideDialogDefaults.secondaryFadeEndProgress).coerceIn(0f, 1f)
        val showSkip = !single && onSkip != null
        LaunchedEffect(pager.settledPage) { if (!single) onPageChange?.invoke(pager.settledPage) }
        Column(Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pager,
                modifier = Modifier.fillMaxWidth().weight(1f),
                userScrollEnabled = !single,
            ) { index ->
                val page = pages[index]
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(
                        bottom = if (single) CouiGuideDialogDefaults.singlePageBottomMargin
                        else CouiGuideDialogDefaults.pagerBottomMargin),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val image = page.image
                    if (image != null) Box(Modifier.fillMaxWidth().height(imageHeight).clipToBounds(),
                        contentAlignment = Alignment.Center) { image() }
                    DialogText(
                        text = page.title,
                        kind = DialogTextKind.Title,
                        color = colors.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = CouiGuideDialogDefaults.textHorizontalPadding)
                            .padding(top = CouiGuideDialogDefaults.titleTopPadding,
                                bottom = CouiGuideDialogDefaults.titleBottomPadding),
                    )
                    val description = page.description
                    if (!description.isNullOrEmpty()) DialogText(
                        text = description,
                        kind = DialogTextKind.Message,
                        color = colors.message,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = CouiGuideDialogDefaults.textHorizontalPadding),
                    )
                }
            }
            if (!single) PageIndicator(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = CouiGuideDialogDefaults.indicatorBottomMargin),
                count = pages.size,
                selectedIndex = pager.currentPage,
                dark = dark,
            )
            BoxWithConstraints(Modifier.fillMaxWidth()
                .padding(horizontal = CouiGuideDialogDefaults.buttonHorizontalPadding)
                .padding(bottom = CouiGuideDialogDefaults.buttonBottomMargin)) {
                val spacing = CouiGuideDialogDefaults.buttonSpacing
                val half = ((maxWidth - spacing) * 0.5f).coerceAtLeast(0.dp)
                val nextWidth = if (showSkip) half + (half + spacing) * fraction else maxWidth
                if (showSkip && skipAlpha > .02f) DialogButton(
                    modifier = Modifier.align(Alignment.CenterStart).width(half)
                        .graphicsLayer { alpha = skipAlpha },
                    text = skipLabel,
                    onClick = { onSkip?.invoke() },
                    dark = dark,
                    style = CouiDialogActionStyle.Secondary,
                    material = material != null,
                )
                DialogButton(
                    modifier = Modifier.align(Alignment.CenterEnd).width(nextWidth),
                    text = if (single || lastPage) finishLabel else nextLabel,
                    onClick = {
                        if (single || lastPage) onFinish() else scope.launch {
                            pager.animateScrollToPage(pager.currentPage + 1,
                                animationSpec = tween(CouiGuideDialogDefaults.scrollDurationMillis,
                                    easing = CouiGuideDialogDefaults.scrollEasing))
                        }
                    },
                    dark = dark,
                    style = CouiDialogActionStyle.Primary,
                    material = material != null,
                    colors = CouiDialogDefaults.primaryActionColors(accent),
                )
            }
        }
    }
}
