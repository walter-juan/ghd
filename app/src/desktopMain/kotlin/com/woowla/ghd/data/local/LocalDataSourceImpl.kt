package com.woowla.ghd.data.local

import com.woowla.ghd.data.local.prop.AppProperties
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.data.local.room.entities.DbPullRequestWithRepoAndReviews
import com.woowla.ghd.data.local.room.entities.DbReleaseWithRepo
import com.woowla.ghd.data.local.room.entities.DbSyncResultEntryWithRepo
import com.woowla.ghd.data.local.room.entities.DbSyncResultWithEntriesAndRepos
import com.woowla.ghd.data.local.room.entities.toPullRequestWithRepoAndReviews
import com.woowla.ghd.data.local.room.entities.toReleaseWithRepo
import com.woowla.ghd.data.local.room.entities.toRepoToCheck
import com.woowla.ghd.data.local.room.entities.toSyncResult
import com.woowla.ghd.data.local.room.entities.toSyncResultEntryWithRepo
import com.woowla.ghd.data.local.room.entities.toSyncResultWithEntriesAndRepos
import com.woowla.ghd.data.local.room.entities.toSyncSettings
import com.woowla.ghd.data.local.room.toDbSyncResult
import com.woowla.ghd.data.local.room.toDbRepoToCheck
import com.woowla.ghd.data.local.room.toDbRelease
import com.woowla.ghd.data.local.room.toDbSyncSettings
import com.woowla.ghd.data.local.room.toDbSyncResultEntry
import com.woowla.ghd.data.local.room.toDbReview
import com.woowla.ghd.data.local.room.toDbPullRequest
import com.woowla.ghd.data.local.room.toRepoToCheck
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.NotificationsSettings
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.SyncResult
import com.woowla.ghd.domain.entities.SyncResultEntry
import com.woowla.ghd.domain.entities.SyncResultEntryWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.domain.entities.SyncSettings
import com.woowla.ghd.core.utils.enumValueOfOrDefault
import com.woowla.ghd.core.utils.enumValueOfOrNull
import com.woowla.ghd.data.local.room.toDbReviewRequest
import com.woowla.ghd.domain.data.LocalDataSource
import com.woowla.ghd.domain.entities.RepoToCheckFilters
import com.woowla.ghd.domain.entities.ReviewRequest

class LocalDataSourceImpl(
    private val appProperties: AppProperties,
    private val appDatabase: AppDatabase,
) : LocalDataSource {
    override suspend fun getAppSettings(): Result<AppSettings> {
        return runCatching {
            appProperties.load()
            val defaultEnabledOption = NotificationsSettings.defaultEnabledOption
            val filtersPullRequestState = appProperties.filtersPullRequestState
                ?.split(",")
                ?.filterNot { it.isBlank() }
                ?.map { enumValueOfOrDefault(it, PullRequestStateExtended.UNKNOWN) }
                ?.toSet()
                ?: emptySet()

            val filtersReleaseGroupName = appProperties.filtersReleaseGroupName
                ?.split(",")
                ?.filterNot { it.isBlank() }
                ?.toSet()
                ?: emptySet()

            val filtersRepoToCheckGroupName = appProperties.filtersRepoToCheckGroupName
                ?.split(",")
                ?.filterNot { it.isBlank() }
                ?.toSet()
                ?: emptySet()

            AppSettings(
                darkTheme = appProperties.darkTheme,
                filtersPullRequestState = filtersPullRequestState,
                filtersReleaseGroupName = filtersReleaseGroupName,
                filtersRepoToCheck = RepoToCheckFilters(
                    groupNames = filtersRepoToCheckGroupName,
                    pullRequestSyncEnabled = appProperties.filtersRepoToCheckPullRequestSyncEnabled,
                    pullRequestNotificationsEnabled = appProperties.filtersRepoToCheckPullRequestNotificationsEnabled,
                    pullRequestBranchFilterActive = appProperties.filtersRepoToCheckPullRequestBranchFilterActive,
                    releasesSyncEnabled = appProperties.filtersRepoToCheckReleasesSyncEnabled,
                    releasesNotificationsEnabled = appProperties.filtersRepoToCheckReleasesNotificationsEnabled,
                ),
                notificationsSettings = NotificationsSettings(
                    filterUsername = appProperties.notificationsFilterUsername,

                    stateEnabledOption = enumValueOfOrNull<NotificationsSettings.EnabledOption>(appProperties.notificationsStateEnabledOption) ?: defaultEnabledOption,
                    stateOpenFromOthersPullRequestsEnabled = appProperties.notificationsStateOpenFromOthersPullRequestsEnabled,
                    stateClosedFromOthersPullRequestsEnabled = appProperties.notificationsStateClosedFromOthersPullRequestsEnabled,
                    stateMergedFromOthersPullRequestsEnabled = appProperties.notificationsStateMergedFromOthersPullRequestsEnabled,
                    stateDraftFromOthersPullRequestsEnabled = appProperties.notificationsStateDraftFromOthersPullRequestsEnabled,

                    activityEnabledOption = enumValueOfOrNull<NotificationsSettings.EnabledOption>(appProperties.notificationsActivityEnabledOption) ?: defaultEnabledOption,
                    activityReviewsFromYourPullRequestsEnabled = appProperties.notificationsActivityReviewsFromYourPullRequestsEnabled,
                    activityReviewsFromYouDismissedEnabled = appProperties.notificationsActivityReviewsFromYouDismissedEnabled,
                    activityChecksFromYourPullRequestsEnabled = appProperties.notificationsActivityChecksFromYourPullRequestsEnabled,
                    activityMergeableFromYourPullRequestsEnabled = appProperties.notificationsActivityMergeableFromYourPullRequestsEnabled,

                    newReleaseEnabled = appProperties.notificationsNewReleaseEnabled,
                )
            )
        }
    }
    override suspend fun updateAppSettings(appSettings: AppSettings): Result<Unit> {
        return runCatching {
            appProperties.load()
            appProperties.darkTheme = appSettings.darkTheme

            appProperties.filtersPullRequestState = appSettings.filtersPullRequestState.joinToString(separator = ",")
            appProperties.filtersReleaseGroupName = appSettings.filtersReleaseGroupName.joinToString(separator = ",")

            appProperties.filtersRepoToCheckGroupName = appSettings.filtersRepoToCheck.groupNames.joinToString(separator = ",")
            appProperties.filtersRepoToCheckPullRequestSyncEnabled = appSettings.filtersRepoToCheck.pullRequestSyncEnabled
            appProperties.filtersRepoToCheckPullRequestNotificationsEnabled = appSettings.filtersRepoToCheck.pullRequestNotificationsEnabled
            appProperties.filtersRepoToCheckPullRequestBranchFilterActive = appSettings.filtersRepoToCheck.pullRequestBranchFilterActive
            appProperties.filtersRepoToCheckReleasesSyncEnabled = appSettings.filtersRepoToCheck.releasesSyncEnabled
            appProperties.filtersRepoToCheckReleasesNotificationsEnabled = appSettings.filtersRepoToCheck.releasesNotificationsEnabled

            appProperties.notificationsFilterUsername = appSettings.notificationsSettings.filterUsername

            appProperties.notificationsStateEnabledOption = appSettings.notificationsSettings.stateEnabledOption.name
            appProperties.notificationsStateOpenFromOthersPullRequestsEnabled = appSettings.notificationsSettings.stateOpenFromOthersPullRequestsEnabled
            appProperties.notificationsStateClosedFromOthersPullRequestsEnabled = appSettings.notificationsSettings.stateClosedFromOthersPullRequestsEnabled
            appProperties.notificationsStateMergedFromOthersPullRequestsEnabled = appSettings.notificationsSettings.stateMergedFromOthersPullRequestsEnabled
            appProperties.notificationsStateDraftFromOthersPullRequestsEnabled = appSettings.notificationsSettings.stateDraftFromOthersPullRequestsEnabled

            appProperties.notificationsActivityEnabledOption = appSettings.notificationsSettings.activityEnabledOption.name
            appProperties.notificationsActivityReviewsFromYourPullRequestsEnabled = appSettings.notificationsSettings.activityReviewsFromYourPullRequestsEnabled
            appProperties.notificationsActivityReviewsFromYouDismissedEnabled = appSettings.notificationsSettings.activityReviewsFromYouDismissedEnabled
            appProperties.notificationsActivityChecksFromYourPullRequestsEnabled = appSettings.notificationsSettings.activityChecksFromYourPullRequestsEnabled
            appProperties.notificationsActivityMergeableFromYourPullRequestsEnabled = appSettings.notificationsSettings.activityMergeableFromYourPullRequestsEnabled

            appProperties.notificationsNewReleaseEnabled = appSettings.notificationsSettings.newReleaseEnabled

            appProperties.store()
        }
    }

    override suspend fun getSyncSettings(): Result<SyncSettings> {
        return runCatching {
            getOrCreateSyncSettings()
        }
    }
    override suspend fun updateSyncSettings(syncSettings: SyncSettings): Result<Unit> {
        return runCatching {
            appDatabase.syncSettingsDao().insert(syncSettings.toDbSyncSettings())
        }
    }

    override suspend fun getLastSyncResult(): Result<SyncResultWithEntriesAndRepos?> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val syncResult = appDatabase.syncResultDao().getLast()
            val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = syncResult.id)
            val syncResultEntryWithRepoList = syncResultEntryList.map { syncResultEntry ->
                DbSyncResultEntryWithRepo(
                    syncResultEntry = syncResultEntry,
                    repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                )
            }
            DbSyncResultWithEntriesAndRepos(
                syncResult = syncResult,
                syncResultEntries = syncResultEntryWithRepoList,
            ).toSyncResultWithEntriesAndRepos()
        }
    }
    override suspend fun getAllSyncResults(): Result<List<SyncResultWithEntriesAndRepos>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.syncResultDao()
                .getAll()
                .map { syncResult ->
                    val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = syncResult.id)
                    val syncResultEntryWithRepoList = syncResultEntryList.map { syncResultEntry ->
                        DbSyncResultEntryWithRepo(
                            syncResultEntry = syncResultEntry,
                            repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                        )
                    }
                    DbSyncResultWithEntriesAndRepos(
                        syncResult = syncResult,
                        syncResultEntries = syncResultEntryWithRepoList,
                    ).toSyncResultWithEntriesAndRepos()
                }
        }
    }
    override suspend fun getSyncResult(id: Long): Result<SyncResultWithEntriesAndRepos> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val syncResult = appDatabase.syncResultDao().get(id)
            val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = id)
            val syncResultEntryWithRepoList = syncResultEntryList.map { syncResultEntry ->
                DbSyncResultEntryWithRepo(
                    syncResultEntry = syncResultEntry,
                    repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                )
            }
            DbSyncResultWithEntriesAndRepos(
                syncResult = syncResult,
                syncResultEntries = syncResultEntryWithRepoList,
            ).toSyncResultWithEntriesAndRepos()
        }
    }
    override suspend fun removeSyncResults(ids: List<Long>): Result<Unit> {
        return runCatching {
            appDatabase.syncResultDao().delete(ids)
        }
    }
    override suspend fun upsertSyncResult(syncResult: SyncResult): Result<SyncResult> {
        return runCatching {
            val syncResultDao = appDatabase.syncResultDao()
            val rowId = syncResultDao.insert(syncResult.toDbSyncResult())
            syncResultDao.get(rowId).toSyncResult()
        }
    }

    /**
     * Get the sync result entries by the sync result id with the repo to check
     */
    override suspend fun getSyncResultEntries(syncResultId: Long): Result<List<SyncResultEntryWithRepo>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val syncResultEntryList = appDatabase.syncResultEntryDao().getBySyncResult(syncResultId = syncResultId)

            syncResultEntryList.map { syncResultEntry ->
                DbSyncResultEntryWithRepo(
                    syncResultEntry = syncResultEntry,
                    repoToCheck = repoToCheckList.firstOrNull { it.id == syncResultEntry.repoToCheckId }
                ).toSyncResultEntryWithRepo()
            }
        }
    }
    override suspend fun upsertSyncResultEntries(syncResultEntryList: List<SyncResultEntry>): Result<Unit> {
        return runCatching {
            appDatabase.syncResultEntryDao().insert(syncResultEntryList.toDbSyncResultEntry())
        }
    }

    override suspend fun getRepoToCheck(id: Long): Result<RepoToCheck> {
        return runCatching {
            appDatabase.repoToCheckDao().get(id).toRepoToCheck()
        }
    }
    override suspend fun getAllReposToCheck(): Result<List<RepoToCheck>> {
        return runCatching {
            appDatabase.repoToCheckDao().getAll().toRepoToCheck()
        }
    }
    override suspend fun upsertRepoToCheck(repoToCheck: RepoToCheck): Result<Unit> {
        return runCatching {
            appDatabase.repoToCheckDao().insert(repoToCheck.toDbRepoToCheck())
        }
    }
    override suspend fun removeRepoToCheck(id: Long): Result<Unit> {
        return runCatching {
            appDatabase.repoToCheckDao().delete(id)
        }
    }

    override suspend fun getPullRequest(id: String): Result<PullRequestWithRepoAndReviews> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            val reviewList = appDatabase.reviewDao().getByPullRequest(pullRequestId = id)
            val reviewRequestList = appDatabase.reviewRequestDao().getByPullRequest(pullRequestId = id)
            val pullRequest = appDatabase.pullRequestDao().get(id)
            DbPullRequestWithRepoAndReviews(
                repoToCheck = repoToCheckList.first { it.id == pullRequest.repoToCheckId },
                pullRequest = pullRequest,
                reviews = reviewList,
                reviewRequests = reviewRequestList,
            ).toPullRequestWithRepoAndReviews()
        }
    }
    override suspend fun getAllPullRequests(): Result<List<PullRequestWithRepoAndReviews>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.pullRequestDao()
                .getAll()
                .map { pullRequest ->
                    val reviewList = appDatabase.reviewDao().getByPullRequest(pullRequestId = pullRequest.id)
                    val reviewRequestList = appDatabase.reviewRequestDao().getByPullRequest(pullRequestId = pullRequest.id)
                    DbPullRequestWithRepoAndReviews(
                        repoToCheck = repoToCheckList.first { it.id == pullRequest.repoToCheckId },
                        pullRequest = pullRequest,
                        reviews = reviewList,
                        reviewRequests = reviewRequestList,
                    ).toPullRequestWithRepoAndReviews()
                }
        }
    }
    override suspend fun upsertPullRequests(pullRequests: List<PullRequest>): Result<Unit> {
        return runCatching {
            appDatabase.pullRequestDao().insert(pullRequests.toDbPullRequest())
        }
    }
    override suspend fun removePullRequests(ids: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.pullRequestDao().delete(ids)
        }
    }

    override suspend fun getAllReleases(): Result<List<ReleaseWithRepo>> {
        return runCatching {
            val repoToCheckList = appDatabase.repoToCheckDao().getAll()
            appDatabase.releaseDao().getAll().map { release ->
                DbReleaseWithRepo(
                    release = release,
                    repoToCheck = repoToCheckList.first { it.id == release.repoToCheckId }
                ).toReleaseWithRepo()
            }
        }
    }
    override suspend fun upsertRelease(release: Release): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().insert(release.toDbRelease())
        }
    }
    override suspend fun removeReleases(ids: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().delete(ids)
        }
    }
    override suspend fun removeReleaseByRepoToCheck(repoToCheckId: Long): Result<Unit> {
        return runCatching {
            appDatabase.releaseDao().deleteByRepoToCheck(repoToCheckId = repoToCheckId)
        }
    }

    override suspend fun removeReviewsByPullRequest(pullRequestIds: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.reviewDao().deleteByPullRequest(pullRequestIds)
        }
    }
    override suspend fun upsertReviews(reviews: List<Review>): Result<Unit> {
        if (reviews.isEmpty()) return Result.success(Unit)

        return runCatching {
            appDatabase.reviewDao().insert(reviews.toDbReview())
        }
    }

    override suspend fun removeReviewRequestsByPullRequest(pullRequestIds: List<String>): Result<Unit> {
        return runCatching {
            appDatabase.reviewRequestDao().deleteByPullRequest(pullRequestIds)
        }
    }

    override suspend fun upsertReviewRequests(reviewRequests: List<ReviewRequest>): Result<Unit> {
        if (reviewRequests.isEmpty()) return Result.success(Unit)

        return runCatching {
            appDatabase.reviewRequestDao().insert(reviewRequests.toDbReviewRequest())
        }
    }

    override suspend fun getOrCreateSyncSettings(): SyncSettings {
        val dbSyncSettings = appDatabase.syncSettingsDao().get()
        val defaultDbSyncSettings = SyncSettings(
            githubPatToken = "",
            checkTimeout = null,
            pullRequestCleanUpTimeout = null,
        )
        return if (dbSyncSettings == null) {
            appDatabase.syncSettingsDao().insert(defaultDbSyncSettings.toDbSyncSettings())
            defaultDbSyncSettings
        } else {
            dbSyncSettings.toSyncSettings()
        }
    }
}
