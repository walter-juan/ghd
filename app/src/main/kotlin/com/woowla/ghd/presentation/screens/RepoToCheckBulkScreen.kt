package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.TableExport
import com.woowla.compose.icon.collections.tabler.tabler.outline.TableImport
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.components.Section
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.viewmodels.ReposToCheckBulkViewModel
import com.woowla.ghd.presentation.viewmodels.ReposToCheckBulkStateMachine

object RepoToCheckBulkScreen {
    @Composable
    fun Content(
        onBackClick: (() -> Unit),
    ) {
        val viewModel = viewModel { ReposToCheckBulkViewModel() }
        val state = viewModel.state.collectAsState().value

        var isBulkImportFileDialogOpen by remember { mutableStateOf(false) }
        if (isBulkImportFileDialogOpen) {
            FileLoadDialog(
                onCloseRequest = { file ->
                    isBulkImportFileDialogOpen = false
                    if (file != null) {
                        viewModel.dispatch(ReposToCheckBulkStateMachine.Act.ImportRepos(file))
                    }
                }
            )
        }

        var isBulkExportFileDialogOpen by remember { mutableStateOf(false) }
        if (isBulkExportFileDialogOpen) {
            FileSaveDialog(
                fileName = "ghd-repos.yml",
                onCloseRequest = { file ->
                    isBulkExportFileDialogOpen = false
                    if (file != null) {
                        viewModel.dispatch(ReposToCheckBulkStateMachine.Act.ExportRepos(file))
                    }
                }
            )
        }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = i18n.top_bar_title_repos_to_check_bulk,
                    navOnClick = onBackClick,
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                when (state) {
                    is ReposToCheckBulkStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    else -> {
                        Section(title = "Import") {
                            SectionItem(
                                title = "Import your repositories",
                                description = "Import a list of repositories from a YML format",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Tabler.Outline.TableImport,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            ) {
                                Button(
                                    onClick = { isBulkImportFileDialogOpen = true },
                                    content = { Text("Import") }
                                )
                            }
                        }

                        Section(title = "Export") {
                            SectionItem(
                                title = "Export your repositories",
                                description = "Export your repositories in a YML format",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Tabler.Outline.TableExport,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            ) {
                                Button(
                                    onClick = { isBulkExportFileDialogOpen = true },
                                    content = { Text("Export") }
                                )
                            }
                        }

                        Section(title = "YML example") {
                            SectionItem(
                                title = "Example of a YML file in case you want to create by yourself",
                            ) {
                                Text(
                                    text = i18n.screen_repos_to_check_bulk_sample_sample_file,
                                    fontSize = 18.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
