package com.woowla.ghd.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize

@Composable
fun <T: Any?> OutlinedSelectField(
    values: List<Pair<T, String>>,
    selected: T? = null,
    emptyText: String = "",
    modifier: Modifier = Modifier,
    onSelected: (T, String) -> Unit,
) {
    val selectedText = values.firstOrNull { pair -> pair.first == selected }?.second ?: emptyText

    var dropDownExpanded by remember { mutableStateOf(false) }
    var textFieldText by remember { mutableStateOf(selectedText) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val textFieldTrailingIcon = if (dropDownExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textFieldText,
            onValueChange = { textFieldText = it },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // this value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable { dropDownExpanded = !dropDownExpanded },
            trailingIcon = { Icon(textFieldTrailingIcon, contentDescription = null) }
        )
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){ textFieldSize.width.toDp() })
        ) {
            values.forEach { (value, text) ->
                DropdownMenuItem(onClick = {
                    textFieldText = text
                    dropDownExpanded = false
                    onSelected.invoke(value, text)
                }) {
                    Text(text = text)
                }
            }
        }
    }
}