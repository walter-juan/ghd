package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.extensions.toHRString
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.ReleaseCard
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.components.TopBar
import com.woowla.ghd.presentation.viewmodels.ReleasesViewModel

class ReleasesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { ReleasesViewModel() }
        val releasesState by viewModel.state.collectAsState()
        val lockedState = releasesState
        val statusBarText = when(lockedState) {
            is ReleasesViewModel.State.Error -> i18n.status_bar_error
            is ReleasesViewModel.State.Loading -> i18n.status_bar_loading
            is ReleasesViewModel.State.Success -> i18n.status_bar_synchronized_at(lockedState.synchronizedAt)
        }

        val lazyListState = rememberLazyListState()
        Screen(
            topBar = { TopBar(title = i18n.top_bar_title_releases) },
            statusBarText = statusBarText,
            lazyListState = lazyListState,
        ) {
            when(lockedState) {
                is ReleasesViewModel.State.Error -> item { Text(i18n.generic_error) }
                is ReleasesViewModel.State.Loading -> {
                    lockedState.releases.forEach { (group, uiReleases) ->
                        GroupWithReleases(group, uiReleases)
                    }
                }
                is ReleasesViewModel.State.Success -> {
                    lockedState.releases.forEach { (group, uiReleases) ->
                        GroupWithReleases(group, uiReleases)
                    }
                }
            }
        }
    }

    private fun LazyListScope.GroupWithReleases(group: String?, releases: List<Release>) {
        if (group != null) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group,
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
            }
        }
        items(releases) { release ->
            ReleaseCard(
                text = "${release.repoToCheck.owner}/${release.repoToCheck.name}",
                overlineText = release.publishedAt.toHRString(),
                secondaryText = "${release.name} (${release.tagName}) - ${release.authorLogin}",
                linkUrl = release.url,
                imageUrl = release.authorAvatarUrl,
            )
        }
        item {
            Divider(modifier = Modifier.padding(vertical = 10.dp))
        }
    }
}