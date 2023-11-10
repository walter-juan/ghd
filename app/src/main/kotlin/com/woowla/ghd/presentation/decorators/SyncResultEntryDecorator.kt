package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.presentation.app.i18n

class SyncResultEntryDecorator(private val syncResultEntry: SyncResultEntry) {
    val emoji: String = when(syncResultEntry) {
        is SyncResultEntry.Error -> "❌"
        is SyncResultEntry.Success -> "✅"
    }
}