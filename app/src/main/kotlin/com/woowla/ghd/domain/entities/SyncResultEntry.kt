package com.woowla.ghd.domain.entities

import androidx.room.*
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Entity(
    tableName = "sync_result_entry",
    foreignKeys = [
        ForeignKey(
            entity = SyncResult::class,
            parentColumns = ["id"],
            childColumns = ["sync_result_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RepoToCheck::class,
            parentColumns = ["id"],
            childColumns = ["repo_to_check_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["sync_result_id"]), Index(value = ["repo_to_check_id"])],
)
data class SyncResultEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sync_result_id") val syncResultId: Long,
    @ColumnInfo(name = "repo_to_check_id") val repoToCheckId: Long?,
    @ColumnInfo(name = "is_success") val isSuccess: Boolean,
    @ColumnInfo(name = "start_at") val startAt: Instant,
    @ColumnInfo(name = "end_at") val endAt: Instant,
    @ColumnInfo(name = "origin") val origin: Origin,
    @ColumnInfo(name = "error") val error: String?,
    @ColumnInfo(name = "error_message") val errorMessage: String?,
): Comparable<SyncResultEntry> {
    companion object {
        val defaultComparator = compareBy<SyncResultEntry> { it.isSuccess }.thenByDescending { it.origin }.thenByDescending { it.repoToCheckId }
    }

    enum class Origin { OTHER, PULL, RELEASE, UNKNOWN }

    val duration: Duration by lazy { endAt.minus(startAt) }

    override fun compareTo(other: SyncResultEntry): Int {
        return defaultComparator.compare(this, other)
    }
}
