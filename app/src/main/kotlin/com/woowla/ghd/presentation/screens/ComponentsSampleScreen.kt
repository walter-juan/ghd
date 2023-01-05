package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestGitHubState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.utils.MaterialColors
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

class ComponentsSampleScreen : Screen {
    private val repoToCheck = RepoToCheck(
        id = 12,
        owner = "walter-juan",
        name = "ghd",
        pullNotificationsEnabled = false,
        releaseNotificationsEnabled = false,
        groupName = "applications",
        pullBranchRegex = null
    )
    private val release = Release(
        id = "8u8wuw93u",
        name = "Version 1.0.2 ",
        tagName = "v1.0.2",
        url = "https://github.com/walter-juan/ghd/releases/tag/v1.0.2",
        publishedAt = Clock.System.now().minus(67.days),
        authorLogin = "github-actions",
        authorUrl = null,
        authorAvatarUrl = null,
        repoToCheckId = repoToCheck.id,
        repoToCheck = repoToCheck
    )
    private val pullRequest = PullRequest(
        id = "jdf9skw4",
        number = 3,
        url = "https://github.com/walter-juan/ghd/pull/3",
        gitHubState = PullRequestGitHubState.OPEN,
        title = "v1.0.4",
        createdAt = Clock.System.now().minus(78.days),
        updatedAt = Clock.System.now().minus(2.days),
        mergedAt = null,
        draft = false,
        baseRef = null,
        headRef = null,
        authorLogin = "walter-juan",
        authorUrl = null,
        authorAvatarUrl = null,
        appSeenAt = Clock.System.now(),
        totalCommentsCount = 3,
        repoToCheckId = repoToCheck.id,
        repoToCheck = repoToCheck,
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val onBackClick: (() -> Unit) = { navigator.pop() }

        ScreenScrollable(
            topBar = {
                TopBar(
                    title = "Components",
                    navOnClick = onBackClick
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp.dp)
                    .width(AppDimens.contentWidthDp.dp)
            ) {
                Title("Typography")
                TypographySample()
                Title("SwitchText")
                SwitchTextSample()
                Title("Sections")
                SectionsSample()
                Title("Chips")
                ChipsSample()
                Title("Card list item")
                CardListItemSample()
                Title("Repo to check card")
                RepoToCheckCardSample()
                Title("Pull request card")
                PullRequestCardSample()
                Title("Release card")
                ReleaseCardSample()
            }
        }
    }

    @Composable
    private fun Title(text: String) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = MaterialColors.OnBlueGray200,
            modifier = Modifier
                .background(color = MaterialColors.BlueGray200, shape = CircleShape)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(10.dp))
    }

    @Composable
    private fun CardListItemSample() {
        val selected = remember { mutableStateOf(false) }

        Row {
            Checkbox(
                checked = selected.value,
                onCheckedChange = { selected.value = it }
            )
            Text(
                text = "Click to change the card to 'selected'/'unselected' or hover the car and click to the checkbox",
                modifier = Modifier.padding(5.dp).background(Color.White)
            )
        }

        CardListItem(
            selected = selected.value,
            onClick = {},
            overlineText = {
                Text("The overline text")
            },
            headlineText = {
                Text("This is a full card list item sample with all the things you can set")
            },
            supportingText = {
                Text(
                    text = "This is the supporting text for the card list item, in this case the is quite large and it has been set to one line as maximum. Just check the this sample code to see how to do it.",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = { paddingValues ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(25.dp)
                        .background(Color.LightGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stadium,
                        contentDescription = null,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            },
            trailingContent = { paddingValues ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(25.dp)
                        .background(Color.LightGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stadium,
                        contentDescription = null,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            },
            hoverContent = { paddingValues ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    Checkbox(
                        checked = selected.value,
                        onCheckedChange = { selected.value = it }
                    )
                    Text(
                        text = "Set whatever you want as hover content. In this case click to change the card to 'selected'/'unselected'",
                        modifier = Modifier.background(Color.White)
                    )
                }
            },
        )
    }

    @Composable
    private fun RepoToCheckCardSample() {
        RepoToCheckCard(repoToCheck = repoToCheck, onEditClick = {}, onDeleteClick = {})
    }

    @Composable
    private fun PullRequestCardSample() {
        val seen = remember { mutableStateOf(false) }
        val pr = pullRequest.copy(
            appSeenAt = if (seen.value) {
                Clock.System.now().minus(2.days)
            } else {
                null
            }
        )

        Row {
            Checkbox(
                checked = seen.value,
                onCheckedChange = { seen.value = it }
            )
            Text(
                text = "Click to change the card to 'seen'/'not seen' or hover the car and click to the button",
                modifier = Modifier.padding(5.dp).background(Color.White)
            )
        }

        PullRequestCard(
            pullRequest = pr,
            onSeenClick = {
                seen.value = !seen.value
            }
        )
    }

    @Composable
    private fun ReleaseCardSample() {
        ReleaseCard(release)
    }

    @Composable
    private fun SwitchTextSample() {
        Text(text = "A text with switch")
        val checkedState = remember { mutableStateOf(true) }
        SwitchText(
            text = "Click to change the switch",
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
    }

    @Composable
    private fun ChipsSample() {
        Text(text = "Chips without image")
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            val checkedState1 = remember { mutableStateOf(false) }
            val checkedState2 = remember { mutableStateOf(true) }
            val checkedState3 = remember { mutableStateOf(false) }
            val checkedState4 = remember { mutableStateOf(true) }
            val checkedState5 = remember { mutableStateOf(true) }
            val checkedState6 = remember { mutableStateOf(true) }
            Chip(
                text = "Chip 1",
                selected = checkedState1.value,
                onSelectedChanged = { checkedState1.value = it },
                modifier = Modifier.padding(5.dp)
            )
            Chip(
                text = "Chip 2",
                selected = checkedState3.value,
                onSelectedChanged = { checkedState3.value = it },
                modifier = Modifier.padding(5.dp)
            )
            Chip(
                text = "Chip 2",
                selected = checkedState2.value,
                onSelectedChanged = { checkedState2.value = it },
                modifier = Modifier.padding(5.dp)
            )
            Chip(
                text = "Chip 3",
                selected = checkedState4.value,
                onSelectedChanged = { checkedState4.value = it },
                modifier = Modifier.padding(5.dp)
            )
            Chip(
                text = "Chip 4",
                selected = checkedState5.value,
                onSelectedChanged = { checkedState5.value = it },
                modifier = Modifier.padding(5.dp)
            )
            Chip(
                text = "Chip 5",
                selected = checkedState6.value,
                onSelectedChanged = { checkedState6.value = it },
                modifier = Modifier.padding(5.dp)
            )
        }
        Text(text = "Chips with image")
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            val checkedState1 = remember { mutableStateOf(false) }
            val checkedState2 = remember { mutableStateOf(true) }
            val checkedState3 = remember { mutableStateOf(false) }
            val checkedState4 = remember { mutableStateOf(true) }
            val checkedState5 = remember { mutableStateOf(true) }
            val checkedState6 = remember { mutableStateOf(true) }
            ImageChip(
                text = "Chip 1",
                painter = rememberVectorPainter(Icons.Default.Home),
                selected = checkedState1.value,
                onSelectedChanged = { checkedState1.value = it },
                modifier = Modifier.padding(5.dp)
            )
            ImageChip(
                text = "Chip 2",
                painter = rememberVectorPainter(Icons.Default.Delete),
                selected = checkedState3.value,
                onSelectedChanged = { checkedState3.value = it },
                modifier = Modifier.padding(5.dp)
            )
            ImageChip(
                text = "Chip 2",
                painter = rememberVectorPainter(Icons.Default.Settings),
                selected = checkedState2.value,
                onSelectedChanged = { checkedState2.value = it },
                modifier = Modifier.padding(5.dp)
            )
            ImageChip(
                text = "Chip 3",
                painter = rememberVectorPainter(Icons.Default.Usb),
                selected = checkedState4.value,
                onSelectedChanged = { checkedState4.value = it },
                modifier = Modifier.padding(5.dp)
            )
            ImageChip(
                text = "Chip 4",
                painter = rememberVectorPainter(Icons.Default.Star),
                selected = checkedState5.value,
                onSelectedChanged = { checkedState5.value = it },
                modifier = Modifier.padding(5.dp)
            )
            ImageChip(
                text = "Chip 5",
                painter = rememberVectorPainter(Icons.Default.Stadium),
                selected = checkedState6.value,
                onSelectedChanged = { checkedState6.value = it },
                modifier = Modifier.padding(5.dp)
            )
        }
    }

    @Composable
    private fun SectionsSample() {
        val checkedState1 = remember { mutableStateOf(true) }
        val checkedState2 = remember { mutableStateOf(true) }
        SectionCategory("This is a SectionCategory") {
            SectionItem(title = "This is a SectionItem", description = "And this its description") {
                Text("Also you can add whatever you want as a content")
                Button(onClick = {}) { Text("Sample button") }
            }
            SectionItemSwitch(
                title = "This is a SectionItemSwitch",
                description = "And this its description",
                checked = checkedState1.value,
                onCheckedChange = { checkedState1.value = it }
            ) {
                Text("Also like a SectionItem you can add whatever you want as a content")
                Text("Also you have a SectionCategorySwitch for the categories")
                Button(onClick = {}) { Text("Sample button") }
            }
        }
        SectionCategorySwitch(
            text = "This is a SectionCategorySwitch",
            checked = checkedState2.value,
            onCheckedChange = { checkedState2.value = it }
        ) {
            SectionItem(title = "This is a SectionItem", description = "And this its description") {
                Text("Also you can add whatever you want as a content")
                Button(onClick = {}) { Text("Sample button") }
            }
        }
    }

    @Composable
    private fun TypographySample() {
        Text(
            text = "The main first title to start is the H4",
            style = MaterialTheme.typography.body1
        )
        Divider()
        Text(
            text = "Header H1",
            style = MaterialTheme.typography.h1
        )
        Text(
            text = "Header H2",
            style = MaterialTheme.typography.h2
        )
        Text(
            text = "Header H3",
            style = MaterialTheme.typography.h3
        )
        Text(
            text = "Header H4",
            style = MaterialTheme.typography.h4
        )
        Text(
            text = "Header H5",
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "Header H6",
            style = MaterialTheme.typography.h6
        )
        Text(
            text = "Subtitle1",
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = "Subtitle2",
            style = MaterialTheme.typography.subtitle2
        )
        Text(
            text = "Body1",
            style = MaterialTheme.typography.body1
        )
        Text(
            text = "Body2",
            style = MaterialTheme.typography.body2
        )
        Text(
            text = "Button",
            style = MaterialTheme.typography.button
        )
        Text(
            text = "Caption",
            style = MaterialTheme.typography.caption
        )
        Text(
            text = "Overline",
            style = MaterialTheme.typography.overline
        )
    }
}