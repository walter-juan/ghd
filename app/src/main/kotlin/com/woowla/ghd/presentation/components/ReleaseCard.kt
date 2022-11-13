package com.woowla.ghd.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.ghd.presentation.app.AppIcons
import com.woowla.ghd.utils.MaterialColors
import com.woowla.ghd.utils.openWebpage
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReleaseCard(
    text: String,
    textIcon: Painter? = null,
    textIconTint: Color? = null,
    overlineText: String = "",
    overlineIcon: Painter? = null,
    overlineIconTint: Color? = null,
    secondaryText: String = "",
    secondaryIcon: Painter? = null,
    secondaryIconTint: Color? = null,
    categoryColor: Color? = null,
    linkUrl: String? = null,
    imageUrl: String? = null
) {
    var hover by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !linkUrl.isNullOrBlank(),
                onClick = { linkUrl?.let(::openWebpage) }
            )
            .onPointerEvent(PointerEventType.Enter) { hover = true  }
            .onPointerEvent(PointerEventType.Exit) { hover = false }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(color = if (hover) { MaterialColors.LightBlue50 } else { MaterialColors.White } )
        ) {
            if (categoryColor != null) {
                Box(
                    modifier = Modifier.fillMaxHeight().width(5.dp).background(categoryColor)
                )
            }
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (overlineIcon != null) {
                            Icon(
                                painter = overlineIcon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = overlineIconTint ?: MaterialTheme.colors.secondaryVariant
                            )
                        }
                        Text(
                            text = overlineText,
                            style = MaterialTheme.typography.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialColors.Gray700
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (textIcon != null) {
                            Icon(
                                painter = textIcon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = textIconTint ?: MaterialTheme.colors.primary
                            )
                        }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.subtitle1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (secondaryIcon != null) {
                            Icon(
                                painter = secondaryIcon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = secondaryIconTint ?: MaterialTheme.colors.secondary
                            )
                        }
                        Text(
                            text = secondaryText,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialColors.Gray700
                        )
                    }
                }

                KamelImage(
                    resource = lazyPainterResource(data = imageUrl ?: ""),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(45.dp).clip(CircleShape),
                    onLoading = {
                        Image(
                            painter = painterResource(AppIcons.placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(45.dp).clip(CircleShape),
                        )
                    },
                    onFailure = {
                        Image(
                            painter = painterResource(AppIcons.placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(45.dp).clip(CircleShape),
                        )
                    }
                )
            }
        }
    }
}