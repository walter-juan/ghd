package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.FileDialog
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.SectionCategory
import com.woowla.ghd.presentation.components.SectionItem
import com.woowla.ghd.presentation.components.TopBar
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

        var isFileDialogOpen by remember { mutableStateOf(false) }
        if (isFileDialogOpen) {
            FileDialog(
                onCloseRequest = { file ->
                    isFileDialogOpen = false
                    viewModel.bulkImportRepo(file)
                }
            )
        }

        Screen(
            scaffoldState = scaffoldState,
            topBar = { TopBar(title = i18n.top_bar_title_repos_to_check) }
        ) {
            val lockedState = reposState
            when(lockedState) {
                is ReposToCheckViewModel.State.Error -> item { Text(i18n.generic_error) }
                ReposToCheckViewModel.State.Loading -> item { /* nothing, this should be fast to load? */ }
                is ReposToCheckViewModel.State.Success -> {
                    item {
                        SectionCategory(i18n.screen_repos_to_check_new_repositories_section) {
                            SectionItem(
                                title = i18n.screen_repos_to_check_add_new_repository_item,
                            ) {
                                Button(onClick = { onAddNewRepoClick.invoke() }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                }
                            }
                            SectionItem(
                                title = i18n.screen_repos_to_check_bulk_import_item,
                                description = i18n.screen_repos_to_check_bulk_import_item_description,
                            ) {
                                Button(onClick = { isFileDialogOpen = true }) {
                                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null)
                                }
                            }
                        }
                        SectionCategory(i18n.screen_repos_to_check_repositories_section) {
                            SectionItem(
                                title = i18n.screen_app_settings_repositories_item,
                                description = i18n.screen_app_settings_repositories_item_description(lockedState.reposToCheck.size),
                            ) {
                                lockedState.reposToCheck.forEach { repoToCheck ->
                                    RepoCard(
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

