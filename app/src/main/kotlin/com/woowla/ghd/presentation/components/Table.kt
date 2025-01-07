package com.woowla.ghd.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun RowScope.TableCell(
    weight: Float,
    composable: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
        .weight(weight)
        .padding(8.dp)
    ) {
        composable()
    }
}
