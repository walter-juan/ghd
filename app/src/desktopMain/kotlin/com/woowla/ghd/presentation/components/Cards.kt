package com.woowla.ghd.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Book2
import com.woowla.compose.icon.collections.tabler.tabler.outline.Boom
import com.woowla.compose.icon.collections.tabler.tabler.outline.BrandGithub
import com.woowla.compose.icon.collections.tabler.tabler.outline.Clock
import com.woowla.compose.icon.collections.tabler.tabler.outline.Edit
import com.woowla.compose.icon.collections.tabler.tabler.outline.Filter
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.Rocket
import com.woowla.compose.icon.collections.tabler.tabler.outline.Tag
import com.woowla.compose.icon.collections.tabler.tabler.outline.Trash
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.SyncResultEntryWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.core.extensions.toColor
import com.woowla.ghd.presentation.i18nUi
import com.woowla.ghd.presentation.app.AppColors.gitPrMerged
import com.woowla.ghd.presentation.decorators.PullRequestDecorator
import com.woowla.ghd.presentation.decorators.ReleaseDecorator
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.decorators.SyncResultEntryDecorator
import com.woowla.ghd.core.utils.openWebpage
import com.woowla.ghd.domain.entities.DeploymentWithRepo
import com.woowla.ghd.presentation.app.AppColors.info
import com.woowla.ghd.presentation.app.AppColors.warning
import com.woowla.ghd.presentation.decorators.DeploymentDecorator
import com.woowla.ghd.presentation.decorators.PullRequestReviewDecisionDecorator
import com.woowla.ghd.presentation.decorators.ReviewDecorator
import com.woowla.ghd.presentation.decorators.ReviewRequestDecorator

@Composable
fun SynResultCard(
    syncResultWithEntries: SyncResultWithEntriesAndRepos,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val decorator = SyncResultDecorator(syncResultWithEntries)
    val duration = syncResultWithEntries.syncResult.duration
    val durationText = if (duration == null) {
        i18nUi.screen_sync_results_in_progress
    } else {
        i18nUi.screen_sync_results_took_seconds((duration.inWholeSeconds))
    }
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        title = i18nUi.screen_sync_results_start_at(syncResultWithEntries.syncResult.startAt),
        subtitle = durationText,
        leadingContent = {
            Avatar(
                image = Tabler.Outline.Refresh,
                icon = decorator.icon,
                iconTint = decorator.iconTint()
            )
        },
    )
}

@Composable
fun SynResultEntryCard(
    syncResultEntryWithRepo: SyncResultEntryWithRepo,
    modifier: Modifier = Modifier,
) {
    val decorator = SyncResultEntryDecorator(syncResultEntryWithRepo.syncResultEntry)

    val title = syncResultEntryWithRepo
        .syncResultEntry
        .origin.toString() + " " + (syncResultEntryWithRepo.repoToCheck?.let { RepoToCheckDecorator(it) }?.fullRepo ?: "")
    CardListItem(
        modifier = modifier,
        onClick = {},
        title = title,
        subtitle = i18nUi.screen_sync_result_entries_took_seconds(syncResultEntryWithRepo.syncResultEntry.duration.inWholeSeconds),
        leadingContent = {
            Avatar(
                image = decorator.originIcon,
                icon = decorator.statusIcon,
                iconTint = decorator.statusIconTint()
            )
        },
        supportingContent = if (syncResultEntryWithRepo.syncResultEntry.isSuccess) {
            null
        } else {
            {
                Text(
                    text = syncResultEntryWithRepo.syncResultEntry.errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepoToCheckCard(
    repoToCheck: RepoToCheck,
    onOpenClick: (RepoToCheck) -> Unit,
    onEditClick: (RepoToCheck) -> Unit,
    onDeleteClick: (RepoToCheck) -> Unit,
    modifier: Modifier = Modifier,
) {
    val repoToCheckDecorator = RepoToCheckDecorator(repoToCheck)

    CardListItem(
        modifier = modifier,
        onClick = { onOpenClick.invoke(repoToCheck) },
        title = repoToCheckDecorator.fullRepo,
        subtitle = if (repoToCheck.groupName.isNullOrBlank()) { i18nUi.screen_edit_repo_to_no_group } else { repoToCheck.groupName },
        leadingContent = {
            Avatar(
                image = Tabler.Outline.BrandGithub,
                icon = Tabler.Outline.Book2,
            )
        },
        supportingContent = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val groupName = repoToCheck.groupName
                if (!groupName.isNullOrBlank()) {
                    Tag(
                        text = groupName,
                        icon = null,
                        color = groupName.toColor(),
                    )
                }
                if (repoToCheck.arePullRequestsEnabled) {
                    Tag(
                        text = "Pulls",
                        icon = repoToCheckDecorator.pullRequestsSyncIcon,
                    )
                }
                if (repoToCheck.areReleasesEnabled) {
                    Tag(
                        text = "Releases",
                        icon = repoToCheckDecorator.releasesSyncIcon,
                    )
                }
                if (repoToCheck.areDeploymentsEnabled) {
                    Tag(
                        text = "Deployments",
                        icon = repoToCheckDecorator.deploymentsSyncIcon,
                    )
                }
                if (!repoToCheck.pullBranchRegex.isNullOrBlank()) {
                    Tag(
                        text = "Filtered",
                        icon = Tabler.Outline.Filter,
                    )
                }
            }
        },
        hoverContent = { paddingValues ->
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                ElevatedAssistChip(
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = { onEditClick.invoke(repoToCheck) },
                    modifier = Modifier.height(30.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Tabler.Outline.Edit,
                            contentDescription = null,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    },
                    label = {
                        Text(text = "Edit")
                    }
                )
                Spacer(modifier = Modifier.height(15.dp))
                ElevatedAssistChip(
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer,
                        trailingIconContentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    onClick = { onDeleteClick.invoke(repoToCheck) },
                    modifier = Modifier.height(30.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Tabler.Outline.Trash,
                            contentDescription = null,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    },
                    label = {
                        Text(text = i18nUi.generic_delete)
                    }
                )
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReleaseCard(
    releaseWithRepo: ReleaseWithRepo,
    modifier: Modifier = Modifier,
) {
    val releaseDecorator = ReleaseDecorator(releaseWithRepo)

    CardListItem(
        modifier = modifier,
        onClick = { openWebpage(releaseWithRepo.release.url) },
        title = releaseDecorator.name,
        subtitle = releaseDecorator.fullRepo,
        leadingContent = {
            Avatar(
                imageUrl = releaseWithRepo.release.author?.avatarUrl,
                icon = Tabler.Outline.Tag,
            )
        },
        supportingContent = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val groupName = releaseWithRepo.repoToCheck.groupName
                if (!groupName.isNullOrBlank()) {
                    Tag(
                        text = groupName,
                        icon = null,
                        color = groupName.toColor(),
                    )
                }
                Tag(
                    text = releaseWithRepo.release.tagName,
                    icon = null,
                )
                Tag(
                    text = releaseDecorator.publishedAt,
                    icon = Tabler.Outline.Clock,
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PullRequestCard(
    pullRequestWithReviews: PullRequestWithRepoAndReviews,
    modifier: Modifier = Modifier,
) {
    val pullRequestDecorator = PullRequestDecorator(pullRequestWithReviews)
    val showExtras = pullRequestWithReviews.pullRequest.stateExtended == PullRequestStateExtended.OPEN ||
        pullRequestWithReviews.pullRequest.stateExtended == PullRequestStateExtended.DRAFT

    CardListItem(
        modifier = modifier,
        onClick = { openWebpage(pullRequestWithReviews.pullRequest.url) },
        title = pullRequestDecorator.title,
        subtitle = pullRequestDecorator.fullRepo,
        leadingContent = {
            Avatar(
                imageUrl = pullRequestWithReviews.pullRequest.author?.avatarUrl,
                icon = pullRequestDecorator.state.icon,
                iconTint = pullRequestDecorator.state.iconTint(),
            )
        },
        supportingContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Tag(
                        text = pullRequestDecorator.state.text,
                        icon = pullRequestDecorator.state.icon,
                        color = pullRequestDecorator.state.iconTint(),
                    )
                    val date = when(pullRequestWithReviews.pullRequest.stateExtended) {
                        PullRequestStateExtended.OPEN,
                        PullRequestStateExtended.DRAFT,
                        PullRequestStateExtended.UNKNOWN -> {
                            pullRequestDecorator.createdAt
                        }
                        PullRequestStateExtended.CLOSED -> {
                            pullRequestDecorator.closedAt
                        }
                        PullRequestStateExtended.MERGED -> {
                            pullRequestDecorator.mergedAt
                        }
                    }
                    Tag(
                        text = date,
                        icon = Tabler.Outline.Clock,
                    )
                    if (showExtras) {
                        // has conflicts
                        if (pullRequestWithReviews.pullRequest.hasConflicts) {
                            Tag(
                                text = "Conflicts",
                                icon = Tabler.Outline.Boom,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        // can be merged
                        if (pullRequestWithReviews.pullRequest.canBeMerged) {
                            Tag(
                                text = i18nUi.screen_pull_requests_can_be_merged,
                                icon = Tabler.Outline.Rocket,
                                color = MaterialTheme.colorScheme.gitPrMerged
                            )
                        }
                        // commit checks
                        if (pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus != CommitCheckRollupStatus.SUCCESS) {
                            Tag(
                                text = pullRequestDecorator.commitChecks,
                                icon = pullRequestDecorator.commitChecksIcon,
                                color = pullRequestDecorator.commitChecksColor(),
                            )
                        }
                        // review decision
                        var reviewDecisionHover by remember { mutableStateOf(false) }
                        val reviewDecisionDecorator = PullRequestReviewDecisionDecorator(pullRequestWithReviews.pullRequest.reviewDecision)
                        val reviewerDecisionFullText = reviewDecisionDecorator.text
                        var reviewerDecisionText by remember { mutableStateOf("") }
                        Tag(
                            text = reviewerDecisionText,
                            icon = reviewDecisionDecorator.icon,
                            color = reviewDecisionDecorator.color(),
                            modifier = Modifier
                                .onPointerEvent(PointerEventType.Enter) {
                                    reviewDecisionHover = true
                                    reviewerDecisionText = reviewerDecisionFullText
                                }
                                .onPointerEvent(PointerEventType.Exit) {
                                    reviewDecisionHover = false
                                    reviewerDecisionText = ""
                                }
                        )
                        // reviews
                        pullRequestWithReviews.reviews.forEach { review ->
                            val reviewDecorator = ReviewDecorator(review)
                            val reviewerLogin = reviewDecorator.authorLogin
                            var reviewerText by remember { mutableStateOf("") }
                            var reviewerHover by remember { mutableStateOf(false) }
                            if (reviewDecisionHover || reviewerHover) {
                                reviewerText = reviewerLogin
                            } else {
                                reviewerText = ""
                            }
                            Tag(
                                text = reviewerText,
                                icon = reviewDecorator.icon,
                                color = reviewDecorator.stateColor(),
                                modifier = Modifier
                                    .onPointerEvent(PointerEventType.Enter) {
                                        reviewerHover = true
                                    }
                                    .onPointerEvent(PointerEventType.Exit) {
                                        reviewerHover = false
                                    }
                            )
                        }
                        // review requests
                        pullRequestWithReviews.reviewRequests.forEach { reviewRequest ->
                            val reviewRequestDecorator = ReviewRequestDecorator(reviewRequest)
                            val reviewRequestLogin = reviewRequestDecorator.authorLogin
                            var reviewRequestText by remember { mutableStateOf("") }
                            var reviewRequestHover by remember { mutableStateOf(false) }
                            if (reviewDecisionHover || reviewRequestHover) {
                                reviewRequestText = reviewRequestLogin
                            } else {
                                reviewRequestText = ""
                            }
                            Tag(
                                text = reviewRequestText,
                                icon = reviewRequestDecorator.icon,
                                color = MaterialTheme.colorScheme.warning,
                                modifier = Modifier
                                    .onPointerEvent(PointerEventType.Enter) {
                                        reviewRequestHover = true
                                    }
                                    .onPointerEvent(PointerEventType.Exit) {
                                        reviewRequestHover = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DeploymentCard(
    deploymentWithRepo: DeploymentWithRepo,
    modifier: Modifier = Modifier,
) {
    val deploymentDecorator = DeploymentDecorator(deploymentWithRepo)
    val deployment = deploymentWithRepo.deployment
    CardListItem(
        modifier = modifier,
        onClick = { },
        title = "Deployment #${deployment.id}",
        subtitle = deploymentDecorator.fullRepo,
        leadingContent = {
            Avatar(
                imageUrl = deployment.creator.avatarUrl,
                icon = Tabler.Outline.Tag,
            )
        },
        supportingContent = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val groupName = deploymentWithRepo.repoToCheck.groupName
                if (!groupName.isNullOrBlank()) {
                    Tag(
                        text = groupName,
                        icon = null,
                        color = groupName.toColor(),
                    )
                }
                Tag(
                    text = deploymentDecorator.createdAt,
                    icon = Tabler.Outline.Clock,
                )
                Tag("id: ${deployment.id}", icon = null)
                Tag("description: ${deployment.description}", icon = null)
                Tag("payload: ${deployment.payload}", icon = null)
                Tag("creator: ${deployment.creator.login}", icon = null)
                Tag("environment: ${deployment.environment}", icon = null)
                Tag("latestEnvironment: ${deployment.latestEnvironment}", icon = null)
                Tag("originalEnvironment: ${deployment.originalEnvironment}", icon = null)
                Tag("state: ${deployment.state}", icon = null)
                Tag("task: ${deployment.task}", icon = null)
                deployment.statuses.forEach { status ->
                    Tag(
                        text = "${status.state} -> ${status.description}",
                        icon = null,
                        color = MaterialTheme.colorScheme.info
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardListItem(
    onClick: (() -> Unit),
    title: String,
    subtitle: String? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    hoverContent: (@Composable (paddingValues: PaddingValues) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var hover by remember { mutableStateOf(false) }
    val paddingValues = PaddingValues(12.dp)

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { hover = true }
            .onPointerEvent(PointerEventType.Exit) { hover = false },
    ) {
        Box {
            Column(
                modifier = Modifier.padding(paddingValues),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leadingContent?.invoke()
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                supportingContent?.invoke()
            }
            this@ElevatedCard.AnimatedVisibility(
                visible = hover,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    hoverContent?.invoke(paddingValues)
                }
            }
        }
    }
}

