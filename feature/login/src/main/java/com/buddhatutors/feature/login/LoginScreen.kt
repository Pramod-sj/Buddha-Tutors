@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.feature.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.FullScreenLoader
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.collectAndResetState
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageHelper
import com.buddhatutors.core.constant.ScreenResultConstant
import com.buddhatutors.feature.login.navigation.ExternalLoginNavigationHandler

@Preview
@Composable
internal fun PreviewLoginPage() {
    LoginScreenContent(uiState = LoginUiState(email = "", password = ""),
        uiEvent = {},
        loginNavigationHandler = object : ExternalLoginNavigationHandler {
            override fun navigateToTermsAndConditions() {
                TODO("Not yet implemented")
            }

            override fun navigateToRegistration() {
                TODO("Not yet implemented")
            }

            override fun navigateToAdminHome() {
                TODO("Not yet implemented")
            }

            override fun navigateToStudentHome() {
                TODO("Not yet implemented")
            }

            override fun navigateToTutorHome() {
                TODO("Not yet implemented")
            }

            override fun navigateToMasterTutor() {
                TODO("Not yet implemented")
            }

            override fun openForgotPassword() {
                TODO("Not yet implemented")
            }
        })
}

@Composable
fun LoginScreen(
    loginNavigationHandler: ExternalLoginNavigationHandler
) {

    val viewModel = hiltViewModel<LoginViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    LoginScreenContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        loginNavigationHandler = loginNavigationHandler
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {

                LoginUiEffect.NavigateToRegister -> {
                    loginNavigationHandler.navigateToRegistration()
                }

                LoginUiEffect.NavigateToAdminHome -> {
                    loginNavigationHandler.navigateToAdminHome()
                    /*navigator.navigate(route = AdminGraph, navOptions = navOptions {
                        popUpTo(AuthGraph.LoginUser) {
                            inclusive = true
                        }
                    })*/
                }

                LoginUiEffect.NavigateToMasterTutorHome -> {
                    loginNavigationHandler.navigateToMasterTutor()
                    /*navigator.navigate(MasterTutorGraph, navOptions = navOptions {
                        popUpTo(AuthGraph.LoginUser) {
                            inclusive = true
                        }
                    })*/
                }

                LoginUiEffect.NavigateToStudentHome -> {
                    loginNavigationHandler.navigateToStudentHome()
                    /*navigator.navigate(StudentGraph, navOptions = navOptions {
                        popUpTo(AuthGraph.LoginUser) {
                            inclusive = true
                        }
                    })*/
                }

                LoginUiEffect.NavigateToTutorHome -> {
                    loginNavigationHandler.navigateToTutorHome()

                    /*navigator.navigate(TutorGraph, navOptions = navOptions {
                        popUpTo(AuthGraph.LoginUser) {
                            inclusive = true
                        }
                    })*/
                }

                LoginUiEffect.NavigateToTermAndConditionPage -> {
                    loginNavigationHandler.navigateToTermsAndConditions()

                    //navigator.navigate(AuthGraph.TermAndConditions)
                }
            }
        }
    }
}


@Composable
internal fun LoginScreenContent(
    uiState: LoginUiState,
    uiEvent: (LoginUiEvent) -> Unit,
    loginNavigationHandler: ExternalLoginNavigationHandler
) {

    val navigator = Navigator

    val snackBarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState)
    }, bottomBar = {
        val annotatedLinkString = buildAnnotatedString {
            append("Don't have a student account? ")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold
                )
            ) {
                append("Register now")
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
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
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.Center
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    value = uiState.email.orEmpty(),
                    label = { Text(text = "Email address") },
                    onValueChange = { text ->
                        if (text.isNotBlank()) {
                            uiEvent(LoginUiEvent.OnEmailChanged(text))
                        } else {
                            uiEvent(LoginUiEvent.OnEmailChanged(null))
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    isError = uiState.validateLoginDataResult.isEmailValid == false,
                    shape = MaterialTheme.shapes.small,
                    colors = DefaultBuddhaTutorTextFieldColors
                )

                Column {

                    TextField(modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
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
                            //uiEvent(LoginUiEvent.OnLoginClick)
                        },
                        isError = uiState.validateLoginDataResult.isPasswordValid == false,
                        shape = MaterialTheme.shapes.small,
                        colors = DefaultBuddhaTutorTextFieldColors,

                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description =
                                if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        }

                    )

                    Spacer(Modifier.height(4.dp))

                    TextButton(modifier = Modifier.align(Alignment.End), onClick = {
                        loginNavigationHandler.openForgotPassword()
                        //navigator.navigate(AuthGraph.ForgotPassword)
                    }) {
                        Text(text = "Forgot password?")
                    }

                }


                Row(modifier = Modifier.clickable {
                    uiEvent(LoginUiEvent.OnTermsAndConditionsClick)
                }) {
                    Checkbox(checked = uiState.isTermConditionAccepted,
                        onCheckedChange = { uiEvent(LoginUiEvent.OnTermsAndConditionsClick) })
                    Text(buildAnnotatedString {
                        val span = "Terms & Conditions"
                        val str = "By proceeding with login, you agree to our Terms & Conditions."
                        val startIndex = str.indexOf(span)
                        val endIndex = startIndex + span.length
                        append(str)
                        addStyle(
                            style = SpanStyle(
                                color = Color.Blue, textDecoration = TextDecoration.Underline
                            ), start = startIndex, end = endIndex
                        )
                    })
                }

                Button(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        uiEvent(LoginUiEvent.OnLoginClick)
                    }, enabled = uiState.isLoginButtonEnabled
                ) {
                    Text(text = "Login")
                }
            }
        }
    }

    FullScreenLoader(isVisible = uiState.showFullScreenLoader)

    //show success message when reset password email is sent
    navigator.currentBackStackEntry?.savedStateHandle?.collectAndResetState(key = ScreenResultConstant.EXTRA_SUCCESSFULLY_SENT_FORGOT_PASS_LINK,
        defaultValue = false,
        onEffect = {
            if (it) {
                MessageHelper.showMessage(Message.Success("Reset password link is sent registered email id"))
            }
        })


    //show success message when register successfully
    navigator.currentBackStackEntry?.savedStateHandle?.collectAndResetState(key = ScreenResultConstant.EXTRA_SUCCESSFULLY_REGISTER,
        defaultValue = false,
        onEffect = {
            if (it) {
                MessageHelper.showMessage(Message.Success("You have successfully register, please use same email id to login"))
            }
        })

    // Retrieve the saved state handle from the previous back stack entry
    navigator.currentBackStackEntry?.savedStateHandle?.collectAndResetState(key = ScreenResultConstant.EXTRA_TERM_CONDITION_ACCEPTED,
        defaultValue = false,
        onEffect = {
            if (it) {
                uiEvent(LoginUiEvent.OnTermsAndConditionsStateChanged(true))
            }
        })
}