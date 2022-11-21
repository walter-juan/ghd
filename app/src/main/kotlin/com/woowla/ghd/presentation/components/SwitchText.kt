package com.woowla.ghd.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwitchText(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(preferenceSwitchHeight).padding(PaddingValues(bottom = 10.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1F))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.height(preferenceSwitchHeight)
        )
    }
}