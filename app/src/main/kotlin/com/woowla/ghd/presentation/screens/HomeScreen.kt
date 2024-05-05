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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.woowla.ghd.presentation.app.AppScreen

object HomeScreen {
    @Composable
    fun Content() {
        val navController = rememberNavController()
        val items = listOf(
            AppScreen.PullRequestList,
            AppScreen.ReleaseList,
            AppScreen.RepoToCheckList,
            AppScreen.Settings,
            AppScreen.About,
        )
        var selectedItem by remember { mutableStateOf(0) }
        Row {
            NavigationRail {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(
                        icon = { Icon(painter = painterResource(item.icon), contentDescription = item.title, modifier = Modifier.size(25.dp)) },
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
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult.route.replace("{id}", syncResult.id.toString()),
                            )
                        }
                    )
                }
                composable(AppScreen.ReleaseList.route) {
                    ReleasesScreen.Content(
                        onSyncResultEntriesClick = { syncResult ->
                            navController.navigate(
                                route = AppScreen.SyncResult.route.replace("{id}", syncResult.id.toString()),
                            )
                        }
                    )
                }
                composable(AppScreen.RepoToCheckList.route) {
                    RepoToCheckScreen.Content(
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
                        onBulkExampleClick = {
                            navController.navigate(AppScreen.RepoToCheckBulkSample.route)
                        },
                    )
                }
                composable(AppScreen.Settings.route) {
                    SettingsScreen.Content(
                        onSyncResultsClicked = { navController.navigate(AppScreen.SyncResultList.route) }
                    )
                }
                composable(AppScreen.SyncResultList.route) {
                    SyncResultsScreen.Content(
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
                    SyncResultEntriesScreen.Content(
                        syncResultId = backStackEntry.arguments?.getLong("id")!!,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppScreen.RepoToCheckNew.route) {
                    RepoToCheckEditScreen.Content(
                        repoToCheckId = null,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(
                    AppScreen.RepoToCheckEdit.route,
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    RepoToCheckEditScreen.Content(
                        repoToCheckId = backStackEntry.arguments?.getLong("id")!!,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppScreen.RepoToCheckBulkSample.route) {
                    RepoToCheckBulkSampleScreen.Content(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(AppScreen.About.route) {
                    AboutScreen.Content(
                        onComponentsSampleScreenClick = {
                            navController.navigate(AppScreen.ComponentsSample.route)
                        }
                    )
                }
                composable(AppScreen.ComponentsSample.route) {
                    ComponentsSampleScreen.Content(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}