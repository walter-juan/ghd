package com.woowla.ghd.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun LabelledRadioButton(
    label: String,
    description: String? = null,
    enabled: Boolean = true,
    selected: Boolean,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable(
                    enabled = enabled,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClick() }
                )
                .requiredHeight(ButtonDefaults.MinHeight)
        ) {
            RadioButton(selected = selected, enabled = enabled, onClick = null)
            Spacer(Modifier.size(5.dp))
            Text(
                text = label,
                modifier = Modifier.then(if (!enabled) Modifier.alpha(ContentAlpha.disabled) else Modifier)
            )
        }
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.then(if (!enabled) Modifier.alpha(ContentAlpha.disabled) else Modifier)
            )
        }
    }
}