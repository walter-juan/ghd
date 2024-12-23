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
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.Clock
import com.woowla.compose.icon.collections.tabler.tabler.outline.Tag
import com.woowla.ghd.domain.entities.ReleaseWithRepo
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Placeholder
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.decorators.ReleaseDecorator
import com.woowla.ghd.utils.openWebpage
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun ReleaseCard(releaseWithRepo: ReleaseWithRepo) {
    val releaseDecorator = ReleaseDecorator(releaseWithRepo)
    val avatarImageSize = 45.dp

    IconCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            openWebpage(releaseWithRepo.release.url)
        },
        trailingContent = { paddingValues, _ ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
            ) {
                KamelImage(
                    resource = lazyPainterResource(data = releaseWithRepo.release.author?.avatarUrl ?: ""),
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
            IconCardRowTitle(text = releaseDecorator.name, icon = Tabler.Outline.Tag)
            IconCardRowSmallContent(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(releaseWithRepo.release.tagName)
                    }
                    append(" ${i18n.release_on} ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(releaseDecorator.fullRepo)
                    }
                },
            )
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            IconCardRowSmallContent(text = releaseDecorator.publishedAt, icon = Tabler.Outline.Clock)
        }
    )
}