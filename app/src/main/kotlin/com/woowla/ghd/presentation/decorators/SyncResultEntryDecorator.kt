package com.woowla.ghd.presentation.decorators

import com.woowla.ghd.domain.entities.SyncResultEntry

class SyncResultEntryDecorator(private val syncResultEntry: SyncResultEntry) {
    val emoji: String = if (syncResultEntry.isSuccess) {
        "✅"
    } else {
        "❌"
    }
}