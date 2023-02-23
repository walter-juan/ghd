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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IconCard(
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable (paddingValues: PaddingValues, hover: Boolean) -> Unit)? = null,
    trailingContent: (@Composable (paddingValues: PaddingValues, hover: Boolean) -> Unit)? = null,
    hoverContent: (@Composable (PaddingValues) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var hover by remember { mutableStateOf(false) }

    val alphaAnimationDurationMillis = 150
    val paddingValues = PaddingValues(10.dp)

    val selectedAlpha: Float by animateFloatAsState(
        targetValue = if (selected) 0.35f else 1f,
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
            .onPointerEvent(PointerEventType.Enter) { hover = true }
            .onPointerEvent(PointerEventType.Exit) { hover = false },
    ) {
        Box {
            Row(modifier = Modifier.alpha(selectedAlpha)) {
                if (leadingContent != null) {
                    leadingContent(paddingValues, hover)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.padding(paddingValues).weight(1F),
                ) {
                    content.invoke()
                }

                if (trailingContent != null) {
                    trailingContent(paddingValues, hover)
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

@Composable
fun IconCardSpacer(
    modifier: Modifier = Modifier.padding(vertical = 2.dp),
) {
    Spacer(modifier = modifier)
}

@Composable
fun IconCardRowTitle(
    text: String,
    icon: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.Transparent,
    rowColor: Color = Color.Transparent,
) {
    IconCardRowTitle(
        text = buildAnnotatedString { append(text) },
        icon = icon,
        iconTint = iconTint,
        iconBackgroundColor = iconBackgroundColor,
        rowColor = rowColor
    )
}

@Composable
fun IconCardRowTitle(
    text: AnnotatedString,
    icon: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.Transparent,
    rowColor: Color = Color.Transparent,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = rowColor, shape = RoundedCornerShape(5.dp))
    ) {
        val modifier = Modifier
            .padding(end = 10.dp)
            .background(color = iconBackgroundColor, shape = RoundedCornerShape(5.dp))
            .padding(5.dp)
            .size(20.dp)
        if (icon == null) {
            Box(modifier = modifier)
        } else {
            Icon(painter = icon, contentDescription = null, tint = iconTint, modifier = modifier)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun IconCardRowContent(
    text: String,
    icon: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.Transparent,
    rowColor: Color = Color.Transparent,
) {
    IconCardRowContent(
        text = buildAnnotatedString { append(text) },
        icon = icon,
        iconTint = iconTint,
        iconBackgroundColor = iconBackgroundColor,
        rowColor = rowColor
    )
}

@Composable
fun IconCardRowContent(
    text: AnnotatedString,
    icon: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.Transparent,
    rowColor: Color = Color.Transparent,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = rowColor, shape = RoundedCornerShape(5.dp))
    ) {
        val modifier = Modifier
            .padding(end = 10.dp)
            .background(color = iconBackgroundColor, shape = RoundedCornerShape(5.dp))
            .padding(5.dp)
            .size(20.dp)
        if (icon == null) {
            Box(modifier = modifier)
        } else {
            Icon(painter = icon, contentDescription = null, tint = iconTint, modifier = modifier)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun IconCardRowSmallContent(
    text: String,
    icon: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.Transparent,
    rowColor: Color = Color.Transparent,
) {
    IconCardRowSmallContent(
        text = buildAnnotatedString { append(text) },
        icon = icon,
        iconTint = iconTint,
        iconBackgroundColor = iconBackgroundColor,
        rowColor = rowColor
    )
}

@Composable
fun IconCardRowSmallContent(
    text: AnnotatedString,
    icon: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.Transparent,
    rowColor: Color = Color.Transparent,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = rowColor, shape = RoundedCornerShape(5.dp))
    ) {
        val modifier = Modifier
            .padding(start = 5.dp, end = 15.dp)
            .background(color = iconBackgroundColor, shape = RoundedCornerShape(5.dp))
            .padding(2.5.dp)
            .size(15.dp)
        if (icon == null) {
            Box(modifier = modifier)
        } else {
            Icon(painter = icon, contentDescription = null, tint = iconTint, modifier = modifier)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}