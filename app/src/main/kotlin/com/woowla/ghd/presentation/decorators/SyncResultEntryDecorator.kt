package com.woowla.ghd.presentation.decorators

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Check
import com.woowla.compose.icon.collections.tabler.tabler.outline.QuestionMark
import com.woowla.compose.icon.collections.tabler.tabler.outline.X
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.presentation.app.AppColors.success
import com.woowla.ghd.presentation.app.AppScreen

class SyncResultEntryDecorator(private val syncResultEntry: SyncResultEntry) {
    val statusIcon: ImageVector = if (syncResultEntry.isSuccess) {
        Tabler.Outline.Check
    } else {
        Tabler.Outline.X
    }

    @Composable
    fun statusIconTint(): Color = if (syncResultEntry.isSuccess) {
        MaterialTheme.colorScheme.success
    } else {
        MaterialTheme.colorScheme.error
    }

    val originIcon: ImageVector = when(syncResultEntry.origin) {
        SyncResultEntry.Origin.PULL -> AppScreen.PullRequestList.icon
        SyncResultEntry.Origin.RELEASE -> AppScreen.ReleaseList.icon
        SyncResultEntry.Origin.UNKNOWN -> Tabler.Outline.QuestionMark
        SyncResultEntry.Origin.OTHER -> Tabler.Outline.QuestionMark
    }
}