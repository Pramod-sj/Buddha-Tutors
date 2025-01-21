@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.feature.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.Navigator
import com.buddhatutors.core.constant.ScreenResultConstant.EXTRA_SUCCESSFULLY_SENT_FORGOT_PASS_LINK
import kotlinx.coroutines.flow.Flow

@Composable
fun ForgotPasswordScreen() {

    val viewModel = hiltViewModel<ForgotPasswordViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    ForgotPasswordContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        uiEffect = viewModel.effect
    )
}


@Composable
internal fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    uiEvent: (ForgotPasswordUiEvent) -> Unit,
    uiEffect: Flow<ForgotPasswordUiEffect>
) {

    val navigator = Navigator

    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        onDismissRequest = {
            navigator.popBackStack()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(modifier = Modifier.align(Alignment.Start)) {

                Text(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .align(Alignment.Start),
                    text = "Forgot password",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.Start),
                    text = "Please provide your email to reset your password.",
                )

            }

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.email.orEmpty(),
                label = { Text(text = "Email address") },
                onValueChange = { text ->
                    if (text.isNotBlank()) {
                        uiEvent(ForgotPasswordUiEvent.OnEmailChanged(text))
                    } else {
                        uiEvent(ForgotPasswordUiEvent.OnEmailChanged(null))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                isError = uiState.isEmailValid == false,
                shape = MaterialTheme.shapes.small,
                colors = DefaultBuddhaTutorTextFieldColors,

                )

            Spacer(Modifier)

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    uiEvent(ForgotPasswordUiEvent.SendForgotPassEmailButtonClick)
                },
                enabled = uiState.isEmailValid == true
            ) {
                if (uiState.showLoader) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Reset password")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { uiEffect ->
            when (uiEffect) {

                is ForgotPasswordUiEffect.PopupToLoginScreen -> {
                    navigator.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(EXTRA_SUCCESSFULLY_SENT_FORGOT_PASS_LINK, true)
                    navigator.popBackStack()
                }

            }
        }
    }

}