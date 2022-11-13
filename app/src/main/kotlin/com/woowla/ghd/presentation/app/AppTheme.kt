package com.woowla.ghd.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val typography = MaterialTheme.typography.copy(
        h1 = MaterialTheme.typography.h1.copy(
            fontSize = 34.sp
        ),
        h2 = MaterialTheme.typography.h2.copy(
            fontSize = 30.sp
        ),
        h3 = MaterialTheme.typography.h3.copy(
            fontSize = 27.sp
        ),
        h4 = MaterialTheme.typography.h4.copy(
            fontSize = 24.sp
        ),
        h5 = MaterialTheme.typography.h5.copy(
            fontSize = 22.sp
        ),
        h6 = MaterialTheme.typography.h6.copy(
            fontSize = 20.sp
        ),
        subtitle1 = MaterialTheme.typography.subtitle1.copy(
            fontSize = 18.sp
        ),
        subtitle2 = MaterialTheme.typography.subtitle2.copy(
            fontSize = 16.sp
        ),
        body1 = MaterialTheme.typography.body1.copy(
            fontSize = 14.sp
        ),
        body2 = MaterialTheme.typography.body2.copy(
            fontSize = 12.sp
        ),
        button = MaterialTheme.typography.button.copy(
            fontSize = 12.sp
        ),
        caption = MaterialTheme.typography.caption.copy(
            fontSize = 11.sp
        ),
        overline = MaterialTheme.typography.overline.copy(
            fontSize = 10.sp
        )
    )

    MaterialTheme(
        colors = AppColors.materialColors(lightTheme = !darkTheme),
        typography = typography,
        content = content,
    )
}