package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.navigation.NavController
import com.woowla.ghd.domain.services.AppSettingsService
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.AppScreen
import com.woowla.ghd.presentation.app.AppTheme
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.ScreenScrollable
import com.woowla.ghd.presentation.viewmodels.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

object LoginScreen {
    @Composable
    fun Content(
        navController: NavController,
        onAboutClick: () -> Unit,
        viewModel : LoginViewModel = koinViewModel(),
    ) {
        val systemDarkTheme = isSystemInDarkTheme()
        var darkTheme by remember { mutableStateOf(systemDarkTheme) }
        val appSettingsService: AppSettingsService = koinInject()
        val synchronizer: Synchronizer = koinInject()

        LaunchedEffect("login-app-theme") {
            appSettingsService.get().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
            EventBus.subscribe("app-subscriber", this, Event.SETTINGS_UPDATED) {
                launch {
                    appSettingsService.get().onSuccess { darkTheme = it.darkTheme ?: systemDarkTheme }
                }
            }
        }

        ScreenScrollable {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(AppDimens.screenPadding)
                    .fillMaxWidth(fraction = 0.75F)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                loginDatabaseAlreadyExists(
                    darkTheme = darkTheme,
                    onAboutClick = onAboutClick,
                    onContinue = {
                        synchronizer.initialize()
                        EventBus.publish(Event.APP_UNLOCKED)
                        navController.navigate(AppScreen.Home.route) {
                            popUpTo(AppScreen.Login.route) { inclusive = true }
                        }
                    },
                    onResetDatabase = {
                        viewModel.resetDatabase()
                    },
                )
            }
        }
    }

    @Composable
    private fun loginDatabaseAlreadyExists(darkTheme: Boolean, onAboutClick: () -> Unit, onContinue: () -> Unit, onResetDatabase: () -> Unit) {
        val openConfirmRemoveDatabaseDialog = remember { mutableStateOf(false)  }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(i18n.screen_login_unlock_button)
                }
                Spacer(modifier = Modifier.padding(10.dp))
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onAboutClick,
                ) {
                    Text(i18n.screen_login_about_app_button)
                }
                Button(
                    onClick = {
                        openConfirmRemoveDatabaseDialog.value = true
                    },
                ) {
                    Text(i18n.screen_login_fresh_start)
                }
            }
        }

        if (openConfirmRemoveDatabaseDialog.value) {
            deleteDatabaseConfirmationDialog(
                darkTheme = darkTheme,
                onCloseRequest = {
                    openConfirmRemoveDatabaseDialog.value = false
                },
                onConfirmClick = {
                    openConfirmRemoveDatabaseDialog.value = false
                    onResetDatabase.invoke()
                },
                onDiscardClick = {
                    openConfirmRemoveDatabaseDialog.value = false
                }
            )
        }
    }

    @Composable
    private fun deleteDatabaseConfirmationDialog(darkTheme: Boolean, onCloseRequest: () -> Unit, onConfirmClick: () -> Unit, onDiscardClick: () -> Unit, ) {
        DialogWindow(
            title = i18n.screen_login_fresh_start_confirmation_dialog_title,
            onCloseRequest = onCloseRequest,
            state = rememberDialogState(position = WindowPosition(Alignment.Center)),
        ) {
            AppTheme(darkTheme = darkTheme) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = i18n.screen_login_fresh_start_confirmation_dialog_text,
                        textAlign = TextAlign.Center,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        OutlinedButton(
                            onClick = onConfirmClick
                        ) {
                            Text(i18n.screen_login_fresh_start_confirmation_dialog_yes_button)
                        }
                        Button(
                            onClick = onDiscardClick
                        ) {
                            Text(i18n.screen_login_fresh_start_confirmation_dialog_no_button)
                        }
                    }
                }
            }
        }
    }
}