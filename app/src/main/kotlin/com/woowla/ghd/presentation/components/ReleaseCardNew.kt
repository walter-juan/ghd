package com.woowla.ghd.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.woowla.compose.octoicons.OctoiconsPainter
import com.woowla.compose.octoicons.Tag
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemTimeLine
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Placeholder
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.decorators.ReleaseDecoratorNew
import com.woowla.ghd.utils.openWebpage
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun ReleaseCardNew(
    release: Release,
    useBoldStyle: Boolean
) {
    val releaseDecorator = ReleaseDecoratorNew(release)
    val avatarImageSize = 45.dp

    IconCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            openWebpage(release.url)
        },
        trailingContent = { paddingValues, _ ->
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
        content = {
            IconCardRowTitle(text = releaseDecorator.name, icon = OctoiconsPainter.Tag)
            IconCardRowSmallContent(
                text = buildAnnotatedString {
                    if (useBoldStyle) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(release.tagName)
                        }
                    } else {
                        append(release.tagName)
                    }
                    append(" ${i18n.release_on} ")
                    if (useBoldStyle) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(releaseDecorator.fullRepo)
                        }
                    } else {
                        append(releaseDecorator.fullRepo)
                    }
                },
            )
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            IconCardRowSmallContent(text = releaseDecorator.publishedAt, icon = RemixiconPainter.SystemTimeLine)
        }
    )
}