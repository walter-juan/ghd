package com.woowla.ghd.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Filled
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.filled.Bell
import com.woowla.compose.icon.collections.tabler.tabler.filled.BellRinging
import com.woowla.compose.icon.collections.tabler.tabler.filled.BellRinging2
import com.woowla.compose.icon.collections.tabler.tabler.outline.Bell
import com.woowla.compose.icon.collections.tabler.tabler.outline.BellOff
import com.woowla.compose.icon.collections.tabler.tabler.outline.BellRinging
import com.woowla.compose.icon.collections.tabler.tabler.outline.BellRinging2
import com.woowla.compose.icon.collections.tabler.tabler.outline.List
import com.woowla.compose.icon.collections.tabler.tabler.outline.Refresh
import com.woowla.compose.icon.collections.tabler.tabler.outline.RefreshOff
import com.woowla.compose.icon.collections.tabler.tabler.outline.Trash
import com.woowla.compose.icon.collections.tabler.tabler.outline.X
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator

@Composable
fun RepoToCheckCard(repoToCheck: RepoToCheck, onEditClick: (RepoToCheck) -> Unit, onDeleteClick: (RepoToCheck) -> Unit) {
    val repoToCheckDecorator = RepoToCheckDecorator(repoToCheck)

    IconCard(
        onClick = { onEditClick.invoke(repoToCheck) },
        hoverContent = { paddingValues ->
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
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
        content = {
            IconCardRowTitle(text = repoToCheckDecorator.fullRepo)
            IconCardRowSmallContent(
                text = buildAnnotatedString {
                    if(repoToCheck.groupName.isNullOrBlank()) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(i18n.screen_edit_repo_to_no_group)
                        }
                    } else {
                        append(repoToCheck.groupName)
                    }
                },
                icon = Tabler.Outline.List
            )
            IconCardRowSmallContent {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = repoToCheckDecorator.pullRequestsSyncIcon,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Pulls",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = repoToCheckDecorator.releasesSyncIcon,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Releases",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}