package com.woowla.ghd.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.presentation.DiUi
import com.woowla.ghd.presentation.screens.AboutLibraries
import com.woowla.ghd.presentation.screens.AboutScreen
import com.woowla.ghd.presentation.screens.DeploymentsScreen
import com.woowla.ghd.presentation.screens.NotificationsScreen
import com.woowla.ghd.presentation.screens.PullRequestsScreen
import com.woowla.ghd.presentation.screens.ReleasesScreen
import com.woowla.ghd.presentation.screens.RepoToCheckBulkScreen
import com.woowla.ghd.presentation.screens.RepoToCheckEditScreen
import com.woowla.ghd.presentation.screens.ReposToCheckScreen
import com.woowla.ghd.presentation.screens.SettingsScreen
import com.woowla.ghd.presentation.screens.SyncResultEntriesScreen
import com.woowla.ghd.presentation.screens.SyncResultsScreen
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditStateMachine
import com.woowla.ghd.presentation.viewmodels.RepoToCheckEditViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

object HomeScreen {
    @Composable
    fun Content() {
        val navController = rememberNavController()
        val items = listOf(
            AppScreen.PullRequestList,
            AppScreen.ReleaseList,
            AppScreen.DeploymentList,
            AppScreen.RepoToCheckList,
            AppScreen.Notifications,
            AppScreen.Settings,
            AppScreen.About,
        )
        var selectedItem by remember { mutableStateOf(0) }
        Row {
            NavigationRail {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.Companion.size(25.dp)
                            )
                        },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(items[selectedItem]) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                navController.graph.startDestinationRoute?.let {
                                    popUpTo(it) {
                                        saveState = true
                                    }
                                }
                                // Avoid multiple copies of the same destination when
                                // re-selecting the same item
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        },
                        alwaysShowLabel = false,
                    )
                }
            }
            NavHost(
                navController = navController,
                startDestination = items[selectedItem],
                enterTransition = { EnterTransition.Companion.None },
                exitTransition = { ExitTransition.Companion.None },
            ) {
                composable<AppScreen.PullRequestList> {
                    PullRequestsScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult(id = syncResult.id),
                            )
                        }
                    )
                }
                composable<AppScreen.ReleaseList> {
                    ReleasesScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult(id = syncResult.id),
                            )
                        }
                    )
                }
                composable<AppScreen.DeploymentList> {
                    DeploymentsScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult(id = syncResult.id),
                            )
                        }
                    )
                }
                composable<AppScreen.RepoToCheckList> {
                    ReposToCheckScreen.Content(
                        viewModel = koinViewModel(),
                        onEditRepoClick = { repoToCheck ->
                            navController.navigate(
                                route = AppScreen.RepoToCheckEdit(id = repoToCheck.id),
                            )
                        },
                        onAddNewRepoClick = {
                            navController.navigate(
                                route = AppScreen.RepoToCheckNew
                            )
                        },
                        onBulkClick = {
                            navController.navigate(AppScreen.RepoToCheckBulk)
                        },
                    )
                }
                composable<AppScreen.Settings> {
                    SettingsScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultsClicked = { navController.navigate(AppScreen.SyncResultList) }
                    )
                }
                composable<AppScreen.Notifications> {
                    NotificationsScreen.Content(viewModel = koinViewModel())
                }
                composable<AppScreen.SyncResultList> {
                    SyncResultsScreen.Content(
                        viewModel = koinViewModel(),
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult(id = syncResult.id),
                            )
                        }
                    )
                }
                composable<AppScreen.SyncResult> { backStackEntry ->
                    val syncResultId = backStackEntry.toRoute<AppScreen.SyncResult>().id
                    SyncResultEntriesScreen.Content(
                        viewModel = koinViewModel { parametersOf(syncResultId) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable<AppScreen.RepoToCheckNew> { backStackEntry ->
                    val viewModel: RepoToCheckEditViewModel = koinViewModel { parametersOf(null) }
                    val repo = backStackEntry.savedStateHandle.getStateFlow("repository", "").collectAsState()

                    if (repo.value.isNotEmpty()) {
                        viewModel.dispatch(RepoToCheckEditStateMachine.Act.UpdateRepository(repo.value))
                    }

                    RepoToCheckEditScreen.Content(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable<AppScreen.RepoToCheckEdit> { backStackEntry ->
                    val repoToCheckId  = backStackEntry.toRoute<AppScreen.RepoToCheckEdit>().id
                    RepoToCheckEditScreen.Content(
                        viewModel = koinViewModel { parametersOf(repoToCheckId) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable<AppScreen.RepoToCheckBulk> {
                    RepoToCheckBulkScreen.Content(
                        viewModel = koinViewModel(),
                        onBackClick = { navController.popBackStack() },
                    )
                }
                composable<AppScreen.About> {
                    AboutScreen.Content(
                        viewModel = koinViewModel(),
                        appVersion = BuildConfig.APP_VERSION,
                        onAboutLicensesClick = { navController.navigate(AppScreen.AboutLibraries) },
                    )
                }
                composable<AppScreen.AboutLibraries> {
                    AboutLibraries.Content(
                        jsonFileName = koinInject(qualifier = named(DiUi.Name.ABOUT_LIBRARIES_JSON_FILE_NAME)),
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}