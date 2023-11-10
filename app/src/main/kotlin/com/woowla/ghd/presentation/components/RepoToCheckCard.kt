package com.woowla.ghd.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.woowla.compose.remixicon.EditorListUnordered
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemDeleteBinLine
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
            IconCardRowSmallContent(text = repoToCheckDecorator.enabledFeatures)
        }
    )
}