package com.woowla.ghd.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.AlertTriangle
import com.woowla.compose.icon.collections.tabler.tabler.outline.Book2
import com.woowla.compose.icon.collections.tabler.tabler.outline.Boom
import com.woowla.compose.icon.collections.tabler.tabler.outline.BrandGithub
import com.woowla.compose.icon.collections.tabler.tabler.outline.CarCrash
import com.woowla.compose.icon.collections.tabler.tabler.outline.Clock
import com.woowla.compose.icon.collections.tabler.tabler.outline.Filter
import com.woowla.compose.icon.collections.tabler.tabler.outline.GitMerge
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.Rocket
import com.woowla.compose.icon.collections.tabler.tabler.outline.Tag
import com.woowla.compose.icon.collections.tabler.tabler.outline.Trash
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.PullRequestStateExtended
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.domain.entities.SyncResultEntryWithRepo
import com.woowla.ghd.domain.entities.SyncResultWithEntriesAndRepos
import com.woowla.ghd.presentation.app.AppColors.gitPrMerged
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.decorators.PullRequestDecorator
import com.woowla.ghd.presentation.decorators.ReleaseDecorator
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator
import com.woowla.ghd.presentation.decorators.SyncResultDecorator
import com.woowla.ghd.presentation.decorators.SyncResultEntryDecorator
import com.woowla.ghd.utils.openWebpage

@Composable
fun SynResultCard(
    syncResultWithEntries: SyncResultWithEntriesAndRepos,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val decorator = SyncResultDecorator(syncResultWithEntries)
    val durationText = if (syncResultWithEntries.syncResult.duration == null) {
        i18n.screen_sync_results_in_progress
    } else {
        i18n.screen_sync_results_took_seconds((syncResultWithEntries.syncResult.duration.inWholeMilliseconds / 1000.0))
    }
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        title = i18n.screen_sync_results_start_at(syncResultWithEntries.syncResult.startAt),
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

    val title = syncResultEntryWithRepo.syncResultEntry.origin.toString() + " " + (syncResultEntryWithRepo.repoToCheck?.let { RepoToCheckDecorator(it) }?.fullRepo ?: "")
    CardListItem(
        modifier = modifier,
        onClick = {},
        title = title,
        subtitle = i18n.screen_sync_result_entries_took_seconds((syncResultEntryWithRepo.syncResultEntry.duration.inWholeMilliseconds / 1000.0)),
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
    onEditClick: (RepoToCheck) -> Unit,
    onDeleteClick: (RepoToCheck) -> Unit,
    modifier: Modifier = Modifier,
) {
    val repoToCheckDecorator = RepoToCheckDecorator(repoToCheck)

    CardListItem(
        modifier = modifier,
        onClick = { onEditClick.invoke(repoToCheck) },
        title = repoToCheckDecorator.fullRepo,
        subtitle = if(repoToCheck.groupName.isNullOrBlank()) { i18n.screen_edit_repo_to_no_group } else { repoToCheck.groupName },
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
                if (!repoToCheck.groupName.isNullOrBlank()) {
                    Tag(
                        text = repoToCheck.groupName,
                        icon = null,
                        color = MaterialTheme.colorScheme.primary,
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
                if (!repoToCheck.pullBranchRegex.isNullOrBlank()) {
                    Tag(
                        text = "Filtered",
                        icon = Tabler.Outline.Filter,
                    )
                }
            }
        },
        hoverContent = { paddingValues ->
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth().padding(paddingValues)
            ) {
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
                        Text(text = i18n.generic_delete)
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
                if (!releaseWithRepo.repoToCheck.groupName.isNullOrBlank()) {
                    Tag(
                        text = releaseWithRepo.repoToCheck.groupName,
                        icon = null,
                        color = MaterialTheme.colorScheme.primary,
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

@OptIn(ExperimentalLayoutApi::class)
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
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Tag(
                    text = "${pullRequestDecorator.state.text} (${pullRequestDecorator.createdAt})",
                    icon = pullRequestDecorator.state.icon,
                    color = pullRequestDecorator.state.iconTint()
                )
                if (pullRequestWithReviews.pullRequest.hasConflicts) {
                    Tag(
                        text = "Conflicts",
                        icon = Tabler.Outline.Boom,
                    )
                }
                if (showExtras) {
                    if (pullRequestWithReviews.pullRequest.canBeMerged) {
                        Tag(
                            text = i18n.screen_pull_requests_can_be_merged,
                            icon = Tabler.Outline.Rocket,
                            color = MaterialTheme.colorScheme.gitPrMerged
                        )
                    }
                    if (pullRequestWithReviews.reviews.isEmpty() || pullRequestWithReviews.reviews.any { it.state != ReviewState.APPROVED }) {
                        Tag(
                            text = pullRequestDecorator.reviewsNonApproved(),
                            icon = pullRequestDecorator.reviewsIcon()
                        )
                    }
                    if (pullRequestWithReviews.pullRequest.lastCommitCheckRollupStatus != CommitCheckRollupStatus.SUCCESS) {
                        Tag(
                            text = pullRequestDecorator.commitChecks,
                            icon = pullRequestDecorator.commitChecksIcon
                        )
                    }
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
                Box(modifier = Modifier
                    .fillMaxSize()) {
                    hoverContent?.invoke(paddingValues)
                }
            }
        }
    }
}
