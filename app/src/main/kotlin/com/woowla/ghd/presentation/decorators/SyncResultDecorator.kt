package com.woowla.ghd.presentation.decorators

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.AlertCircle
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.Flame
import com.woowla.compose.icon.collections.tabler.tabler.outline.Skull
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.RateLimit
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.app.AppColors.success
import com.woowla.ghd.presentation.app.AppColors.warning

class SyncResultDecorator(private val syncResultWithEntities: SyncResultWithEntriesAndRepos) {
    val icon: ImageVector = when (syncResultWithEntities.status) {
        SyncResult.Status.SUCCESS -> Tabler.Outline.CircleCheck
        SyncResult.Status.WARNING -> Tabler.Outline.AlertCircle
        SyncResult.Status.ERROR -> Tabler.Outline.Flame
        SyncResult.Status.CRITICAL -> Tabler.Outline.Skull
    }

    @Composable
    fun iconTint(): Color = when (syncResultWithEntities.status) {
        SyncResult.Status.SUCCESS -> MaterialTheme.colorScheme.success
        SyncResult.Status.WARNING -> MaterialTheme.colorScheme.warning
        SyncResult.Status.ERROR -> MaterialTheme.colorScheme.error
        SyncResult.Status.CRITICAL -> MaterialTheme.colorScheme.error
    }

    @Composable
    fun Title() {
        Row {
            Icon(
                imageVector = icon,
                tint = iconTint(),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = i18nUi.top_bar_subtitle_synchronization_result_entries(syncResultWithEntities.errorPercentage, syncResultWithEntities.entriesSize),
            )
        }
    }

    @Composable
    fun TitleWithDate(onClick: (() -> Unit)?) {
        val rateLimit: RateLimit? = syncResultWithEntities.syncResultEntries.maxByOrNull { it.syncResultEntry.endAt }?.syncResultEntry?.rateLimit
        Row(
            modifier = Modifier.clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
        ) {
            Icon(
                imageVector = icon,
                tint = iconTint(),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = i18nUi.sync_result_title(syncResultWithEntities.syncResult.startAt, rateLimit?.percentageUsed, rateLimit?.reset),
            )
        }
    }
}