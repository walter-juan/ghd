package com.woowla.ghd.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    text: String,
    selected: Boolean,
    onSelectedChanged: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = if (selected) { MaterialTheme.colors.secondary } else { Color.Transparent },
        contentColor = if (selected) { MaterialTheme.colors.onSecondary } else { Color.LightGray },
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) { MaterialTheme.colors.secondaryVariant } else { Color.LightGray }
        ),
        modifier = modifier.clickable { onSelectedChanged?.invoke(!selected) }
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ImageChip(
    text: String,
    painter: Painter,
    selected: Boolean,
    onSelectedChanged: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) {
            MaterialTheme.colors.secondary
        } else {
            Color.Transparent
        },
        contentColor = if (selected) {
            MaterialTheme.colors.onSecondary
        } else {
            Color.LightGray
        },
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) {
                MaterialTheme.colors.secondaryVariant
            } else {
                Color.LightGray
            }
        ),
        modifier = modifier.clickable { onSelectedChanged?.invoke(!selected) }
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(
                    if (selected) {
                        MaterialTheme.colors.secondaryVariant
                    } else {
                        Color.LightGray
                    }
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 2.dp).fillMaxHeight()
            )
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 2.dp, end = 8.dp)
            )
        }
    }
}
