package com.woowla.ghd.domain.data

import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewRequest
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncResultEntryWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.entities.SyncSettings

interface LocalDataSource {
    suspend fun getAppSettings(): Result<AppSettings>

    suspend fun updateAppSettings(appSettings: AppSettings): Result<Unit>

    suspend fun getSyncSettings(): Result<SyncSettings>

    suspend fun updateSyncSettings(syncSettings: SyncSettings): Result<Unit>

    suspend fun getLastSyncResult(): Result<SyncResultWithEntriesAndRepos?>

    suspend fun getAllSyncResults(): Result<List<SyncResultWithEntriesAndRepos>>

    suspend fun getSyncResult(id: Long): Result<SyncResultWithEntriesAndRepos>

    suspend fun removeSyncResults(ids: List<Long>): Result<Unit>

    suspend fun upsertSyncResult(syncResult: SyncResult): Result<SyncResult>

    /**
     * Get the sync result entries by the sync result id with the repo to check
     */
    suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntryWithRepo>>

    suspend fun upsertSyncResultEntries(syncResultEntryList: List<SyncResultEntry>): Result<Unit>

    suspend fun getRepoToCheck(id: Long): Result<RepoToCheck>

    suspend fun getAllReposToCheck(): Result<List<RepoToCheck>>

    suspend fun upsertRepoToCheck(repoToCheck: RepoToCheck): Result<Unit>

    suspend fun removeRepoToCheck(id: Long): Result<Unit>

    suspend fun getPullRequest(id: String): Result<PullRequestWithRepoAndReviews>

    suspend fun getAllPullRequests(): Result<List<PullRequestWithRepoAndReviews>>

    suspend fun upsertPullRequests(pullRequests: List<PullRequest>): Result<Unit>

    suspend fun removePullRequests(ids: List<String>): Result<Unit>

    suspend fun getAllReleases(): Result<List<ReleaseWithRepo>>

    suspend fun upsertRelease(release: Release): Result<Unit>

    suspend fun removeReleases(ids: List<String>): Result<Unit>

    suspend fun removeReleaseByRepoToCheck(repoToCheckId: Long): Result<Unit>

    suspend fun removeReviewsByPullRequest(pullRequestIds: List<String>): Result<Unit>

    suspend fun upsertReviews(reviews: List<Review>): Result<Unit>

    suspend fun removeReviewRequestsByPullRequest(pullRequestIds: List<String>): Result<Unit>

    suspend fun upsertReviewRequests(reviewRequests: List<ReviewRequest>): Result<Unit>

    suspend fun getOrCreateSyncSettings(): SyncSettings
}