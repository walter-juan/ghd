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
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.presentation.DiUi
import com.woowla.ghd.presentation.screens.AboutLibraries
import com.woowla.ghd.presentation.screens.AboutScreen
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
                            navController.navigate(items[selectedItem].route) {
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
                startDestination = items[selectedItem].route,
                enterTransition = { EnterTransition.Companion.None },
                exitTransition = { ExitTransition.Companion.None },
            ) {
                composable(AppScreen.PullRequestList.route) {
                    PullRequestsScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult.route.replace("{id}", syncResult.id.toString()),
                            )
                        }
                    )
                }
                composable(AppScreen.ReleaseList.route) {
                    ReleasesScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult.route.replace("{id}", syncResult.id.toString()),
                            )
                        }
                    )
                }
                composable(AppScreen.RepoToCheckList.route) {
                    ReposToCheckScreen.Content(
                        viewModel = koinViewModel(),
                        onEditRepoClick = { repoToCheck ->
                            navController.navigate(
                                route = AppScreen.RepoToCheckEdit.route.replace("{id}", repoToCheck.id.toString()),
                            )
                        },
                        onAddNewRepoClick = {
                            navController.navigate(
                                route = AppScreen.RepoToCheckNew.route
                            )
                        },
                        onBulkClick = {
                            navController.navigate(AppScreen.RepoToCheckBulk.route)
                        },
                    )
                }
                composable(AppScreen.Settings.route) {
                    SettingsScreen.Content(
                        viewModel = koinViewModel(),
                        onSyncResultsClicked = { navController.navigate(AppScreen.SyncResultList.route) }
                    )
                }
                composable(AppScreen.Notifications.route) {
                    NotificationsScreen.Content(viewModel = koinViewModel())
                }
                composable(AppScreen.SyncResultList.route) {
                    SyncResultsScreen.Content(
                        viewModel = koinViewModel(),
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult.route.replace("{id}", syncResult.id.toString()),
                            )
                        }
                    )
                }
                composable(
                    AppScreen.SyncResult.route,
                    arguments = listOf(navArgument("id") { type = NavType.Companion.LongType })
                ) { backStackEntry ->
                    val syncResultId = backStackEntry.arguments?.getLong("id")!!
                    SyncResultEntriesScreen.Content(
                        viewModel = koinViewModel { parametersOf(syncResultId) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppScreen.RepoToCheckNew.route) { entry ->
                    val viewModel: RepoToCheckEditViewModel = koinViewModel { parametersOf(null) }
                    val repo = entry.savedStateHandle.getStateFlow("repository", "").collectAsState()

                    if (repo.value.isNotEmpty()) {
                        viewModel.dispatch(RepoToCheckEditStateMachine.Act.UpdateRepository(repo.value))
                    }

                    RepoToCheckEditScreen.Content(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(
                    AppScreen.RepoToCheckEdit.route,
                    arguments = listOf(navArgument("id") { type = NavType.Companion.LongType })
                ) { backStackEntry ->
                    val repoToCheckId = backStackEntry.arguments?.getLong("id")!!
                    RepoToCheckEditScreen.Content(
                        viewModel = koinViewModel { parametersOf(repoToCheckId) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppScreen.RepoToCheckBulk.route) {
                    RepoToCheckBulkScreen.Content(
                        viewModel = koinViewModel(),
                        onBackClick = { navController.popBackStack() },
                    )
                }
                composable(AppScreen.About.route) {
                    AboutScreen.Content(
                        viewModel = koinViewModel(),
                        appVersion = BuildConfig.APP_VERSION,
                        onAboutLicensesClick = { navController.navigate(AppScreen.AboutLibraries.route) },
                    )
                }
                composable(AppScreen.AboutLibraries.route) {
                    AboutLibraries.Content(
                        jsonFileName = koinInject(qualifier = named(DiUi.Name.ABOUT_LIBRARIES_JSON_FILE_NAME)),
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}