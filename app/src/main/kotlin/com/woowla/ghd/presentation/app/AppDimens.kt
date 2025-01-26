package com.woowla.ghd.presentation.app

import androidx.compose.ui.unit.dp

/**
 * application dimens
 */
object AppDimens {
    val windowWidth = 900.dp
    val windowHeight = 650.dp

    val cardMaxWidth = 500.dp
    val cardHorizontalSpaceBetween = 4.dp
    val cardVerticalSpaceBetween = 4.dp

    val screenPadding = 10.dp
    val screenMaxWidth = cardMaxWidth * 2 + screenPadding * 2 + cardHorizontalSpaceBetween * 4 // same as 2 cards per screen
}