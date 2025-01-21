package com.buddhatutors.feature.login

import androidx.lifecycle.viewModelScope
import com.buddhatutors.core.auth.domain.usecase.LoginValidationResult
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageHelper
import com.buddhatutors.core.auth.domain.EmailNotVerifiedException
import com.buddhatutors.core.auth.domain.usecase.LoginUser
import com.buddhatutors.core.auth.domain.usecase.SendEmailVerification
import com.buddhatutors.core.auth.domain.usecase.ValidateLoginDataUseCase
import com.buddhatutors.model.Resource
import com.buddhatutors.model.user.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUser: LoginUser,
    private val sendEmailVerification: SendEmailVerification,
    private val validateLoginDataUseCase: ValidateLoginDataUseCase
) : BaseViewModel<LoginUiEvent, LoginUiState, LoginUiEffect>() {

    override fun createInitialState(): LoginUiState = LoginUiState()

    override fun handleEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnEmailChanged -> {
                setState { copy(email = event.email) }
            }

            is LoginUiEvent.OnPasswordChanged -> {
                setState { copy(password = event.password) }
            }

            LoginUiEvent.OnLoginClick -> {
                loginUser()
            }

            LoginUiEvent.OnRegisterClick -> {
                setEffect { LoginUiEffect.NavigateToRegister }
            }

            LoginUiEvent.OnTermsAndConditionsClick -> {
                if (!currentState.isTermConditionAccepted) {
                    setEffect { LoginUiEffect.NavigateToTermAndConditionPage }
                } else {
                    setState { copy(isTermConditionAccepted = false) }
                }
            }

            is LoginUiEvent.OnTermsAndConditionsStateChanged -> {
                setState { copy(isTermConditionAccepted = event.isAccepted) }
            }
        }
    }

    private fun loginUser() {
        viewModelScope.launch {
            setState { copy(showFullScreenLoader = true) }
            when (val resource =
                loginUser(currentState.email.orEmpty(), currentState.password.orEmpty())) {

                is Resource.Error -> {
                    if (resource.throwable is EmailNotVerifiedException) {

                        MessageHelper.showMessage(Message.Warning(
                            text = resource.throwable.message.orEmpty(),
                            actionLabel = "Send mail",
                            actionCallback = {
                                viewModelScope.launch {
                                    when (val emailVerificationResource =
                                        sendEmailVerification()) {
                                        is Resource.Error -> {
                                            MessageHelper.showMessage(
                                                message = Message.Warning(
                                                    text = emailVerificationResource.throwable.message.orEmpty()
                                                )
                                            )
                                        }

                                        is Resource.Success -> {
                                            MessageHelper.showMessage(
                                                message = Message.Success(
                                                    text = "Verification email sent!"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        ))
                    } else {
                        MessageHelper.showMessage(
                            message = Message.Warning(
                                text = resource.throwable.message.orEmpty()
                            )
                        )
                    }
                }

                is Resource.Success -> {
                    val effect = when (resource.data.userType) {
                        UserType.STUDENT -> LoginUiEffect.NavigateToStudentHome
                        UserType.TUTOR -> LoginUiEffect.NavigateToTutorHome
                        UserType.ADMIN -> LoginUiEffect.NavigateToAdminHome
                        UserType.MASTER_TUTOR -> LoginUiEffect.NavigateToMasterTutorHome
                    }
                    setEffect { effect }
                }
            }
            setState {
                copy(isLoginButtonEnabled = true, showFullScreenLoader = false)
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(uiState.map { it.email },
                uiState.map { it.password },
                uiState.map { it.isTermConditionAccepted }) { email, pass, isTermConditionAccepted ->
                val loginResult = validateLoginDataUseCase(email, pass)
                setState {
                    copy(
                        validateLoginDataResult = loginResult,
                        isLoginButtonEnabled = loginResult.isValid && isTermConditionAccepted
                    )
                }
            }.collect()
        }
    }
}


internal sealed class LoginUiEvent : UiEvent {

    data object OnLoginClick : LoginUiEvent()

    data object OnRegisterClick : LoginUiEvent()

    data class OnEmailChanged(val email: String?) : LoginUiEvent()

    data class OnPasswordChanged(val password: String?) : LoginUiEvent()

    data object OnTermsAndConditionsClick : LoginUiEvent()

    data class OnTermsAndConditionsStateChanged(val isAccepted: Boolean) : LoginUiEvent()

}

internal data class LoginUiState(
    val email: String? = null,
    val password: String? = null,
    val isTermConditionAccepted: Boolean = false,
    val validateLoginDataResult: LoginValidationResult = LoginValidationResult(
        null,
        null
    ),

    val showFullScreenLoader: Boolean = false,
    val isLoginButtonEnabled: Boolean = false,
) : UiState


internal sealed class LoginUiEffect : UiEffect {

    data object NavigateToRegister : LoginUiEffect()

    data object NavigateToStudentHome : LoginUiEffect()

    data object NavigateToTutorHome : LoginUiEffect()

    data object NavigateToMasterTutorHome : LoginUiEffect()

    data object NavigateToAdminHome : LoginUiEffect()

    data object NavigateToTermAndConditionPage : LoginUiEffect()

}
