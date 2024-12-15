package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultRateLimit
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.extensions.toRelativeString
import com.woowla.ghd.presentation.app.i18n

class SyncResultDecorator(private val syncResultWithEntities: SyncResultWithEntriesAndRepos) {
    val emoji: String = when(syncResultWithEntities.status) {
        SyncResult.Status.SUCCESS -> "✅"
        SyncResult.Status.WARNING -> "⚠️"
        SyncResult.Status.ERROR -> "❌"
        SyncResult.Status.CRITICAL -> "🚨"
    }

    val title: String by lazy {
        val rateLimit: SyncResultRateLimit? = syncResultWithEntities.syncResultEntries.maxByOrNull { it.syncResultEntry.endAt }?.syncResultEntry?.rateLimit
        i18n.sync_result_title(syncResultWithEntities.syncResult.startAt, emoji, rateLimit?.percentageUsed, rateLimit?.reset)
    }
}