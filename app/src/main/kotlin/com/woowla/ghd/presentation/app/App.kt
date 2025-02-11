package com.woowla.ghd.presentation.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.AppVersionService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.screens.HomeScreen
import com.woowla.ghd.presentation.screens.SplashScreen
import com.woowla.ghd.utils.openWebpage
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }
    val openNewAppVersionDialog = remember { mutableStateOf(false) }
    val newAppVersion = remember { mutableStateOf("") }
    val eventBus: EventBus = koinInject()
    val appSettingsService: AppSettingsService = koinInject()
    val appVersionService: AppVersionService = koinInject()

    LaunchedEffect("app-theme") {
        appSettingsService.get().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
        eventBus.subscribe("app-subscriber", this, Event.SETTINGS_UPDATED) {
            launch {
                appSettingsService.get().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
            }
        }
    }

    LaunchedEffect("check-app-update") {
        if (!BuildConfig.DEBUG) {
            // check only on prod releases to avoid checking each time the app is opened
            appVersionService.checkForNewVersion().onSuccess { response ->
                openNewAppVersionDialog.value = response.newVersion
                newAppVersion.value = response.latestVersion.toString()
            }
        }
    }

    AppTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = AppScreen.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable(AppScreen.Splash.route) {
                SplashScreen.Content(
                    onSplashFinished = {
                        navController.navigate(AppScreen.Home.route) {
                            popUpTo(AppScreen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppScreen.Home.route) {
                HomeScreen.Content()
            }
        }

        if (openNewAppVersionDialog.value) {
            newAppVersionDialog(
                newVersion = newAppVersion.value,
                onCloseRequest = { openNewAppVersionDialog.value = false },
                onDiscardClick = { openNewAppVersionDialog.value = false },
                onDownloadClick = {
                    openNewAppVersionDialog.value = false
                    openWebpage(BuildConfig.GH_GHD_LATEST_RELEASE_URL)
                },
            )
        }
    }
}

@Composable
private fun newAppVersionDialog(
    newVersion: String,
    onCloseRequest: () -> Unit,
    onDownloadClick: () -> Unit,
    onDiscardClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCloseRequest,
        title = { Text(i18n.dialog_new_app_version_title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = i18n.dialog_new_app_version_text(newVersion),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = i18n.dialog_new_app_version_current_version,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = i18n.dialog_new_app_version_latest_version(newVersion),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDownloadClick
            ) {
                Text(i18n.dialog_new_app_version_update_button)
            }
        },
        dismissButton = {
            Button(
                onClick = onDiscardClick
            ) {
                Text(i18n.dialog_new_app_version_ignore_button)
            }
        }
    )
}
