package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.decorators.RepoToCheckDecorator
import com.woowla.ghd.presentation.viewmodels.ReposToCheckViewModel
import com.woowla.ghd.utils.MaterialColors

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

        val scaffoldState = rememberScaffoldState()

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
            scaffoldState = scaffoldState,
            topBar = { TopBar(title = i18n.top_bar_title_repos_to_check) }
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp.dp)
                    .width(AppDimens.contentWidthDp.dp)
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
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                }
                            }

                            Row {
                                Box(modifier = Modifier.weight(1f)) {
                                    SectionItem(
                                        title = i18n.screen_repos_to_check_bulk_import_item,
                                        description = i18n.screen_repos_to_check_bulk_import_item_description,
                                    ) {
                                        Button(onClick = { isBulkImportFileDialogOpen = true }) {
                                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null)
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SectionItem(
                                        title = i18n.screen_repos_to_check_export_item,
                                        description = i18n.screen_repos_to_check_export_item_description,
                                    ) {
                                        Button(onClick = { isBulkExportFileDialogOpen = true }) {
                                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
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

@Composable
private fun RepoCard(repoToCheck: RepoToCheck, onEditClick: (RepoToCheck) -> Unit, onDeleteClick: (RepoToCheck) -> Unit) {
    val repoToCheckDecorator = RepoToCheckDecorator(repoToCheck)
    Card(
        modifier = Modifier.clickable { onEditClick.invoke(repoToCheck) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialColors.Gray700
                    )
                    Text(
                        text = repoToCheckDecorator.fullRepo,
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = repoToCheckDecorator.enabledNotifications,
                            style = MaterialTheme.typography.body2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialColors.Gray700
                        )
                    }
                }

                Button(
                    onClick = {
                        onDeleteClick.invoke(repoToCheck)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialColors.Red700),
                    contentPadding = PaddingValues(all = 5.dp),
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialColors.Gray100,
                    )
                }
            }
        }
    }
}

