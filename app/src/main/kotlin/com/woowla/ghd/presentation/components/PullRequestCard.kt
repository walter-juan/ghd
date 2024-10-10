package com.woowla.ghd.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Filled
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.filled.CircleCheck
import com.woowla.compose.icon.collections.tabler.tabler.outline.*
import com.woowla.ghd.domain.entities.PullRequestStateWithDraft
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Placeholder
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.decorators.PullRequestDecorator
import com.woowla.ghd.utils.openWebpage
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun PullRequestCard(
    pullRequestWithReviews: PullRequestWithRepoAndReviews,
    onSeenClick: () -> Unit
) {
    val avatarImageSize = 45.dp
    val pullRequestDecorator = PullRequestDecorator(pullRequestWithReviews)
    val seen = pullRequestWithReviews.pullRequest.appSeen
    val showExtras = !seen && (pullRequestWithReviews.pullRequest.stateWithDraft == PullRequestStateWithDraft.OPEN || pullRequestWithReviews.pullRequest.stateWithDraft == PullRequestStateWithDraft.DRAFT)

    IconCard(
        selected = seen,
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            openWebpage(pullRequestWithReviews.pullRequest.url)
        },
        hoverContent = { paddingValues ->
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                ElevatedAssistChip(
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = { onSeenClick.invoke() },
                    modifier = Modifier.height(30.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = if (seen) { Tabler.Filled.CircleCheck } else { Tabler.Outline.Circle },
                            contentDescription = null,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    },
                    label = {
                        if (seen) {
                            Text(text = i18n.pull_request_unseen)
                        } else {
                            Text(text = i18n.pull_request_seen)
                        }
                    }
                )
            }
        },
        leadingContent = { _, _ ->
            Box(modifier = Modifier.fillMaxHeight().width(5.dp).background(pullRequestDecorator.state.iconTint()))
        },
        trailingContent = { paddingValues, hover ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Crossfade(targetState = hover) { targetState ->
                    if (targetState) {
                        Box(modifier = Modifier.size(avatarImageSize))
                    } else {
                        KamelImage(
                            resource = lazyPainterResource(data = pullRequestWithReviews.pullRequest.author?.avatarUrl ?: ""),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(avatarImageSize).clip(CircleShape),
                            onLoading = {
                                Image(
                                    painter = AppIconsPainter.Placeholder,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(avatarImageSize).clip(CircleShape),
                                )
                            },
                            onFailure = {
                                Image(
                                    painter = AppIconsPainter.Placeholder,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(avatarImageSize).clip(CircleShape),
                                )
                            }
                        )
                    }
                }
            }
        },
        content = {
            IconCardRowTitle(
                text = pullRequestDecorator.title,
                icon = pullRequestDecorator.state.icon,
                iconTint = pullRequestDecorator.state.iconTint(),
            )
            IconCardRowSmallContent(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("#${pullRequestWithReviews.pullRequest.number}")
                    }
                    append(" ${i18n.pull_request_opened_by} ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(pullRequestDecorator.authorLogin)
                    }
                    append(" ${i18n.pull_request_on} ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(pullRequestDecorator.fullRepo)
                    }
                }
            )
            IconCardRowSmallContent(
                text = pullRequestDecorator.updatedAt,
                icon = Tabler.Outline.Clock
            )
            if (showExtras) {
                if (pullRequestDecorator.showMergeableBadge) {
                    IconCardRowSmallContent(
                        text = pullRequestDecorator.mergeable,
                        icon = Tabler.Outline.GitMerge,
                        showBadge = true,
                        badgeColor = pullRequestDecorator.mergeableBadgeColor(),
                    )
                }
                IconCardRowSmallContent(
                    text = pullRequestDecorator.commitChecks,
                    icon = Tabler.Outline.ListDetails,
                    showBadge = pullRequestDecorator.showCommitsCheckBadge,
                    badgeColor = pullRequestDecorator.commitsCheckBadgeColor(),
                )
                if (pullRequestWithReviews.pullRequest.stateWithDraft == PullRequestStateWithDraft.OPEN) {
                    IconCardRowSmallContent(
                        text = pullRequestDecorator.reviews(),
                        icon = Tabler.Outline.Users,
                        showBadge = pullRequestDecorator.showReviewsBadge,
                        badgeColor = pullRequestDecorator.reviewsBadgeColor(),
                    )
                }
                IconCardRowSmallContent(
                    text = pullRequestDecorator.comments,
                    icon = Tabler.Outline.MessageCircle
                )
            }
        }
    )
}
