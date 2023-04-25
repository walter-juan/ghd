package com.woowla.ghd.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.woowla.compose.remixicon.ArrowsArrowDownSLine
import com.woowla.compose.remixicon.ArrowsArrowUpSLine
import com.woowla.compose.remixicon.RemixiconPainter

@OptIn(ExperimentalMaterial3Api::class)
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
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    var textFieldText by remember { mutableStateOf(selectedText) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val textFieldTrailingIcon = if (dropDownExpanded) {
        RemixiconPainter.ArrowsArrowUpSLine
    } else {
        RemixiconPainter.ArrowsArrowDownSLine
    }

    Column(
        modifier = modifier
    ) {
        Box {
            OutlinedTextField(
                value = textFieldText,
                onValueChange = { textFieldText = it },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // this value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    }
                    .focusRequester(focusRequester),
                trailingIcon = { Icon(textFieldTrailingIcon, contentDescription = null, modifier = Modifier.size(25.dp)) }
            )
            // this box here is a hack because the OutlinedTextField clickable (and onClick) without being disabled doesn't work
            if (!dropDownExpanded) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            onClick = {
                                dropDownExpanded = !dropDownExpanded
                                focusRequester.requestFocus() //to give the focus to the TextField
                            },
                            interactionSource = interactionSource,
                            indication = null //to avoid the ripple on the Box
                        )
                )
            }
        }

        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){ textFieldSize.width.toDp() })
        ) {
            values.forEach { (value, text) ->
                DropdownMenuItem(
                    text = {
                        Text(text = text)
                    },
                    onClick = {
                        textFieldText = text
                        dropDownExpanded = false
                        onSelected.invoke(value, text)
                    }
                )
            }
        }
    }
}