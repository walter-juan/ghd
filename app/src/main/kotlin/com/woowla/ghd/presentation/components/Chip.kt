package com.woowla.ghd.presentation.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ColoredFilterChip(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector?,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = color.copy(alpha = 0.5F),
        ),
        colors = FilterChipDefaults.filterChipColors(
            labelColor = color,
            iconColor = color,
            selectedContainerColor = color.copy(alpha = 0.1F),
            selectedLabelColor = color,
            selectedLeadingIconColor = color,
            selectedTrailingIconColor = color,
        ),
        label = {
            Text(
                text = text,
            )
        },
        leadingIcon = icon?.let {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        },
    )
}

