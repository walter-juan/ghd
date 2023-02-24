package com.woowla.ghd.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.woowla.compose.remixicon.EditorListUnordered
import com.woowla.compose.remixicon.MediaNotification4Line
import com.woowla.compose.remixicon.MediaNotificationOffLine
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemDeleteBinLine
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoToCheckCardNew(repoToCheck: RepoToCheck, onEditClick: (RepoToCheck) -> Unit, onDeleteClick: (RepoToCheck) -> Unit) {
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
                            painter = RemixiconPainter.SystemDeleteBinLine,
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
                icon = RemixiconPainter.EditorListUnordered
            )
            if (repoToCheck.pullNotificationsEnabled && repoToCheck.releaseNotificationsEnabled) {
                IconCardRowSmallContent(
                    text = i18n.screen_edit_repo_to_all_notifications_enabled,
                    icon = RemixiconPainter.MediaNotification4Line
                )
            } else if (!repoToCheck.pullNotificationsEnabled && !repoToCheck.releaseNotificationsEnabled) {
                IconCardRowSmallContent(
                    text = i18n.screen_edit_repo_to_all_notifications_disabled,
                    icon = RemixiconPainter.MediaNotificationOffLine
                )
            } else {
                IconCardRowSmallContent(
                    text = i18n.screen_edit_repo_to_all_pull_request_notifications_enabled(repoToCheck.pullNotificationsEnabled),
                    icon = if (repoToCheck.pullNotificationsEnabled) {
                        RemixiconPainter.MediaNotification4Line
                    } else {
                        RemixiconPainter.MediaNotificationOffLine
                    }
                )
                IconCardRowSmallContent(
                    text = i18n.screen_edit_repo_to_all_releases_notifications_enabled(repoToCheck.releaseNotificationsEnabled),
                    icon = if (repoToCheck.releaseNotificationsEnabled) {
                        RemixiconPainter.MediaNotification4Line
                    } else {
                        RemixiconPainter.MediaNotificationOffLine
                    }
                )
            }
        }
    )
}