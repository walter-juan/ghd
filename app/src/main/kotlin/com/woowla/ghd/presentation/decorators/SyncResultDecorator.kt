package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.presentation.app.i18n

class SyncResultDecorator(private val syncResult: SyncResult) {
    val emoji: String = when(syncResult.status) {
        SyncResult.Status.SUCCESS -> "✅"
        SyncResult.Status.WARNING -> "⚠️"
        SyncResult.Status.ERROR -> "❌"
        SyncResult.Status.CRITICAL -> "🚨"
    }

    val title: String = i18n.sync_result_title(syncResult.startAt, emoji)
}