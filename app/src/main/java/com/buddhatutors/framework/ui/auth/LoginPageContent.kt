@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.framework.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.framework.ui.common.Navigator
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Preview
@Composable
internal fun PreviewLoginPage() {
    LoginPageContent(
        uiState = LoginUiState(email = "", password = ""),
        uiEvent = {},
        uiEffect = flow { }
    )
}

@Composable
internal fun LoginPage() {

    val viewModel = hiltViewModel<LoginViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    LoginPageContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        uiEffect = viewModel.effect
    )
}


@Composable
internal fun LoginPageContent(
    uiState: LoginUiState,
    uiEvent: (LoginUiEvent) -> Unit,
    uiEffect: kotlinx.coroutines.flow.Flow<LoginUiEffect>
) {

    val navigator = Navigator

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "BuddhaTutors",
                        style = MaterialTheme.typography.titleLarge
                    )
                })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    placeholder = {
                        Text(text = "Email")
                    },
                    onValueChange = { text ->
                        uiEvent(LoginUiEvent.OnEmailChanged(text))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Next)
                    })
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password,
                    visualTransformation = PasswordVisualTransformation(),
                    placeholder = {
                        Text(text = "Password")
                    },
                    onValueChange = { text ->
                        uiEvent(LoginUiEvent.OnPasswordChanged(text))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnLoginClick)
                    })
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnLoginClick)
                    }) {
                    if (uiState.showLoader) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(text = "Login")
                    }
                }

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        uiEvent(LoginUiEvent.OnRegisterClick)
                    }
                ) {
                    Text(text = "Register yourself")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { uiEffect ->
            when (uiEffect) {
                is LoginUiEffect.ShowErrorMessage -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = uiEffect.message)
                    }
                }

                LoginUiEffect.ShowLoginSuccess -> {
                    navigator.popBackStack()
                    navigator.navigate("/home")
                }

                LoginUiEffect.NavigateToRegister -> {
                    navigator.navigate("/register")
                }
            }
        }
    }

}