package com.woowla.ghd.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.compose.remixicon.CommunicationChat3Line
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.RemixiconRes
import com.woowla.compose.remixicon.SystemCheckboxBlankCircleFill
import com.woowla.compose.remixicon.SystemCheckboxBlankCircleLine
import com.woowla.compose.remixicon.SystemCheckboxCircleLine
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Placeholder
import com.woowla.ghd.presentation.decorators.PullRequestDecorator
import com.woowla.ghd.utils.MaterialColors
import com.woowla.ghd.utils.openWebpage
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun PullRequestCard(pullRequest: PullRequest, onSeenClick: () -> Unit) {
    val pullRequestDecorator = PullRequestDecorator(pullRequest)
    val seen = pullRequest.appSeen
    val avatarImageSize = 45.dp

    CardListItem(
        modifier = Modifier.width(AppDimens.contentWidthDp.dp),
        selected = seen,
        onClick = {
            openWebpage(pullRequest.url)
        },
        leadingContent = { _ ->
            Box(modifier = Modifier.fillMaxHeight().width(5.dp).background(pullRequestDecorator.state.iconTint))
        },
        hoverContent = { paddingValues ->
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                TextButton(
                    onClick = { onSeenClick.invoke() },
                    contentPadding = PaddingValues(all = 5.dp),
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        painter = if (seen) { RemixiconPainter.SystemCheckboxCircleLine } else { RemixiconPainter.SystemCheckboxBlankCircleLine },
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.padding(start = avatarImageSize + 10.dp))
            }
        },
        overlineText = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
            ) {
                if (pullRequestDecorator.totalCommentCount.isNotBlank()) {
                    Icon(
                        painter = RemixiconPainter.CommunicationChat3Line,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MaterialColors.Gray700
                    )
                }
                Text(text = (pullRequestDecorator.totalCommentCount + pullRequestDecorator.updatedAt), maxLines = 1, overflow = TextOverflow.Ellipsis,)
            }
        },
        headlineText = {
            Text(text = pullRequestDecorator.fullRepo, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingText = {
            Text(text = pullRequestDecorator.authorWithTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {
                KamelImage(
                    resource = lazyPainterResource(data = pullRequest.authorAvatarUrl ?: ""),
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
        },
    )
}