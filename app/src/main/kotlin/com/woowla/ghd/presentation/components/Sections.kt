package com.woowla.ghd.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val preferenceSwitchHeight = 35.dp

@Composable
fun SectionCategory(
    text: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(preferenceSwitchHeight).padding(PaddingValues(bottom = 10.dp))
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h6,
            )
        }
        content()
        SectionDivider()
    }
}

@Composable
fun SectionCategorySwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(preferenceSwitchHeight).padding(PaddingValues(bottom = 10.dp))
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.weight(1F))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.height(preferenceSwitchHeight)
            )
        }
        AnimatedVisibility(visible = checked) {
            Column {
                content()
            }
        }
        SectionDivider()
    }
}

@Composable
fun SectionItem(
    title: String,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.height(preferenceSwitchHeight).padding(PaddingValues(bottom = 10.dp))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1
            )
        }
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
            )
        }
        content()
    }
}

@Composable
fun SectionItemSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(preferenceSwitchHeight).padding(PaddingValues(bottom = 10.dp))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.weight(1F))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.height(preferenceSwitchHeight)
            )
        }
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
            )
        }
        AnimatedVisibility(visible = checked) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SectionDivider() {
    Divider(
        modifier = Modifier.padding(vertical = 15.dp)
    )
}