package com.woowla.ghd.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.woowla.compose.remixicon.RemixiconPainter
import com.woowla.compose.remixicon.SystemEyeFill
import com.woowla.compose.remixicon.SystemEyeOffFill
import com.woowla.ghd.presentation.app.AppDimens
import com.woowla.ghd.presentation.app.i18n
import com.woowla.ghd.presentation.components.LabelledCheckBox
import com.woowla.ghd.presentation.components.OutlinedTextFieldValidation
import com.woowla.ghd.presentation.components.ScreenScaffold
import com.woowla.ghd.presentation.decorators.ErrorMessageFactory
import com.woowla.ghd.presentation.viewmodels.LoginViewModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { LoginViewModel(navigator) }
        val loginState by viewModel.state.collectAsState()

        val lockedLoginState = loginState
        ScreenScaffold {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .padding(AppDimens.contentPaddingAllDp.dp)
                    .width(AppDimens.contentWidthDp.dp)
            ) {
                when(lockedLoginState) {
                    LoginViewModel.State.Initializing -> {
                        Text(i18n.generic_loading)
                    }
                    is LoginViewModel.State.NonexistentDatabase -> {
                        loginDatabaseNoExists(
                            navigator = navigator,
                            error = lockedLoginState.error,
                            onCreateNewDatabase = { encrypt, password ->
                                viewModel.onCreateDatabase(encrypt, password)
                            }
                        )
                    }
                    is LoginViewModel.State.LockedDatabase -> {
                        loginDatabaseAlreadyExists(
                            navigator = navigator,
                            isDbEncrypted = lockedLoginState.isDbEncrypted,
                            error = lockedLoginState.error,
                            onUnlockDatabase = { password ->
                                viewModel.onUnlockDatabase(password = password)
                            },
                            onDeleteDatabase = {
                                viewModel.onDeleteDatabase()
                            },
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun loginDatabaseNoExists(navigator: Navigator, error: Throwable? = null, onCreateNewDatabase: (encryt: Boolean, passsword: String?) -> Unit) {
        var encryptDatabase by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Button(
                    onClick = { onCreateNewDatabase.invoke(encryptDatabase, password) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(i18n.screen_login_create_new_database_button)
                }

                LabelledCheckBox(
                    label = i18n.screen_login_encrypt_data_field_label,
                    checked = encryptDatabase,
                    onCheckedChange = {
                        encryptDatabase = it
                    },
                )

                AnimatedVisibility(
                    visible = encryptDatabase,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Text(i18n.screen_login_master_password_info)
                        OutlinedTextFieldValidation(
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            label = { Text(i18n.screen_login_master_password_field_label) },
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
                                    RemixiconPainter.SystemEyeFill
                                } else {
                                    RemixiconPainter.SystemEyeOffFill
                                }
                                val description = if (passwordVisible) {
                                    i18n.screen_login_master_password_field_field_hide
                                } else {
                                    i18n.screen_login_master_password_field_field_show
                                }
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(painter = image, contentDescription = description, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(25.dp))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Column {
                OutlinedButton(
                    onClick = {
                        navigator.push(AboutScreen(onBackClick = { navigator.pop() }))
                    },
                ) {
                    Text(i18n.screen_login_about_app_button)
                }
            }
        }
    }

    @Composable
    private fun loginDatabaseAlreadyExists(navigator: Navigator, isDbEncrypted: Boolean, error: Throwable? = null, onUnlockDatabase: (password: String?) -> Unit, onDeleteDatabase: () -> Unit) {
        var passwordVisible by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        val openConfirmRemoveDatabaseDialog = remember { mutableStateOf(false)  }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                if (isDbEncrypted) {
                    OutlinedTextFieldValidation(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = { Text(i18n.screen_login_master_password_field_label) },
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
                                RemixiconPainter.SystemEyeFill
                            } else {
                                RemixiconPainter.SystemEyeOffFill
                            }
                            val description = if (passwordVisible) {
                                i18n.screen_login_master_password_field_field_hide
                            } else {
                                i18n.screen_login_master_password_field_field_show
                            }
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(painter = image, contentDescription = description, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(25.dp))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(i18n.screen_login_master_password_info)
                }
                Button(
                    onClick = { if (isDbEncrypted) onUnlockDatabase.invoke(password) else onUnlockDatabase.invoke(null) },
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
                    onClick = {
                        navigator.push(AboutScreen(onBackClick = { navigator.pop() }))
                    },
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

    @Composable
    private fun deleteDatabaseConfirmationDialog(onCloseRequest: () -> Unit, onConfirmClick: () -> Unit, onDiscardClick: () -> Unit, ) {
        Dialog(
            title = i18n.screen_login_fresh_start_confirmation_dialog_title,
            onCloseRequest = onCloseRequest,
            state = rememberDialogState(position = WindowPosition(Alignment.Center)),
        ) {
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