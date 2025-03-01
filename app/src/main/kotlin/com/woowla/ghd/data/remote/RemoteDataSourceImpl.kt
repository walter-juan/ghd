package com.woowla.ghd.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.network.http.HttpInfo
import com.woowla.ghd.core.AppLogger
import com.woowla.ghd.data.remote.entities.ApiGhdRelease
import com.woowla.ghd.data.remote.mappers.toGhdRelease
import com.woowla.ghd.domain.entities.ApiResponse
import com.woowla.ghd.data.remote.mappers.toPullRequest
import com.woowla.ghd.data.remote.mappers.toRelease
import com.woowla.ghd.data.remote.mappers.toRepository
import com.woowla.ghd.domain.data.RemoteDataSource
import com.woowla.ghd.domain.entities.GhdRelease
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.RateLimit
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Repository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class RemoteDataSourceImpl(
    private val ghOwner: String,
    private val ghRepo: String,
    private val apolloClient: ApolloClient,
    private val ktorClient: HttpClient,
    private val appLogger: AppLogger,
) : RemoteDataSource {
    companion object {
        fun apolloClientInstance(authorizationInterceptor: AuthorizationInterceptor) = ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .addHttpInterceptor(authorizationInterceptor)
            .build()

        fun ktorClientInstance() = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    override suspend fun getAllStatesPullRequests(repoToCheck: RepoToCheck): Result<ApiResponse<List<PullRequestWithRepoAndReviews>>> {
        return runCatching {
            val owner = repoToCheck.owner
            val repo = repoToCheck.name
            val pullRequestsQuery = GetAllStatesPullRequestsQuery(owner = owner, name = repo, last = 25)
            val pullRequestsResponse = apolloClient.query(pullRequestsQuery).execute()

            val repository = pullRequestsResponse.dataAssertNoErrors.repository
            val openPullRequests = repository?.openPullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()
            val closedPullRequests = repository?.closedPullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()
            val mergedPullRequests = repository?.mergedPullRequests?.pullRequestFragment?.edges?.mapNotNull { it?.node } ?: listOf()

            val data = openPullRequests + closedPullRequests + mergedPullRequests
            val rateLimit = pullRequestsResponse.getHeadersAsMap().getRateLimit()

            ApiResponse(
                data = data.map { it.toPullRequest(repoToCheck) },
                rateLimit = rateLimit
            )
        }
    }

    override suspend fun search(
        text: String?,
        owner: String?,
    ): Result<ApiResponse<List<Repository>>> {
        return runCatching {
            val searchQueryItems = buildList {
                if (!owner.isNullOrBlank()) {
                    add("owner:$owner")
                }
            }

            val searchQuery = SearchRepositoryQuery(
                query = "$text " + searchQueryItems.joinToString(separator = "+"),
                first = 25
            )
            val searchResponse = apolloClient.query(searchQuery).execute()
            val data = searchResponse
                .dataAssertNoErrors
                .search
                .edges
                ?.mapNotNull { it?.node?.onRepository } ?: listOf()
            val rateLimit = searchResponse.getHeadersAsMap().getRateLimit()

            ApiResponse(
                data = data.map { it.toRepository() },
                rateLimit = rateLimit
            )
        }
    }

    override suspend fun getLastRelease(repoToCheck: RepoToCheck): Result<ApiResponse<ReleaseWithRepo>> {
        return runCatching {
            val owner = repoToCheck.owner
            val repo = repoToCheck.name
            val getLastReleaseQuery = GetLastReleaseQuery(owner = owner, name = repo)
            val getLastReleaseResponse = apolloClient.query(getLastReleaseQuery).execute()

            val data = getLastReleaseResponse
                .dataAssertNoErrors
                .repository
                ?.latestRelease
                ?.toRelease(repoToCheck)
                ?: throw NotFoundException("Last release not found for $owner/$repo")
            val rateLimit = getLastReleaseResponse.getHeadersAsMap().getRateLimit()

            ApiResponse(data = data, rateLimit = rateLimit)
        }
    }

    override suspend fun getLastGhdRelease(): Result<ApiResponse<GhdRelease>> {
        return runCatching {
            val httpResponse = ktorClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.github.com"
                    url {
                        appendPathSegments(
                            "repos",
                            ghOwner,
                            ghRepo,
                            "releases",
                            "latest"
                        )
                    }
                }
            }

            val rateLimit = httpResponse.getHeadersAsMap().getRateLimit()
            val data: ApiGhdRelease = httpResponse.body()

            ApiResponse(data = data.toGhdRelease(), rateLimit = rateLimit)
        }
    }


    private fun HttpResponse.getHeadersAsMap(): Map<String, String> {
        return headers.entries().associate { it.key to it.value.joinToString(separator = ", ") }
    }

    private fun <D : Operation.Data> ApolloResponse<D>.getHeadersAsMap(): Map<String, String> {
        return executionContext[HttpInfo]?.headers?.associate { (key, value) -> key to value } ?: mapOf()
    }

    private fun Map<String, String>.getRateLimit(): RateLimit {
        val limit = this["x-ratelimit-limit"]?.toLongOrNull()
        val remaining = this["x-ratelimit-remaining"]?.toLongOrNull()
        val used = this["x-ratelimit-used"]?.toLongOrNull()
        val reset = this["x-ratelimit-reset"]?.toLongOrNull()
        val resource = this["x-ratelimit-resource"]
        return RateLimit(
            limit = limit,
            remaining = remaining,
            used = used,
            reset = reset?.let { Instant.fromEpochSeconds(it) },
            resource = resource,
        )
    }
}