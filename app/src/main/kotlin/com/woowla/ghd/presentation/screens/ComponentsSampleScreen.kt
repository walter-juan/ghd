package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.compose.remixicon.BuildingsBankFill
import com.woowla.compose.remixicon.RemixiconPainter
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
                    .padding(AppDimens.contentPaddingAllDp)
                    .width(AppDimens.contentWidthDp)
            ) {
                Title("Colors")
                ColorsSample()
                Title("Typography")
                TypographySample()
                Title("SwitchText")
                SwitchTextSample()
                Title("Sections")
                SectionsSample()
                Title("OutlinedSelectField")
                OutlinedSelectFieldSample()
                Title("LabelledCheckBox")
                LabelledCheckBoxSample()
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
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialColors.OnBlueGray200,
            modifier = Modifier
                .background(color = MaterialColors.BlueGray200, shape = CircleShape)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(10.dp))
    }

    @Composable
    private fun LabelledCheckBoxSample() {
        val checked = remember { mutableStateOf(false) }
        LabelledCheckBox(
            checked = checked.value,
            onCheckedChange = { checked.value = it },
            label = "Click to this label to modify the checkbox"
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun OutlinedSelectFieldSample() {
        val values = listOf(1 to "One", 2 to "Two", 3 to "Three", 4 to "Four", 5 to "Five")
        val selected = values.random().first

        Text(text = "With one option selected by default")
        OutlinedSelectField(
            selected = selected,
            values = values,
        ) { _, _ -> }

        Text(text = "Without an option selected by default")
        OutlinedSelectField(
            values = values,
            emptyText = "(no option selected)"
        ) { _, _ -> }
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
                        painter = RemixiconPainter.BuildingsBankFill,
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
                        painter = RemixiconPainter.BuildingsBankFill,
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
            text = "The main first title to start is the Headline Medium (old h4)",
            style = MaterialTheme.typography.bodyLarge
        )
        Divider()
        Text(
            text = "Display Large",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Display Medium",
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Display Small",
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = "Headline Large",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Headline Medium",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Headline Small",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Title Large",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Title Medium",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Title Small",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "Body Large",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Body Medium",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Body Small",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Label large",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "Label Medium",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "Label Small",
            style = MaterialTheme.typography.labelSmall
        )
    }

    @Composable
    fun ColorsSample() {
        Text(
            text = "Color samples",
            style = MaterialTheme.typography.bodyLarge
        )
        Divider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                ColorBox("Primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                ColorBox("On Primary", MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.primary)
                ColorBox("Primary Container", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                ColorBox("On Primary Container", MaterialTheme.colorScheme.onPrimaryContainer, MaterialTheme.colorScheme.primaryContainer)
            }
            Row {
                ColorBox("Secondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
                ColorBox("On Secondary", MaterialTheme.colorScheme.onSecondary, MaterialTheme.colorScheme.secondary)
                ColorBox("Secondary Container", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
                ColorBox("On Secondary Container", MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondaryContainer)
            }
            Row {
                ColorBox("Tertiary", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
                ColorBox("On Tertiary", MaterialTheme.colorScheme.onTertiary, MaterialTheme.colorScheme.tertiary)
                ColorBox("Tertiary Container", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
                ColorBox("On Tertiary Container", MaterialTheme.colorScheme.onTertiaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
            }
            Row {
                ColorBox("Error", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
                ColorBox("On Error", MaterialTheme.colorScheme.onError, MaterialTheme.colorScheme.error)
                ColorBox("Error Container", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
                ColorBox("On Error Container", MaterialTheme.colorScheme.onErrorContainer, MaterialTheme.colorScheme.errorContainer)
            }
            Row {
                ColorBox("Surface", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
                ColorBox("On Surface", MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surface)
                ColorBox("Surface Variant", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                ColorBox("On Surface Variant", MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
            }
            Row {
                ColorBox("Background", MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground)
                ColorBox("On Background", MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.background)
                ColorBox("Outline", MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outlineVariant)
                ColorBox("Outline variant", MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.outline)
            }
            Row {
                ColorBox("Inverse Surface", MaterialTheme.colorScheme.inverseSurface, MaterialTheme.colorScheme.inverseOnSurface)
                ColorBox("On Inverse Surface", MaterialTheme.colorScheme.inverseOnSurface, MaterialTheme.colorScheme.inverseSurface)
                ColorBox("Inverse Primary", MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.primary)
                ColorBox("Surface Tint", MaterialTheme.colorScheme.surfaceTint, MaterialTheme.colorScheme.onSurface)
            }
            Row {
                ColorBox("Scrim", MaterialTheme.colorScheme.scrim, MaterialTheme.colorScheme.primary)
            }
        }
    }

    @Composable
    fun ColorBox(name: String, color: Color, onColor: Color) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .padding(4.dp)
                .size(width = 125.dp, height = 50.dp)
                .background(color)
        ) {
            Text(
                name,
                style = MaterialTheme.typography.bodySmall,
                color = onColor,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}