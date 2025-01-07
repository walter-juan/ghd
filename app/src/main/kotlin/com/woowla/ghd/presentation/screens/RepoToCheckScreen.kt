package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.FileDownload
import com.woowla.compose.icon.collections.tabler.tabler.outline.FileExport
import com.woowla.compose.icon.collections.tabler.tabler.outline.FileImport
import com.woowla.compose.icon.collections.tabler.tabler.outline.FileUpload
import com.woowla.compose.icon.collections.tabler.tabler.outline.InfoCircle
import com.woowla.compose.icon.collections.tabler.tabler.outline.Plus
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel

object RepoToCheckScreen {
    @Composable
    fun Content(
        onEditRepoClick: (RepoToCheck) -> Unit,
        onAddNewRepoClick: () -> Unit,
        onBulkExampleClick: () -> Unit,
    ) {
        val viewModel = viewModel { ReposToCheckViewModel() }
        val reposState by viewModel.state.collectAsState()

        var isBulkImportFileDialogOpen by remember { mutableStateOf(false) }
        if (isBulkImportFileDialogOpen) {
            FileLoadDialog(
                onCloseRequest = { file ->
                    isBulkImportFileDialogOpen = false
                    viewModel.bulkImportRepo(file)
                }
            )
        }

        var isBulkExportFileDialogOpen by remember { mutableStateOf(false) }
        if (isBulkExportFileDialogOpen) {
            FileSaveDialog(
                fileName = "ghd-repos.yml",
                onCloseRequest = { file ->
                    isBulkExportFileDialogOpen = false
                    viewModel.bulkExportRepo(file)
                }
            )
        }

        ScreenScrollable(
            topBar = { TopBar(title = i18n.top_bar_title_repos_to_check) }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                val lockedState = reposState
                when(lockedState) {
                    ReposToCheckViewModel.State.Initializing -> { }
                    is ReposToCheckViewModel.State.Error -> {
                        Text(i18n.generic_error)
                    }
                    is ReposToCheckViewModel.State.Success -> {
                        SectionCategory(i18n.screen_repos_to_check_new_repositories_section) {
                            SectionItem(
                                title = i18n.screen_repos_to_check_add_new_repository_item,
                                description = i18n.screen_repos_to_check_add_new_repository_item_description,
                            ) {
                                Button(onClick = { onAddNewRepoClick.invoke() }) {
                                    Icon(imageVector = Tabler.Outline.Plus, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            }
                            SectionItem(
                                title = i18n.screen_repos_to_check_bulk_item,
                                description = i18n.screen_repos_to_check_bulk_item_description,
                            ) {
                                Row {
                                    Button(onClick = onBulkExampleClick) {
                                        Row {
                                            Icon(
                                                imageVector = Tabler.Outline.InfoCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(i18n.screen_repos_to_check_bulk_example)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Button(onClick = { isBulkImportFileDialogOpen = true }) {
                                        Row {
                                            Icon(
                                                imageVector = Tabler.Outline.FileImport,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(i18n.screen_repos_to_check_bulk_import)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Button(onClick = { isBulkExportFileDialogOpen = true }) {
                                        Row {
                                            Icon(
                                                imageVector = Tabler.Outline.FileExport,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(i18n.screen_repos_to_check_bulk_export)
                                        }
                                    }
                                }
                            }
                        }
                        SectionCategory(i18n.screen_repos_to_check_repositories_section) {
                            SectionItem(
                                title = i18n.screen_app_settings_repositories_item,
                                description = i18n.screen_app_settings_repositories_item_description(lockedState.size),
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    lockedState.groupedReposToCheck.forEachIndexed { index, (groupName, reposToCheck) ->
                                        if (groupName != null) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.width(AppDimens.contentWidthDp)
                                            ) {
                                                Text(
                                                    text = buildAnnotatedString {
                                                        if(groupName.isBlank()) {
                                                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                                                append(i18n.screen_edit_repo_to_no_group)
                                                            }
                                                        } else {
                                                            append(groupName)
                                                        }
                                                    },
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            }
                                        }
                                        reposToCheck.forEach { repoToCheck ->
                                            RepoToCheckCard(
                                                repoToCheck = repoToCheck,
                                                onEditClick = { repoToEdit ->
                                                    onEditRepoClick.invoke(repoToEdit)
                                                },
                                                onDeleteClick = { repoToDelete ->
                                                    viewModel.deleteRepo(repoToDelete)
                                                },
                                            )
                                        }
                                        if (index < lockedState.groupedReposToCheck.size - 1) {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp).width(AppDimens.contentWidthDp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
