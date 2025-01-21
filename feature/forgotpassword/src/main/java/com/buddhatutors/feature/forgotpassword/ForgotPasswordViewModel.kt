package com.buddhatutors.feature.forgotpassword

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageHelper
import com.buddhatutors.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.buddhatutors.core.auth.domain.usecase.SendForgotPasswordUseCase
import com.buddhatutors.core.auth.domain.usecase.ValidateForgotPasswordDataUseCase

@HiltViewModel
internal class ForgotPasswordViewModel @Inject constructor(
    private val sendForgotPasswordUseCase: SendForgotPasswordUseCase,
    private val validateForgotPasswordDataUseCase: ValidateForgotPasswordDataUseCase
) : BaseViewModel<ForgotPasswordUiEvent, ForgotPasswordUiState, ForgotPasswordUiEffect>() {

    override fun createInitialState(): ForgotPasswordUiState = ForgotPasswordUiState()

    override fun handleEvent(event: ForgotPasswordUiEvent) {
        when (event) {
            ForgotPasswordUiEvent.SendForgotPassEmailButtonClick -> {
                viewModelScope.launch {
                    setState { copy(showLoader = true) }
                    when (val resource = sendForgotPasswordUseCase(currentState.email.orEmpty())) {
                        is Resource.Error -> {
                            MessageHelper.showMessage(
                                Message.Warning(
                                    text = resource.throwable.message.orEmpty()
                                )
                            )
                        }

                        is Resource.Success -> {
                            setEffect { ForgotPasswordUiEffect.PopupToLoginScreen(true) }
                        }
                    }
                    setState { copy(showLoader = false) }
                }
            }

            is ForgotPasswordUiEvent.OnEmailChanged -> {
                setState {
                    copy(
                        email = event.email,
                        isEmailValid = validateForgotPasswordDataUseCase(event.email)
                    )
                }
            }
        }
    }

}


internal sealed class ForgotPasswordUiEvent : UiEvent {

    data object SendForgotPassEmailButtonClick : ForgotPasswordUiEvent()

    data class OnEmailChanged(val email: String?) : ForgotPasswordUiEvent()

}


internal data class ForgotPasswordUiState(
    val email: String? = null,
    val isEmailValid: Boolean? = null,
    val showLoader: Boolean = false
) : UiState


internal sealed class ForgotPasswordUiEffect : UiEffect {

    data class PopupToLoginScreen(val isSuccessfullySentForgotPasswordMail: Boolean) :
        ForgotPasswordUiEffect()

}
