package com.woowla.ghd.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardListItem(
    headlineText: (@Composable () -> Unit),
    overlineText: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable (PaddingValues) -> Unit)? = null,
    trailingContent: (@Composable (PaddingValues) -> Unit)? = null,
    hoverContent: (@Composable (PaddingValues) -> Unit)? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var hover by remember { mutableStateOf(false) }
    val paddingValues = PaddingValues(8.dp)
    val alphaAnimationDurationMillis = 150
    val selectedAlpha: Float by animateFloatAsState(
        targetValue = if (selected) 0.25f else 1f,
        animationSpec = tween(
            durationMillis = alphaAnimationDurationMillis,
            easing = LinearEasing,
        )
    )

    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
            .onPointerEvent(PointerEventType.Enter) { hover = true  }
            .onPointerEvent(PointerEventType.Exit) { hover = false },
    ) {
        Box {
            Row(
                modifier = Modifier
                    .alpha(selectedAlpha)
                    .fillMaxHeight()
            ) {
                if (leadingContent != null) {
                    leadingContent(paddingValues)
                }

                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (overlineText != null) {
                        ProvideTextStyle(MaterialTheme.typography.labelSmall, overlineText)
                    }
                    ProvideTextStyle(MaterialTheme.typography.bodyLarge, headlineText)
                    if (supportingText != null) {
                        ProvideTextStyle(MaterialTheme.typography.bodyMedium, supportingText)
                    }
                }

                if (trailingContent != null) {
                    trailingContent(paddingValues)
                }
            }
            this@ElevatedCard.AnimatedVisibility(
                visible = hover,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F))
                    .fillMaxSize()) {
                    hoverContent?.invoke(paddingValues)
                }
            }
        }
    }
}