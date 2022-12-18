package com.woowla.ghd.data.remote

import com.apollographql.apollo3.ApolloClient
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.data.remote.entities.ApiRelease
import com.woowla.ghd.data.remote.type.PullRequestState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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

    suspend fun getLastGhdRelease(): Result<ApiRelease> {
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

            httpResponse.body()
        }
    }
}