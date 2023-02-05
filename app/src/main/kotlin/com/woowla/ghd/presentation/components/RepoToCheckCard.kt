package com.woowla.ghd.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator
import com.woowla.ghd.utils.MaterialColors

@Composable
fun RepoToCheckCard(repoToCheck: RepoToCheck, onEditClick: (RepoToCheck) -> Unit, onDeleteClick: (RepoToCheck) -> Unit) {
    val repoToCheckDecorator = RepoToCheckDecorator(repoToCheck)

    CardListItem(
        onClick = { onEditClick.invoke(repoToCheck) },
        headlineText = {
            Text(repoToCheckDecorator.fullRepo, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingText = {
            Text(repoToCheckDecorator.enabledNotifications, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        hoverContent = { paddingValues ->
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TextButton(
                    onClick = { onDeleteClick.invoke(repoToCheck) },
                    contentPadding = PaddingValues(all = 5.dp),
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialColors.Red700,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
    )
}