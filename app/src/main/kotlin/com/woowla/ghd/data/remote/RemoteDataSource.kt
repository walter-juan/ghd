package com.woowla.ghd.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.network.http.HttpInfo
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.data.remote.entities.ApiRateLimit
import com.woowla.ghd.data.remote.entities.ApiRelease
import com.woowla.ghd.data.remote.entities.ApiResponse
import com.woowla.ghd.data.remote.fragment.PullRequestFragment
import com.woowla.ghd.data.remote.type.PullRequestState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class RemoteDataSource(
    private val apolloClient: ApolloClient = apolloClientInstance,
    private val ktorClient: HttpClient = ktorClientInstance
) {
    companion object {
        val apolloClientInstance = ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .addHttpInterceptor(AuthorizationInterceptor())
            .build()

        val ktorClientInstance = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    suspend fun getPullRequests(owner: String, repo: String, state: PullRequestState): Result<ApiResponse<List<PullRequestFragment.Node>>> {
        return runCatching {
            val pullRequestsQuery = GetPullRequestsQuery(owner = owner, name = repo, states = listOf(state), last = 25)
            val pullRequestsResponse = apolloClient.query(pullRequestsQuery).execute()

            val data = pullRequestsResponse.dataAssertNoErrors.repository?.pullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()
            val rateLimit = pullRequestsResponse.getHeadersAsMap().getRateLimit()

            ApiResponse(data = data, rateLimit = rateLimit)
        }
    }

    suspend fun getAllStatesPullRequests(owner: String, repo: String): Result<ApiResponse<List<PullRequestFragment.Node>>> {
        return runCatching {
            val pullRequestsQuery = GetAllStatesPullRequestsQuery(owner = owner, name = repo, last = 25)
            val pullRequestsResponse = apolloClient.query(pullRequestsQuery).execute()

            val repository = pullRequestsResponse.dataAssertNoErrors.repository
            val openPullRequests = repository?.openPullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()
            val closedPullRequests = repository?.closedPullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()
            val mergedPullRequests = repository?.mergedPullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()

            val data = openPullRequests + closedPullRequests + mergedPullRequests
            val rateLimit = pullRequestsResponse.getHeadersAsMap().getRateLimit()

            ApiResponse(
                data = data,
                rateLimit = rateLimit
            )
        }
    }

    suspend fun getLastRelease(owner: String, repo: String): Result<ApiResponse<GetLastReleaseQuery.LatestRelease>> {
        return runCatching {
            val getLastReleaseQuery = GetLastReleaseQuery(owner = owner, name = repo)
            val getLastReleaseResponse = apolloClient.query(getLastReleaseQuery).execute()

            val data = getLastReleaseResponse.dataAssertNoErrors.repository?.latestRelease ?: throw NotFoundException("Last release not found for $owner/$repo")
            val rateLimit = getLastReleaseResponse.getHeadersAsMap().getRateLimit()

            ApiResponse(data = data, rateLimit = rateLimit)
        }
    }

    suspend fun getLastGhdRelease(): Result<ApiResponse<ApiRelease>> {
        return runCatching {
            val httpResponse = ktorClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.github.com"
                    url {
                        appendPathSegments(
                            "repos",
                            BuildConfig.GH_GHD_OWNER,
                            BuildConfig.GH_GHD_REPO,
                            "releases",
                            "latest"
                        )
                    }
                }
            }

            val rateLimit = httpResponse.getHeadersAsMap().getRateLimit()
            val data: ApiRelease = httpResponse.body()

            ApiResponse(data = data, rateLimit = rateLimit)
        }
    }

    private fun HttpResponse.getHeadersAsMap(): Map<String, String> {
        return headers.entries().associate { it.key to it.value.joinToString(separator = ", ") }
    }

    private fun <D: Operation.Data> ApolloResponse<D>.getHeadersAsMap(): Map<String, String> {
        return executionContext[HttpInfo]?.headers?.associate { (key, value) -> key to value } ?: mapOf()
    }

    private fun Map<String, String>.getRateLimit(): ApiRateLimit {
        val limit = this["x-ratelimit-limit"]?.toLongOrNull()
        val remaining = this["x-ratelimit-remaining"]?.toLongOrNull()
        val used = this["x-ratelimit-used"]?.toLongOrNull()
        val reset = this["x-ratelimit-reset"]?.toLongOrNull()
        val resource = this["x-ratelimit-resource"]
        return ApiRateLimit(
            limit = limit,
            remaining = remaining,
            used = used,
            reset = reset?.let { Instant.fromEpochSeconds(it) },
            resource = resource,
        )
    }
}