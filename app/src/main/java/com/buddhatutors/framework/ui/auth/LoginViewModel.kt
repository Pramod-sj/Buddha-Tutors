package com.buddhatutors.framework.ui.auth

import androidx.lifecycle.viewModelScope
import com.buddhatutors.domain.usecase.LoginUser
import com.buddhatutors.framework.Resource
import com.buddhatutors.framework.ui.BaseViewModel
import com.buddhatutors.framework.ui.UiEffect
import com.buddhatutors.framework.ui.UiEvent
import com.buddhatutors.framework.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUser: LoginUser
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
            when (val resource = loginUser(currentState.email, currentState.password)) {
                is Resource.Error -> {
                    setEffect { LoginUiEffect.ShowErrorMessage(resource.throwable.message.orEmpty()) }
                }

                is Resource.Success -> {
                    setEffect { LoginUiEffect.ShowLoginSuccess }
                }
            }
            setState { copy(showLoader = false) }
        }
    }

}


internal sealed class LoginUiEvent : UiEvent {

    data object OnLoginClick : LoginUiEvent()

    data object OnRegisterClick : LoginUiEvent()

    data class OnEmailChanged(val email: String) : LoginUiEvent()

    data class OnPasswordChanged(val password: String) : LoginUiEvent()

}

internal data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val showLoader: Boolean = false
) : UiState

internal sealed class LoginUiEffect : UiEffect {

    data object ShowLoginSuccess : LoginUiEffect()

    data class ShowErrorMessage(val message: String) : LoginUiEffect()

    data object NavigateToRegister : LoginUiEffect()

}
