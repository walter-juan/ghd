package com.woowla.ghd.data.remote

import com.apollographql.apollo3.ApolloClient
import com.woowla.ghd.data.remote.type.PullRequestState

class RemoteDataSource(
    private val apolloClient: ApolloClient = apolloClientInstance
) {
    companion object {
        val apolloClientInstance = ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .addHttpInterceptor(AuthorizationInterceptor())
            .build()
    }

    suspend fun getPullRequests(owner: String, repo: String, state: PullRequestState): Result<List<GetPullRequestsQuery.Node>> {
        return runCatching {
            val pullRequestsQuery = GetPullRequestsQuery(owner = owner, name = repo, states = listOf(state), last = 50)
            val pullRequestsResponse = apolloClient.query(pullRequestsQuery).execute()
            pullRequestsResponse.dataAssertNoErrors.repository?.pullRequests?.edges?.mapNotNull { it?.node } ?: listOf()
        }
    }

    suspend fun getLastRelease(owner: String, repo: String): Result<GetLastReleaseQuery.LatestRelease> {
        return runCatching {
            val getLastReleaseQuery = GetLastReleaseQuery(owner = owner, name = repo)
            val getLastReleaseResponse = apolloClient.query(getLastReleaseQuery).execute()
            getLastReleaseResponse.dataAssertNoErrors.repository?.latestRelease ?: throw NotFoundException("Last release not found for $owner/$repo")
        }
    }
}