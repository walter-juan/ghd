package com.woowla.ghd.data.remote

import com.woowla.ghd.domain.entities.ApiResponse
import com.woowla.ghd.domain.entities.GhdRelease
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Repository

interface RemoteDataSource {
    suspend fun getAllStatesPullRequests(repoToCheck: RepoToCheck): Result<ApiResponse<List<PullRequestWithRepoAndReviews>>>
    suspend fun getLastRelease(repoToCheck: RepoToCheck): Result<ApiResponse<ReleaseWithRepo>>
    suspend fun search(text: String? = null, owner: String? = null): Result<ApiResponse<List<Repository>>>
    suspend fun getLastGhdRelease(): Result<ApiResponse<GhdRelease>>
}

