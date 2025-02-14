package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.domain.synchronization.SynchronizableService

interface PullRequestService : SynchronizableService {
    suspend fun getAll(): Result<List<PullRequestWithRepoAndReviews>>

    suspend fun cleanUp(syncSettings: SyncSettings)

    suspend fun sendNotifications(
        appSettings: AppSettings,
        oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>,
        newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>
    ): Result<Unit>

    suspend fun sendStateNotifications(
        appSettings: AppSettings,
        oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>,
        newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>
    ): Result<Unit>

    suspend fun sendActivityNotifications(
        appSettings: AppSettings,
        oldPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>,
        newPullRequestsWithReviews: List<PullRequestWithRepoAndReviews>
    ): Result<Unit>
}