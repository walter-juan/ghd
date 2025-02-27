package com.woowla.ghd.data.remote.mappers

import com.woowla.ghd.data.remote.GetLastReleaseQuery
import com.woowla.ghd.data.remote.SearchRepositoryQuery.OnRepository
import com.woowla.ghd.data.remote.entities.ApiGhdRelease
import com.woowla.ghd.data.remote.fragment.PullRequestFragment
import com.woowla.ghd.domain.entities.Author
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.GhdRelease
import com.woowla.ghd.domain.entities.GitHubMergeableState
import com.woowla.ghd.domain.entities.MergeGitHubStateStatus
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.Repository
import com.woowla.ghd.utils.enumValueOfOrDefault
import kotlinx.datetime.Instant

fun ApiGhdRelease.toGhdRelease(): GhdRelease {
    return GhdRelease(
        tag = this.tag,
    )
}

fun OnRepository.toRepository(): Repository {
    return Repository(
        id = id,
        name = name,
        owner = owner.login,
        url = url.toString(),
        imageUrl = owner.avatarUrl.toString(),
        description = description,
        stargazerCount = stargazerCount,
        licenseInfo = licenseInfo?.name,
        primaryLanguageName = primaryLanguage?.name,
        primaryLanguageColor = primaryLanguage?.color,

    )
}

fun PullRequestFragment.Node.toPullRequest(
    repoToCheck: RepoToCheck,
): PullRequestWithRepoAndReviews {
    val lastCommitCheckRollupStatusString = commits.edges?.firstOrNull()?.node?.commit?.statusCheckRollup?.state?.toString()
    val lastCommitSha1 = commits.edges?.firstOrNull()?.node?.commit?.oid?.toString()

    val pullRequest = PullRequest(
        id = id,
        number = number.toLong(),
        url = url.toString(),
        state = enumValueOfOrDefault(state.toString(), PullRequestState.UNKNOWN),
        title = title,
        createdAt = Instant.parse(createdAt.toString()),
        updatedAt = Instant.parse(updatedAt.toString()),
        mergedAt = mergedAt?.toString()?.let { Instant.parse(it) },
        closedAt = closedAt?.toString()?.let { Instant.parse(it) },
        isDraft = isDraft,
        baseRef = baseRefName,
        headRef = headRefName,
        author = author?.toAuthor(),
        totalCommentsCount = totalCommentsCount?.toLong(),
        mergeableState = enumValueOfOrDefault(mergeable.toString(), GitHubMergeableState.UNKNOWN),
        mergeStateStatus = enumValueOfOrDefault(mergeStateStatus.toString(), MergeGitHubStateStatus.UNKNOWN),
        lastCommitCheckRollupStatus = enumValueOfOrDefault(lastCommitCheckRollupStatusString, CommitCheckRollupStatus.UNKNOWN),
        lastCommitSha1 = lastCommitSha1,
        repoToCheckId = repoToCheck.id,
    )

    return PullRequestWithRepoAndReviews(
        pullRequest = pullRequest,
        reviews = latestReviews?.toReviews(pullRequestId = id) ?: listOf(),
        repoToCheck = repoToCheck,
    )
}

fun GetLastReleaseQuery.LatestRelease.toRelease(repoToCheck: RepoToCheck): ReleaseWithRepo {
    val release = Release(
        id = id,
        name = name,
        tagName = tagName,
        url = url.toString(),
        publishedAt = publishedAt?.toString()?.let { Instant.parse(it) },
        author = author?.toAuthor(),
        repoToCheckId = repoToCheck.id,
    )
    return ReleaseWithRepo(
        release = release,
        repoToCheck = repoToCheck,
    )
}

fun PullRequestFragment.LatestReviews.toReviews(pullRequestId: String): List<Review> {
    return edges?.mapNotNull { edge ->
        edge?.node?.let { node ->
            Review(
                id = node.id,
                state = enumValueOfOrDefault(node.state.toString(), ReviewState.UNKNOWN),
                url = node.url.toString(),
                submittedAt = node.submittedAt?.toString()?.let { Instant.parse(it) },
                author = node.author?.toAuthor(),
                pullRequestId = pullRequestId,
            )
        }
    } ?: listOf()
}

fun PullRequestFragment.Author.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}

fun GetLastReleaseQuery.Author.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}

fun PullRequestFragment.Author1.toAuthor(): Author {
    return Author(
        login = login,
        url = url.toString(),
        avatarUrl = avatarUrl.toString(),
    )
}