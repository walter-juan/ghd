package com.woowla.ghd.data.local.room

import com.woowla.ghd.data.local.room.entities.DbPullRequest
import com.woowla.ghd.data.local.room.entities.DbRelease
import com.woowla.ghd.data.local.room.entities.DbRepoToCheck
import com.woowla.ghd.data.local.room.entities.DbReview
import com.woowla.ghd.data.local.room.entities.DbReviewRequest
import com.woowla.ghd.data.local.room.entities.DbSyncResult
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntry
import com.woowla.ghd.data.local.room.entities.DbSyncSettings
import com.woowla.ghd.data.local.room.entities.fromPullRequest
import com.woowla.ghd.data.local.room.entities.fromRelease
import com.woowla.ghd.data.local.room.entities.fromRepoToCheck
import com.woowla.ghd.data.local.room.entities.fromReview
import com.woowla.ghd.data.local.room.entities.fromReviewRequest
import com.woowla.ghd.data.local.room.entities.fromSyncResult
import com.woowla.ghd.data.local.room.entities.fromSyncResultEntry
import com.woowla.ghd.data.local.room.entities.fromSyncSettings
import com.woowla.ghd.data.local.room.entities.toRepoToCheck
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewRequest
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncSettings

fun SyncSettings.toDbSyncSettings() = DbSyncSettings.fromSyncSettings(this)
fun Release.toDbRelease() = DbRelease.fromRelease(this)
fun RepoToCheck.toDbRepoToCheck() = DbRepoToCheck.fromRepoToCheck(this)
fun SyncResult.toDbSyncResult() = DbSyncResult.fromSyncResult(this)

fun List<DbRepoToCheck>.toRepoToCheck() = map { it.toRepoToCheck() }

fun List<PullRequest>.toDbPullRequest() = map { DbPullRequest.fromPullRequest(it) }
fun List<Review>.toDbReview() = map { DbReview.fromReview(it) }
fun List<ReviewRequest>.toDbReviewRequest() = map { DbReviewRequest.fromReviewRequest(it) }
fun List<SyncResultEntry>.toDbSyncResultEntry() = map { DbSyncResultEntry.fromSyncResultEntry(it) }
