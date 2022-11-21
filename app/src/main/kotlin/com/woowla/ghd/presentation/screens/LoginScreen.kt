package com.woowla.ghd.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.OutlinedTextFieldValidation
import com.woowla.ghd.presentation.components.Screen
import com.woowla.ghd.presentation.decorators.ErrorMessageFactory
import com.woowla.ghd.presentation.viewmodels.LoginViewModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { LoginViewModel(navigator) }
        val loginState by viewModel.state.collectAsState()

        val lockedLoginState = loginState
        Screen {
            item {
                when(lockedLoginState) {
                    is LoginViewModel.State.Error -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(i18n.generic_error)
                        }
                    }
                    LoginViewModel.State.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(i18n.generic_loading)
                        }
                    }
                    is LoginViewModel.State.Success.SuccessWithDatabase -> {
                        loginDatabaseAlreadyExists(
                            navigator = navigator,
                            error = lockedLoginState.error,
                            onUnlockDatabase = { pwd ->
                                viewModel.unlockDatabase(pwd = pwd)
                            },
                            onDeleteDatabase = {
                                viewModel.deleteDatabase()
                            },
                        )
                    }
                    is LoginViewModel.State.Success.SuccessWithoutDatabase -> {
                        loginDatabaseNoExists(
                            navigator = navigator,
                            error = lockedLoginState.error,
                            onCreateNewDatabase = { pwd ->
                                viewModel.createDatabase(pwd = pwd)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun loginDatabaseNoExists(navigator: Navigator, error: Throwable? = null, onCreateNewDatabase: (pwd: String) -> Unit) {
        var passwordVisible by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            OutlinedTextFieldValidation(
                value = password,
                onValueChange = {
                    password = it
                },
                label = { Text(i18n.screen_login_password_field_label) },
                isError = error != null,
                error = error?.let(ErrorMessageFactory::create),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }
                    val description = if (passwordVisible) {
                        i18n.screen_login_password_field_field_hide
                    } else {
                        i18n.screen_login_password_field_field_show
                    }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onCreateNewDatabase.invoke(password) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(i18n.screen_login_create_new_database_button)
            }

            OutlinedButton(
                onClick = {
                    navigator.push(AboutScreen(onBackClick = { navigator.pop() }))
                },
            ) {
                Text(i18n.screen_login_about_app_button)
            }

            Spacer(modifier = Modifier.padding(20.dp))

            Text(i18n.screen_login_password_database_info)
        }
    }

    @Composable
    private fun loginDatabaseAlreadyExists(navigator: Navigator, error: Throwable? = null, onUnlockDatabase: (pwd: String) -> Unit, onDeleteDatabase: () -> Unit) {
        var passwordVisible by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        val openConfirmRemoveDatabaseDialog = remember { mutableStateOf(false)  }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            OutlinedTextFieldValidation(
                value = password,
                onValueChange = {
                    password = it
                },
                label = { Text(i18n.screen_login_password_field_label) },
                isError = error != null,
                error = error?.let(ErrorMessageFactory::create),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }
                    val description = if (passwordVisible) {
                        i18n.screen_login_password_field_field_hide
                    } else {
                        i18n.screen_login_password_field_field_show
                    }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onUnlockDatabase.invoke(password) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(i18n.screen_login_unlock_button)
            }

            OutlinedButton(
                onClick = {
                    navigator.push(AboutScreen(onBackClick = { navigator.pop() }))
                },
            ) {
                Text(i18n.screen_login_about_app_button)
            }

            Spacer(modifier = Modifier.padding(20.dp))

            Text(i18n.screen_login_remove_database_info)
            Button(
                onClick = {
                    openConfirmRemoveDatabaseDialog.value = true
                },
            ) {
                Text(i18n.screen_login_remove_database)
            }

            if (openConfirmRemoveDatabaseDialog.value) {
                deleteDatabaseConfirmationDialog(
                    onCloseRequest = {
                        openConfirmRemoveDatabaseDialog.value = false
                    },
                    onConfirmClick = {
                        openConfirmRemoveDatabaseDialog.value = false
                        onDeleteDatabase.invoke()
                    },
                    onDiscardClick = {
                        openConfirmRemoveDatabaseDialog.value = false
                    }
                )
            }
        }
    }

    @Composable
    private fun deleteDatabaseConfirmationDialog(onCloseRequest: () -> Unit, onConfirmClick: () -> Unit, onDiscardClick: () -> Unit, ) {
        Dialog(
            onCloseRequest = onCloseRequest,
            state = rememberDialogState(position = WindowPosition(Alignment.Center)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = i18n.screen_login_remove_database_confirmation_dialog_text,
                    textAlign = TextAlign.Center,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    OutlinedButton(
                        onClick = onConfirmClick
                    ) {
                        Text(i18n.screen_login_remove_database_confirmation_dialog_yes_button)
                    }
                    Button(
                        onClick = onDiscardClick
                    ) {
                        Text(i18n.screen_login_remove_database_confirmation_dialog_no_button)
                    }
                }
            }
        }
    }
}