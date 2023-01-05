package com.woowla.ghd.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.woowla.ghd.presentation.app.AppColors

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
    val alphaAnimationDurationMillis = 150
    val contentAlpha: Float by animateFloatAsState(
        targetValue = if (hover) 0.5f else 1f,
        animationSpec = tween(
            durationMillis = alphaAnimationDurationMillis,
            easing = LinearEasing,
        )
    )
    val hoverContentAlpha: Float by animateFloatAsState(
        targetValue = if (hover) 1f else 0f,
        animationSpec = tween(
            durationMillis = alphaAnimationDurationMillis,
            easing = LinearEasing,
        )
    )

    val typography = MaterialTheme.typography
    val styledHeadlineText =  applyTextStyle(typography.subtitle1, ContentAlpha.high, headlineText)!!
    val styledSupportingText = applyTextStyle(typography.body2, ContentAlpha.medium, supportingText)
    val styledOverlineText = applyTextStyle(typography.caption, ContentAlpha.high, overlineText)
    val paddingValues = PaddingValues(8.dp)

    Card(
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
        Box(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = if (hover) { AppColors.cardHoverBackground() } else { if (selected) { AppColors.cardSelectedBackground() } else { AppColors.cardBackground() } } ),
        ) { }

        Row(
            modifier = Modifier
                .alpha(contentAlpha)
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
                if (styledOverlineText != null) {
                    styledOverlineText()
                }
                styledHeadlineText.invoke()
                if (styledSupportingText != null) {
                    styledSupportingText()
                }
            }

            if (trailingContent != null) {
                trailingContent(paddingValues)
            }
        }

        if (hoverContent != null) {
            Box(
                modifier = Modifier
                    .alpha(hoverContentAlpha)
                    .fillMaxHeight()
            ) {
                hoverContent(paddingValues)
            }
        }
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    contentAlpha: Float,
    icon: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (icon == null) return null
    return {
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, icon)
        }
    }
}