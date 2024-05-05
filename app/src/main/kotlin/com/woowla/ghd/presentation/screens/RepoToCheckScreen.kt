package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.remixicon.DocumentFileDownloadLine
import com.woowla.compose.remixicon.DocumentFileUploadLine
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemAddLine
import com.woowla.compose.remixicon.SystemInformationLine
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
                                    Icon(painter = RemixiconPainter.SystemAddLine, contentDescription = null, modifier = Modifier.size(20.dp))
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
                                                painter = RemixiconPainter.SystemInformationLine,
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
                                                painter = RemixiconPainter.DocumentFileUploadLine,
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
                                                painter = RemixiconPainter.DocumentFileDownloadLine,
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
                                description = i18n.screen_app_settings_repositories_item_description(lockedState.reposToCheck.size),
                            ) {
                                lockedState.reposToCheck.forEach { repoToCheck ->
                                    RepoToCheckCard(
                                        repoToCheck = repoToCheck,
                                        onEditClick = { repoToEdit ->
                                            onEditRepoClick.invoke(repoToEdit)
                                        },
                                        onDeleteClick = { repoToDelete ->
                                            viewModel.deleteRepo(repoToDelete)
                                        },
                                    )
                                    Spacer(modifier = Modifier.padding(5.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
