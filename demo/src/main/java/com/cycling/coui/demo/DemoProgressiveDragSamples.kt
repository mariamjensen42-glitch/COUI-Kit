package com.cycling.coui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cycling.coui.layout.CouiPageDefaults
import com.cycling.coui.layout.Group
import com.cycling.coui.motion.couiProgressiveDrag
import com.cycling.coui.motion.rememberProgressiveDragState
import com.cycling.coui.text.CouiTextKind
import com.cycling.coui.text.Text
import com.cycling.coui.theme.CouiTheme

/** 渐进式拖动：同一手势下每行按与触发行距拿到不同的弹簧响应。 */
@Composable
internal fun ProgressiveDragSample(dark: Boolean) {
    val scheme = CouiTheme.colorSchemeFor(dark)
    val state = rememberProgressiveDragState()
    val rows = 4

    Column {
        Group(title = stringResource(R.string.progressive_drag_title)) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = CouiPageDefaults.rowPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .couiProgressiveDrag(state, triggerIndex = 0, span = rows - 1),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(rows) { row ->
                            Box(
                                Modifier
                                    .graphicsLayer { translationX = state.translation(row) }
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (row == 0) scheme.container else scheme.card),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(scheme.accent),
                                    )
                                    Text(
                                        text = stringResource(R.string.progressive_drag_row, row + 1),
                                        kind = CouiTextKind.Body,
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.progressive_drag_hint),
                    kind = CouiTextKind.ItemSummary,
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}
