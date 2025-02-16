package com.woowla.ghd.presentation.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.woowla.ghd.presentation.app.AppScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

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
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title, modifier = Modifier.size(25.dp)) },
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
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
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
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val syncResultId = backStackEntry.arguments?.getLong("id")!!
                    SyncResultEntriesScreen.Content(
                        viewModel = koinViewModel { parametersOf(syncResultId) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppScreen.RepoToCheckNew.route) {
                    RepoToCheckEditScreen.Content(
                        viewModel = koinViewModel { parametersOf(null) },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(
                    AppScreen.RepoToCheckEdit.route,
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
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
                    )
                }
            }
        }
    }
}