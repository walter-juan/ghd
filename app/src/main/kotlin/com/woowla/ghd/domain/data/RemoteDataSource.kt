package com.woowla.ghd.domain.data

import com.woowla.ghd.domain.entities.ApiResponse
import com.woowla.ghd.domain.entities.GhdRelease
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck

interface RemoteDataSource {
    suspend fun getAllStatesPullRequests(repoToCheck: RepoToCheck): Result<ApiResponse<List<PullRequestWithRepoAndReviews>>>
    suspend fun getLastRelease(repoToCheck: RepoToCheck): Result<ApiResponse<ReleaseWithRepo>>
    suspend fun getLastGhdRelease(): Result<ApiResponse<GhdRelease>>
}