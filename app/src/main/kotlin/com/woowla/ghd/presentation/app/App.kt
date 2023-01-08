package com.woowla.ghd.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import cafe.adriel.voyager.navigator.Navigator
import com.woowla.ghd.BuildConfig
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.services.AppVersionService
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.screens.SplashScreen
import com.woowla.ghd.utils.openWebpage
import kotlinx.coroutines.launch

@Composable
fun App() {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }
    val openNewAppVersionDialog = remember { mutableStateOf(false)  }
    val newAppVersion = remember { mutableStateOf("")  }

    LaunchedEffect("app-theme") {
        AppSettingsService().get().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
        EventBus.subscribe("app-subscriber", this, Event.SETTINGS_UPDATED) {
            launch {
                AppSettingsService().get().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
            }
        }
    }

    LaunchedEffect("check-app-update") {
        if (!BuildConfig.DEBUG) {
            // check only on prod releases to avoid checking each time the app is opened
            AppVersionService().checkForNewVersion().onSuccess { response ->
                openNewAppVersionDialog.value = response.newVersion
                newAppVersion.value = response.latestVersion.toString()
            }
        }
    }

    AppTheme(darkTheme = darkTheme) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Navigator(SplashScreen())
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
private fun newAppVersionDialog(newVersion: String, onCloseRequest: () -> Unit, onDownloadClick: () -> Unit, onDiscardClick: () -> Unit, ) {
    Dialog(
        title = i18n.dialog_new_app_version_title,
        onCloseRequest = onCloseRequest,
        state = rememberDialogState(position = WindowPosition(Alignment.Center)),
    ) {
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
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = i18n.dialog_new_app_version_latest_version(newVersion),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                OutlinedButton(
                    onClick = onDiscardClick
                ) {
                    Text(i18n.dialog_new_app_version_ignore_button)
                }
                Button(
                    onClick = onDownloadClick
                ) {
                    Text(i18n.dialog_new_app_version_update_button)
                }
            }
        }
    }
}
