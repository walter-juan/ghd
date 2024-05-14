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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.woowla.compose.tabler.OutlineBuildingBank
import com.woowla.compose.tabler.TablerIconsPainter
import com.woowla.ghd.domain.entities.CommitCheckRollupStatus
import com.woowla.ghd.domain.entities.MergeableGitHubState
import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.domain.entities.RepoToCheck
import com.woowla.ghd.domain.entities.Review
import com.woowla.ghd.domain.entities.ReviewState
import com.woowla.ghd.notifications.NotificationType
import com.woowla.ghd.presentation.app.AppColors.gitPrClosed
import com.woowla.ghd.presentation.app.AppColors.gitPrDraft
import com.woowla.ghd.presentation.app.AppColors.gitPrMerged
import com.woowla.ghd.presentation.app.AppColors.gitPrOpen
import com.woowla.ghd.presentation.app.AppColors.info
import com.woowla.ghd.presentation.app.AppColors.infoContainer
import com.woowla.ghd.presentation.app.AppColors.onInfo
import com.woowla.ghd.presentation.app.AppColors.onInfoContainer
import com.woowla.ghd.presentation.app.AppColors.onSuccess
import com.woowla.ghd.presentation.app.AppColors.onSuccessContainer
import com.woowla.ghd.presentation.app.AppColors.onWarning
import com.woowla.ghd.presentation.app.AppColors.onWarningContainer
import com.woowla.ghd.presentation.app.AppColors.success
import com.woowla.ghd.presentation.app.AppColors.successContainer
import com.woowla.ghd.presentation.app.AppColors.warning
import com.woowla.ghd.presentation.app.AppColors.warningContainer
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.components.*
import com.woowla.ghd.presentation.viewmodels.ComponentsViewModel
import com.woowla.ghd.utils.MaterialColors
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

object ComponentsSampleScreen {
    private val repoToCheck = RepoToCheck(
        id = 12,
        owner = "walter-juan",
        name = "ghd",
        groupName = "applications",
        pullBranchRegex = null,
        arePullRequestsEnabled = true,
        areReleasesEnabled = true,
    )
    private val release = Release(
        id = "8u8wuw93u",
        name = "Version 1.0.2 ",
        tagName = "v1.0.2",
        url = "https://github.com/walter-juan/ghd/releases/tag/v1.0.2",
        publishedAt = Clock.System.now().minus(12.days),
        authorLogin = "github-actions",
        authorUrl = null,
        authorAvatarUrl = "https://picsum.photos/200/300",
        repoToCheck = repoToCheck
    )
    private val pullRequest = PullRequest(
        id = "jdf9skw4",
        number = 3,
        url = "https://github.com/walter-juan/ghd/pull/3",
        state = PullRequestState.OPEN,
        title = "v1.0.4",
        createdAt = Clock.System.now().minus(78.days),
        updatedAt = Clock.System.now().minus(2.days),
        mergedAt = null,
        isDraft = false,
        baseRef = null,
        headRef = null,
        authorLogin = "walter-juan",
        authorUrl = null,
        authorAvatarUrl = "https://picsum.photos/200/300",
        appSeenAt = Clock.System.now(),
        totalCommentsCount = 3,
        repoToCheck = repoToCheck,
        mergeable = MergeableGitHubState.MERGEABLE,
        lastCommitCheckRollupStatus = CommitCheckRollupStatus.PENDING,
        reviews = listOf()
    )
    private val review = Review(
        id = "",
        url = "",
        submittedAt = Clock.System.now().minus(10.minutes),
        state = ReviewState.CHANGES_REQUESTED,
        authorLogin = "walter-juan",
        authorUrl = null,
        authorAvatarUrl = "https://picsum.photos/200/300",
        pullRequestId = "",
    )
    private val reviewsSamples = listOf(
        listOf(),
        listOf(review.copy(state = ReviewState.COMMENTED), review.copy(state = ReviewState.DISMISSED)),
        listOf(review.copy(state = ReviewState.CHANGES_REQUESTED), review.copy(state = ReviewState.APPROVED)),
        listOf(review.copy(state = ReviewState.COMMENTED), review.copy(state = ReviewState.APPROVED)),
        listOf(review.copy(state = ReviewState.PENDING), review.copy(state = ReviewState.APPROVED)),
        listOf(review.copy(state = ReviewState.COMMENTED), review.copy(state = ReviewState.CHANGES_REQUESTED)),
        listOf(review.copy(state = ReviewState.APPROVED)),
        listOf(review.copy(state = ReviewState.CHANGES_REQUESTED)),
        listOf(review.copy(state = ReviewState.COMMENTED)),
        listOf(review.copy(state = ReviewState.DISMISSED)),
        listOf(review.copy(state = ReviewState.PENDING)),
        listOf(review.copy(state = ReviewState.UNKNOWN)),
    )

    @Composable
    fun Content(
        onBackClick: (() -> Unit)? = null
    ) {
        val viewModel = viewModel { ComponentsViewModel() }

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
                Title("Test Notifications")
                TestNotifications(viewModel)
            }
        }
    }

    @Composable
    private fun TestNotifications(viewModel: ComponentsViewModel) {
        Text(text = "Try notification by type")
        Spacer(modifier = Modifier.size(5.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.sendByType(NotificationType.NONE)
                }
            ) {
                Text("None")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    viewModel.sendByType(NotificationType.INFO)
                }
            ) {
                Text("Info")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    viewModel.sendByType(NotificationType.WARNING)
                }
            ) {
                Text("Warning")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    viewModel.sendByType(NotificationType.ERROR)
                }
            ) {
                Text("Error")
            }
        }

        Text(text = "Try app notification")
        Spacer(modifier = Modifier.size(5.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.sendNewPullRequest()
                }
            ) {
                Text("New pull request")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    viewModel.sendUpdatedPullRequest()
                }
            ) {
                Text("Updated pull request")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    viewModel.sendNewRelease()
                }
            ) {
                Text("New release")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    viewModel.sendUpdatedRelease()
                }
            ) {
                Text("Updated release")
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
                        painter = TablerIconsPainter.OutlineBuildingBank,
                        contentDescription = null,
                        tint = Color.Magenta,
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
                        painter = TablerIconsPainter.OutlineBuildingBank,
                        contentDescription = null,
                        tint = Color.Magenta,
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
        val mergeable = remember { mutableStateOf(0) }
        val lastCommitCheckRollupStatus = remember { mutableStateOf(0) }
        val reviewsSample = remember { mutableStateOf(0) }

        val pr = pullRequest.copy(
            appSeenAt = if (seen.value) {
                Clock.System.now().minus(2.days)
            } else {
                null
            },
            mergeable = MergeableGitHubState.values()[mergeable.value],
            lastCommitCheckRollupStatus = CommitCheckRollupStatus.values()[lastCommitCheckRollupStatus.value],
            reviews = reviewsSamples[reviewsSample.value]
        )

        SwitchText(
            text = "mark as 'seen'/'not seen'",
            checked = seen.value,
            onCheckedChange = { seen.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val next = mergeable.value + 1
                    if (next >= MergeableGitHubState.values().size) {
                        mergeable.value = 0
                    } else {
                        mergeable.value = next
                    }
                }
            ) {
                Text("Change mergeable state")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    val next = lastCommitCheckRollupStatus.value + 1
                    if (next >= CommitCheckRollupStatus.values().size) {
                        lastCommitCheckRollupStatus.value = 0
                    } else {
                        lastCommitCheckRollupStatus.value = next
                    }
                }
            ) {
                Text("Change commit check")
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    val next = reviewsSample.value + 1
                    if (next >= reviewsSamples.size) {
                        reviewsSample.value = 0
                    } else {
                        reviewsSample.value = next
                    }
                }
            ) {
                Text("Change reviews")
            }
        }

        Spacer(modifier = Modifier.size(5.dp))

        PullRequestCard(
            pullRequest = pr,
            onSeenClick = {
                seen.value = !seen.value
            }
        )
    }

    @Composable
    private fun ReleaseCardSample() {
        ReleaseCard(release = release)
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
            text = "Material colors",
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

        Text(
            text = "Custom colors",
            style = MaterialTheme.typography.bodyLarge
        )
        Divider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                ColorBox("Git PR draft", MaterialTheme.colorScheme.gitPrDraft, Color.White)
                ColorBox("Git PR open", MaterialTheme.colorScheme.gitPrOpen, Color.White)
                ColorBox("Git PR merged", MaterialTheme.colorScheme.gitPrMerged, Color.White)
                ColorBox("Git PR closed", MaterialTheme.colorScheme.gitPrClosed, Color.White)
            }
            Row {
                ColorBox("Info", MaterialTheme.colorScheme.info, MaterialTheme.colorScheme.onInfo)
                ColorBox("On Info", MaterialTheme.colorScheme.onInfo, MaterialTheme.colorScheme.info)
                ColorBox("Info Container", MaterialTheme.colorScheme.infoContainer, MaterialTheme.colorScheme.onInfoContainer)
                ColorBox("On Info Container", MaterialTheme.colorScheme.onInfoContainer, MaterialTheme.colorScheme.infoContainer)
            }
            Row {
                ColorBox("Success", MaterialTheme.colorScheme.success, MaterialTheme.colorScheme.onSuccess)
                ColorBox("On Success", MaterialTheme.colorScheme.onSuccess, MaterialTheme.colorScheme.success)
                ColorBox("Success Container", MaterialTheme.colorScheme.successContainer, MaterialTheme.colorScheme.onSuccessContainer)
                ColorBox("On Success Container", MaterialTheme.colorScheme.onSuccessContainer, MaterialTheme.colorScheme.successContainer)
            }
            Row {
                ColorBox("Warning", MaterialTheme.colorScheme.warning, MaterialTheme.colorScheme.onWarning)
                ColorBox("On Warning", MaterialTheme.colorScheme.onWarning, MaterialTheme.colorScheme.warning)
                ColorBox("Warning Container", MaterialTheme.colorScheme.warningContainer, MaterialTheme.colorScheme.onWarningContainer)
                ColorBox("On Warning Container", MaterialTheme.colorScheme.onWarningContainer, MaterialTheme.colorScheme.warningContainer)
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