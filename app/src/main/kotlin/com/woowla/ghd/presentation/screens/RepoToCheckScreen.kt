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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.compose.remixicon.DocumentFileDownloadLine
import com.woowla.compose.remixicon.DocumentFileUploadLine
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemAddLine
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel

class RepoToCheckScreen: Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { ReposToCheckViewModel() }
        val navigator = LocalNavigator.currentOrThrow
        val onEditRepoClick: (RepoToCheck) -> Unit = {
            navigator.push(RepoToCheckEditScreen(repoToCheck = it))
        }
        val onAddNewRepoClick: () -> Unit = {
            navigator.push(RepoToCheckEditScreen(repoToCheck = null))
        }

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
                            ) {
                                Button(onClick = { onAddNewRepoClick.invoke() }) {
                                    Icon(painter = RemixiconPainter.SystemAddLine, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            }

                            Row {
                                Box(modifier = Modifier.weight(1f)) {
                                    SectionItem(
                                        title = i18n.screen_repos_to_check_bulk_import_item,
                                        description = i18n.screen_repos_to_check_bulk_import_item_description,
                                    ) {
                                        Button(onClick = { isBulkImportFileDialogOpen = true }) {
                                            Icon(painter = RemixiconPainter.DocumentFileUploadLine, contentDescription = null, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SectionItem(
                                        title = i18n.screen_repos_to_check_export_item,
                                        description = i18n.screen_repos_to_check_export_item_description,
                                    ) {
                                        Button(onClick = { isBulkExportFileDialogOpen = true }) {
                                            Icon(painter = RemixiconPainter.DocumentFileDownloadLine, contentDescription = null, modifier = Modifier.size(20.dp))
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
                                    if (lockedState.appSettings?.featurePreviewNewCards == true) {
                                        RepoToCheckCardNew(
                                            repoToCheck = repoToCheck,
                                            onEditClick = { repoToEdit ->
                                                onEditRepoClick.invoke(repoToEdit)
                                            },
                                            onDeleteClick = { repoToDelete ->
                                                viewModel.deleteRepo(repoToDelete)
                                            },
                                        )
                                    } else {
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
