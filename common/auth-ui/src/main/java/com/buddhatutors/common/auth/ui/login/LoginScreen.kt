@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.common.auth.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.AuthGraph
import com.buddhatutors.common.navigation.MasterTutorGraph
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.TutorGraph
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Preview
@Composable
internal fun PreviewLoginPage() {
    LoginScreenContent(
        uiState = LoginUiState(email = "", password = ""),
        uiEvent = {},
        uiEffect = flow { }
    )
}

@Composable
fun LoginScreen() {

    val viewModel = hiltViewModel<LoginViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    LoginScreenContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        uiEffect = viewModel.effect
    )
}


@Composable
internal fun LoginScreenContent(
    uiState: LoginUiState,
    uiEvent: (LoginUiEvent) -> Unit,
    uiEffect: kotlinx.coroutines.flow.Flow<LoginUiEffect>
) {

    val navigator = Navigator

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        bottomBar = {
            val annotatedLinkString = buildAnnotatedString {
                append("Don't have an account? ")

                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Register now")
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    modifier = Modifier
                        .padding(20.dp)
                        .navigationBarsPadding()
                        .clickable { uiEvent(LoginUiEvent.OnRegisterClick) },
                    text = annotatedLinkString,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Center
                )

            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.Start),
                    text = "Sign in to BuddhaTutors",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )



                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email.orEmpty(),
                    label = { Text(text = "Email address") },
                    onValueChange = { text ->
                        if (text.isNotBlank()) {
                            uiEvent(LoginUiEvent.OnEmailChanged(text))
                        } else {
                            uiEvent(LoginUiEvent.OnEmailChanged(null))
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    isError = uiState.validateLoginDataResult.isEmailValid == false,
                    shape = MaterialTheme.shapes.small,
                    colors = DefaultBuddhaTutorTextFieldColors,

                    )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password.orEmpty(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    label = { Text(text = "Password") },
                    onValueChange = { text ->
                        if (text.isNotBlank()) {
                            uiEvent(LoginUiEvent.OnPasswordChanged(text))
                        } else {
                            uiEvent(LoginUiEvent.OnPasswordChanged(null))
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnLoginClick)
                    },
                    isError = uiState.validateLoginDataResult.isPasswordValid == false,
                    shape = MaterialTheme.shapes.small,
                    colors = DefaultBuddhaTutorTextFieldColors,

                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }

                )

                Spacer(Modifier.height(4.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnLoginClick)
                    },
                    enabled = uiState.validateLoginDataResult.isValid
                ) {
                    if (uiState.showLoader) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(text = "Login")
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { uiEffect ->
            when (uiEffect) {
                is LoginUiEffect.ShowMessage -> {
                    coroutineScope.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = uiEffect.message,
                            actionLabel = uiEffect.actionButtonLabel
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            uiEffect.actionButtonCallback?.invoke()
                        }
                    }
                }

                LoginUiEffect.NavigateToRegister -> {
                    navigator.navigate(AuthGraph.RegisterUser)
                }

                LoginUiEffect.NavigateToAdminHome -> {
                    navigator.navigate(
                        route = AdminGraph,
                        navOptions = navOptions {
                            popUpTo(AuthGraph.LoginUser) {
                                inclusive = true
                            }
                        }
                    )
                }

                LoginUiEffect.NavigateToMasterTutorHome -> {
                    navigator.navigate(MasterTutorGraph,
                        navOptions = navOptions {
                            popUpTo(AuthGraph.LoginUser) {
                                inclusive = true
                            }
                        })
                }

                LoginUiEffect.NavigateToStudentHome -> {
                    navigator.navigate(StudentGraph,
                        navOptions = navOptions {
                            popUpTo(AuthGraph.LoginUser) {
                                inclusive = true
                            }
                        })
                }

                LoginUiEffect.NavigateToTutorHome -> {
                    navigator.navigate(TutorGraph,
                        navOptions = navOptions {
                            popUpTo(AuthGraph.LoginUser) {
                                inclusive = true
                            }
                        })
                }
            }
        }
    }

}