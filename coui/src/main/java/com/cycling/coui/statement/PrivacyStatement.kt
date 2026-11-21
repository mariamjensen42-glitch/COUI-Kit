package com.cycling.coui.statement

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.cycling.coui.button.Button
import com.cycling.coui.button.CouiButtonStyle
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/**
 * 全屏隐私声明，对应 ColorOS 17 内嵌 COUI 的 `COUIFullPageStatement` 与
 * `coui_full_page_statement.xml`。
 *
 * 结构：居中标题（超过最大高度可滚动）→ 占满剩余空间的正文（最多 248dp 后滚动）→
 * 174dp 宽的确认按钮 → 底部文字拒绝按钮。最窄屏宽小于 480dp 且处于横屏时，原厂改用
 * 底部一行两个等宽按钮（`button_layout_land`），这里按同一判定切换。
 *
 * 正文通过 [AnnotatedString] 承载链接等行内样式，链接样式写在 `LinkAnnotation` 里；
 * 纯文本用 [PrivacyStatement] 的 `String` 重载。
 *
 * 与原厂的差异：正文不做 `requiresFadingEdge` + 32dp `fadingEdgeLength` 的上下渐隐
 * （Compose 没有等价的滚动渐隐开关，由宿主外壳决定），小屏竖屏的
 * `coui_full_page_statement_tiny` 布局未移植，标题/正文不随系统字体缩放做
 * `adaptFontSize` 自动缩字。
 */
@Composable
fun PrivacyStatement(
    modifier: Modifier = Modifier,
    title: String,
    statement: AnnotatedString,
    confirmText: String,
    exitText: String,
    onConfirm: () -> Unit,
    onExit: () -> Unit,
    dark: Boolean = CouiTheme.isDark,
) {
    StatementLayout(
        modifier = modifier,
        title = title,
        statement = statement,
        confirmText = confirmText,
        exitText = exitText,
        onConfirm = onConfirm,
        onExit = onExit,
        dark = dark,
    )
}

/** 纯文本入口，行为与 [PrivacyStatement] 的 [AnnotatedString] 重载一致。 */
@Composable
fun PrivacyStatement(
    modifier: Modifier = Modifier,
    title: String,
    statement: String,
    confirmText: String,
    exitText: String,
    onConfirm: () -> Unit,
    onExit: () -> Unit,
    dark: Boolean = CouiTheme.isDark,
) {
    PrivacyStatement(
        modifier = modifier,
        title = title,
        statement = AnnotatedString(statement),
        confirmText = confirmText,
        exitText = exitText,
        onConfirm = onConfirm,
        onExit = onExit,
        dark = dark,
    )
}

@Composable
private fun StatementLayout(
    modifier: Modifier,
    title: String,
    statement: AnnotatedString,
    confirmText: String,
    exitText: String,
    onConfirm: () -> Unit,
    onExit: () -> Unit,
    dark: Boolean,
) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val configuration = LocalConfiguration.current
    // 原厂 isSmallScreen() && !isPortrait()：最窄屏宽 < 480dp 的横屏改用横排按钮。
    val compactLandscape = configuration.smallestScreenWidthDp < CouiStatementDefaults.compactLandscapeSmallestWidth &&
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Column(modifier.fillMaxSize()) {
        val titleScroll = rememberScrollState()
        Text(
            text = title,
            style = CouiStatementDefaults.titleStyle,
            color = scheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(
                    start = CouiStatementDefaults.horizontalPadding,
                    end = CouiStatementDefaults.horizontalPadding,
                    top = CouiStatementDefaults.titleTopMargin,
                    bottom = CouiStatementDefaults.titleBottomMargin,
                )
                .heightIn(max = CouiStatementDefaults.titleMaxHeight)
                .verticalScroll(titleScroll),
        )
        // 正文占剩余空间，最多 248dp 后自身滚动；内边距随内容滚动，与原厂 TextView 的内边距一致。
        // 富文本 + 自定义 TextStyle 没有对应的 Text 重载，这里直接用 BasicText，颜色显式给出。
        val statementScroll = rememberScrollState()
        BasicText(
            text = statement,
            style = CouiStatementDefaults.bodyStyle.copy(color = scheme.onSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
                .weight(1f, fill = false)
                .padding(horizontal = CouiStatementDefaults.horizontalPadding)
                .heightIn(max = CouiStatementDefaults.statementMaxHeight)
                .verticalScroll(statementScroll)
                .padding(
                    top = CouiStatementDefaults.statementPaddingTop,
                    bottom = CouiStatementDefaults.statementPaddingBottom,
                ),
        )
        if (compactLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(
                    top = CouiStatementDefaults.landTopPadding,
                    bottom = CouiStatementDefaults.landBottomPadding,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.weight(1f)
                        .padding(
                            start = CouiStatementDefaults.landEdgeMargin,
                            end = CouiStatementDefaults.landCenterGap,
                        )
                        .height(CouiStatementDefaults.landButtonHeight),
                    onClick = onExit,
                    label = exitText,
                    style = CouiButtonStyle.Secondary,
                    dark = dark,
                )
                Button(
                    modifier = Modifier.weight(1f)
                        .padding(
                            start = CouiStatementDefaults.landCenterGap,
                            end = CouiStatementDefaults.landEdgeMargin,
                        )
                        .height(CouiStatementDefaults.landButtonHeight),
                    onClick = onConfirm,
                    label = confirmText,
                    style = CouiButtonStyle.Primary,
                    dark = dark,
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier
                        .padding(top = CouiStatementDefaults.confirmTopMargin)
                        .width(CouiStatementDefaults.confirmButtonWidth),
                    onClick = onConfirm,
                    label = confirmText,
                    style = CouiButtonStyle.Primary,
                    dark = dark,
                )
                ExitText(
                    text = exitText,
                    onClick = onExit,
                    color = scheme.accent,
                    modifier = Modifier.padding(
                        top = CouiStatementDefaults.confirmBottomMargin,
                        bottom = CouiStatementDefaults.exitBottomMargin,
                    ),
                )
            }
        }
    }
}

/**
 * 底部文字按钮：原厂最小高度 32dp，整块可点。
 */
@Composable
private fun ExitText(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    minHeight: Dp = CouiStatementDefaults.exitMinHeight,
) {
    Box(modifier = modifier.heightIn(min = minHeight).clickable(onClick = onClick),
        contentAlignment = Alignment.Center) {
        Text(text = text, style = CouiStatementDefaults.exitStyle, color = color)
    }
}
