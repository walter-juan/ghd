package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Usb
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.woowla.ghd.presentation.components.Chip
import com.woowla.ghd.presentation.components.ImageChip
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.SectionCategory
import com.woowla.ghd.presentation.components.SectionCategorySwitch
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.SectionItemSwitch
import com.woowla.ghd.presentation.components.SwitchText
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.utils.MaterialColors

@Composable
fun ComponentsSampleScreen(onBackClick: (() -> Unit)) {
    Screen(
        topBar = {
            TopBar(
                title = "Components",
                navOnClick = onBackClick
            )
        }
    ) {
        item {
            Column {
                Title("Typography")
                TypographySample()
                Title("SwitchText")
                SwitchTextSample()
                Title("Sections")
                SectionsSample()
                Title("Chips")
                ChipsSample()
            }
        }
    }
}

@Composable
private fun Title(text: String) {
    Spacer(modifier = Modifier.padding(10.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        color = MaterialColors.OnBlueGray200,
        modifier = Modifier
            .background(color = MaterialColors.BlueGray200, shape = CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth()
    )
    Spacer(modifier = Modifier.padding(10.dp))
}

@Composable
private fun SwitchTextSample() {
    Text(text = "A text with switch")
    val checkedState = remember { mutableStateOf(true) }
    SwitchText(
        text = "Click to change the switch",
        checked = checkedState.value,
        onCheckedChange = { checkedState.value = it }
    )
}

@Composable
private fun ChipsSample() {
    Text(text = "Chips without image")
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        val checkedState1 = remember { mutableStateOf(false) }
        val checkedState2 = remember { mutableStateOf(true) }
        val checkedState3 = remember { mutableStateOf(false) }
        val checkedState4 = remember { mutableStateOf(true) }
        val checkedState5 = remember { mutableStateOf(true) }
        val checkedState6 = remember { mutableStateOf(true) }
        Chip(
            text = "Chip 1",
            selected = checkedState1.value,
            onSelectedChanged = { checkedState1.value = it },
            modifier = Modifier.padding(5.dp)
        )
        Chip(
            text = "Chip 2",
            selected = checkedState3.value,
            onSelectedChanged = { checkedState3.value = it },
            modifier = Modifier.padding(5.dp)
        )
        Chip(
            text = "Chip 2",
            selected = checkedState2.value,
            onSelectedChanged = { checkedState2.value = it },
            modifier = Modifier.padding(5.dp)
        )
        Chip(
            text = "Chip 3",
            selected = checkedState4.value,
            onSelectedChanged = { checkedState4.value = it },
            modifier = Modifier.padding(5.dp)
        )
        Chip(
            text = "Chip 4",
            selected = checkedState5.value,
            onSelectedChanged = { checkedState5.value = it },
            modifier = Modifier.padding(5.dp)
        )
        Chip(
            text = "Chip 5",
            selected = checkedState6.value,
            onSelectedChanged = { checkedState6.value = it },
            modifier = Modifier.padding(5.dp)
        )
    }
    Text(text = "Chips with image")
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        val checkedState1 = remember { mutableStateOf(false) }
        val checkedState2 = remember { mutableStateOf(true) }
        val checkedState3 = remember { mutableStateOf(false) }
        val checkedState4 = remember { mutableStateOf(true) }
        val checkedState5 = remember { mutableStateOf(true) }
        val checkedState6 = remember { mutableStateOf(true) }
        ImageChip(
            text = "Chip 1",
            painter = rememberVectorPainter(Icons.Default.Home),
            selected = checkedState1.value,
            onSelectedChanged = { checkedState1.value = it },
            modifier = Modifier.padding(5.dp)
        )
        ImageChip(
            text = "Chip 2",
            painter = rememberVectorPainter(Icons.Default.Delete),
            selected = checkedState3.value,
            onSelectedChanged = { checkedState3.value = it },
            modifier = Modifier.padding(5.dp)
        )
        ImageChip(
            text = "Chip 2",
            painter = rememberVectorPainter(Icons.Default.Settings),
            selected = checkedState2.value,
            onSelectedChanged = { checkedState2.value = it },
            modifier = Modifier.padding(5.dp)
        )
        ImageChip(
            text = "Chip 3",
            painter = rememberVectorPainter(Icons.Default.Usb),
            selected = checkedState4.value,
            onSelectedChanged = { checkedState4.value = it },
            modifier = Modifier.padding(5.dp)
        )
        ImageChip(
            text = "Chip 4",
            painter = rememberVectorPainter(Icons.Default.Star),
            selected = checkedState5.value,
            onSelectedChanged = { checkedState5.value = it },
            modifier = Modifier.padding(5.dp)
        )
        ImageChip(
            text = "Chip 5",
            painter = rememberVectorPainter(Icons.Default.Stadium),
            selected = checkedState6.value,
            onSelectedChanged = { checkedState6.value = it },
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
private fun SectionsSample() {
    val checkedState1 = remember { mutableStateOf(true) }
    val checkedState2 = remember { mutableStateOf(true) }
    SectionCategory("This is a SectionCategory") {
        SectionItem(title = "This is a SectionItem", description = "And this its description") {
            Text("Also you can add whatever you want as a content")
            Button(onClick = {}) { Text("Sample button") }
        }
        SectionItemSwitch(
            title = "This is a SectionItemSwitch",
            description = "And this its description",
            checked = checkedState1.value,
            onCheckedChange = { checkedState1.value = it }
        ) {
            Text("Also like a SectionItem you can add whatever you want as a content")
            Text("Also you have a SectionCategorySwitch for the categories")
            Button(onClick = {}) { Text("Sample button") }
        }
    }
    SectionCategorySwitch(
        text = "This is a SectionCategorySwitch",
        checked = checkedState2.value,
        onCheckedChange = { checkedState2.value = it }
    ) {
        SectionItem(title = "This is a SectionItem", description = "And this its description") {
            Text("Also you can add whatever you want as a content")
            Button(onClick = {}) { Text("Sample button") }
        }
    }
}

@Composable
private fun TypographySample() {
    Text(
        text = "The main first title to start is the H4",
        style = MaterialTheme.typography.body1
    )
    Divider()
    Text(
        text = "Header H1",
        style = MaterialTheme.typography.h1
    )
    Text(
        text = "Header H2",
        style = MaterialTheme.typography.h2
    )
    Text(
        text = "Header H3",
        style = MaterialTheme.typography.h3
    )
    Text(
        text = "Header H4",
        style = MaterialTheme.typography.h4
    )
    Text(
        text = "Header H5",
        style = MaterialTheme.typography.h5
    )
    Text(
        text = "Header H6",
        style = MaterialTheme.typography.h6
    )
    Text(
        text = "Subtitle1",
        style = MaterialTheme.typography.subtitle1
    )
    Text(
        text = "Subtitle2",
        style = MaterialTheme.typography.subtitle2
    )
    Text(
        text = "Body1",
        style = MaterialTheme.typography.body1
    )
    Text(
        text = "Body2",
        style = MaterialTheme.typography.body2
    )
    Text(
        text = "Button",
        style = MaterialTheme.typography.button
    )
    Text(
        text = "Caption",
        style = MaterialTheme.typography.caption
    )
    Text(
        text = "Overline",
        style = MaterialTheme.typography.overline
    )
}