package com.woowla.ghd.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.CircleX
import com.woowla.compose.icon.collections.tabler.tabler.outline.Search
import com.woowla.compose.icon.collections.tabler.tabler.outline.TextRecognition
import com.woowla.compose.icon.collections.tabler.tabler.outline.User
import com.woowla.ghd.domain.entities.Repository
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.ErrorComponent
import com.woowla.ghd.presentation.components.Header
import com.woowla.ghd.presentation.components.LoadingComponent
import com.woowla.ghd.presentation.components.RepositoryCard
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.SearchRepositoryStateMachine
import com.woowla.ghd.presentation.viewmodels.SearchRepositoryViewModel

object SearchRepositoryScreen {
    @Composable
    fun Content(
        viewModel: SearchRepositoryViewModel,
        onRepositorySelected: (Repository) -> Unit,
        onBackClick: (() -> Unit)? = null,
    ) {
        ScreenScrollable(
            topBar = {
                TopBar(
                    title = "Search repository",
                    navOnClick = onBackClick
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth()
            ) {
                val state = viewModel.state.collectAsState().value
                SearchHeader(
                    owner = state?.owner ?: "",
                    searchQuery = state?.text ?: "",
                    onOwnerChanged = { viewModel.dispatch(SearchRepositoryStateMachine.Act.UpdateOwner(it)) },
                    onSearchQueryChanged = { viewModel.dispatch(SearchRepositoryStateMachine.Act.UpdateText(it)) },
                    onSearch = { viewModel.dispatch(SearchRepositoryStateMachine.Act.Search) },
                )
                when(state) {
                    null, is SearchRepositoryStateMachine.St.Error -> {
                        ErrorComponent()
                    }
                    is SearchRepositoryStateMachine.St.Loading -> {
                        LoadingComponent()
                    }
                    is SearchRepositoryStateMachine.St.Success -> {
                        RepositoriesList(state.repositories, onRepositorySelected)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun RepositoriesList(
        repositoryList: List<Repository>,
        onRepositorySelected: (Repository) -> Unit,
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = AppDimens.cardHorizontalSpaceBetween, alignment = Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AppDimens.cardVerticalSpaceBetween),
            maxItemsInEachRow = 2
        ) {
            repositoryList.forEach { repository ->
                RepositoryCard(
                    repository = repository,
                    onClick = { onRepositorySelected.invoke(repository) },
                    modifier = Modifier.sizeIn(maxWidth = AppDimens.cardMaxWidth).fillMaxWidth(),
                )
            }
        }
    }

    @Composable
    private fun SearchHeader(
        owner: String,
        searchQuery: String,
        onOwnerChanged: (String) -> Unit,
        onSearchQueryChanged: (String) -> Unit,
        onSearch: () -> Unit,
    ) {
        Header(
            bottomRowContent = {
                Row {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        leadingIcon = {
                            CleanIcon(icon = Tabler.Outline.TextRecognition, text = searchQuery, onTextChanged = onSearchQueryChanged)
                        },
                        supportingText = { Text("Search by text") },
                        singleLine = true,
                        modifier = Modifier.weight(1F)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = owner,
                        onValueChange = onOwnerChanged,
                        leadingIcon = {
                            CleanIcon(icon = Tabler.Outline.User, text = owner, onTextChanged = onOwnerChanged)
                        },
                        supportingText = { Text("Owner (has to be exact)") },
                        singleLine = true,
                        modifier = Modifier.weight(1F)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSearch,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Tabler.Outline.Search, contentDescription = null, modifier = Modifier.size(25.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Search")
                }
            },
        )
    }

    @Composable
    private fun CleanIcon(
        icon: ImageVector,
        text: String,
        onTextChanged: (String) -> Unit,
    ) {
        AnimatedContent(
            targetState = text.isBlank(),
            transitionSpec = { fadeIn().togetherWith(fadeOut()) }
        ) {
            if (text.isBlank()) {
                Icon(
                    icon,
                    contentDescription = null,
                )
            } else {
                IconButton(onClick = { onTextChanged.invoke("") }) {
                    Icon(imageVector = Tabler.Outline.CircleX, contentDescription = null, modifier = Modifier.size(25.dp))
                }
            }
        }
    }
}