package com.woowla.ghd.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.extensions.toHRString
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Placeholder
import com.woowla.ghd.utils.openWebpage
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun ReleaseCard(release: Release) {
    val avatarImageSize = 45.dp

    CardListItem(
        modifier = Modifier.width(AppDimens.contentWidthDp),
        onClick = {
            openWebpage(release.url)
        },
        overlineText = {
            Text(text = release.publishedAt.toHRString(), maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        headlineText = {
            Text(text = "${release.repoToCheck.owner}/${release.repoToCheck.name}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingText = {
            Text(text = "${release.name} (${release.tagName}) - ${release.authorLogin}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {
                KamelImage(
                    resource = lazyPainterResource(data = release.authorAvatarUrl ?: ""),
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