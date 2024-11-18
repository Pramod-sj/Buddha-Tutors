package com.buddhatutors.common.auth.ui.login

import androidx.lifecycle.viewModelScope
import com.buddhatutors.auth.EmailNotVerifiedException
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.UserType.*
import com.buddhatutors.domain.usecase.auth.LoginUser
import com.buddhatutors.domain.usecase.auth.LoginValidationResult
import com.buddhatutors.domain.usecase.auth.SendEmailVerification
import com.buddhatutors.domain.usecase.auth.ValidateLoginDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
        }
    }

    private fun loginUser() {
        viewModelScope.launch {
            setState { copy(showLoader = true) }
            when (val resource =
                loginUser(currentState.email.orEmpty(), currentState.password.orEmpty())) {

                is Resource.Error -> {
                    if (resource.throwable is EmailNotVerifiedException) {
                        setEffect {
                            LoginUiEffect.ShowMessage(
                                resource.throwable.message.orEmpty(),
                                actionButtonLabel = "Send",
                                actionButtonCallback = {
                                    viewModelScope.launch {
                                        when (val emailVerificationResource =
                                            sendEmailVerification()) {
                                            is Resource.Error -> {
                                                setEffect {
                                                    LoginUiEffect.ShowMessage(
                                                        message = emailVerificationResource.throwable.message.orEmpty()
                                                    )
                                                }
                                            }

                                            is Resource.Success -> {
                                                setEffect {
                                                    LoginUiEffect.ShowMessage(message = "Verification email sent!")
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        setEffect { LoginUiEffect.ShowMessage(resource.throwable.message.orEmpty()) }
                    }
                }

                is Resource.Success -> {
                    val effect = when (resource.data.userType) {
                        STUDENT -> LoginUiEffect.NavigateToStudentHome
                        TUTOR -> LoginUiEffect.NavigateToTutorHome
                        ADMIN -> LoginUiEffect.NavigateToAdminHome
                        MASTER_TUTOR -> LoginUiEffect.NavigateToMasterTutorHome
                    }
                    setEffect { effect }
                }
            }
            setState { copy(showLoader = false) }
        }
    }

    init {
        viewModelScope.launch {
            combine(uiState.map { it.email },
                uiState.map { it.password }) { email, pass ->
                email to pass
            }.collect { pair ->
                val (email, pass) = pair
                val loginResult = validateLoginDataUseCase(email, pass)
                setState { copy(validateLoginDataResult = loginResult) }
            }
        }
    }
}


internal sealed class LoginUiEvent : UiEvent {

    data object OnLoginClick : LoginUiEvent()

    data object OnRegisterClick : LoginUiEvent()

    data class OnEmailChanged(val email: String?) : LoginUiEvent()

    data class OnPasswordChanged(val password: String?) : LoginUiEvent()

}

internal data class LoginUiState(
    val email: String? = null,
    val password: String? = null,
    val showLoader: Boolean = false,
    val validateLoginDataResult: LoginValidationResult = LoginValidationResult(
        null,
        null
    )
) : UiState

internal sealed class LoginUiEffect : UiEffect {

    data class ShowMessage(
        val message: String,
        val actionButtonLabel: String? = null,
        val actionButtonCallback: (() -> Unit)? = null
    ) : LoginUiEffect()


    data object NavigateToRegister : LoginUiEffect()

    data object NavigateToStudentHome : LoginUiEffect()

    data object NavigateToTutorHome : LoginUiEffect()

    data object NavigateToMasterTutorHome : LoginUiEffect()

    data object NavigateToAdminHome : LoginUiEffect()

}
