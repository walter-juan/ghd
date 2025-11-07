package com.woowla.ghd.data.local.room.converters

import androidx.room.TypeConverter
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeGitHubStateStatus
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.core.utils.enumValueOfOrDefault
import kotlin.time.Instant

class Converters {
    @TypeConverter
    fun stringToInstant(value: String?): Instant? = value?.let { Instant.parse(it) }

    @TypeConverter
    fun instantToString(value: Instant?): String? = value?.toString()

    @TypeConverter
    fun stringToOrigin(value: String?): SyncResultEntry.Origin = enumValueOfOrDefault(value, SyncResultEntry.Origin.UNKNOWN)

    @TypeConverter
    fun originToString(value: SyncResultEntry.Origin?): String? = value?.toString()

    @TypeConverter
    fun stringToMergeGitHubStateStatus(value: String?): MergeGitHubStateStatus = enumValueOfOrDefault(value, MergeGitHubStateStatus.UNKNOWN)

    @TypeConverter
    fun mergeGitHubStateStatusToString(value: MergeGitHubStateStatus?): String? = value?.toString()

    @TypeConverter
    fun stringToCommitCheckRollupStatus(value: String?): CommitCheckRollupStatus = enumValueOfOrDefault(value, CommitCheckRollupStatus.UNKNOWN)

    @TypeConverter
    fun commitCheckRollupStatusToString(value: CommitCheckRollupStatus?): String? = value?.toString()

    @TypeConverter
    fun stringToPullRequestState(value: String?): PullRequestState = enumValueOfOrDefault(value, PullRequestState.UNKNOWN)

    @TypeConverter
    fun pullRequestStateToString(value: PullRequestState?): String? = value?.toString()

    @TypeConverter
    fun stringToReviewState(value: String?): ReviewState = enumValueOfOrDefault(value, ReviewState.UNKNOWN)

    @TypeConverter
    fun reviewStateToString(value: ReviewState?): String? = value?.toString()
}