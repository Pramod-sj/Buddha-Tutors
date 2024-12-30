package com.buddhatutors.appadmin.presentation.admin.addmastertutor

import androidx.lifecycle.viewModelScope
import com.buddhatutors.appadmin.Constant.DEFAULT_PASSWORD
import com.buddhatutors.auth.domain.model.registration.ValidationResult
import com.buddhatutors.auth.domain.usecase.RegisterUser
import com.buddhatutors.auth.domain.usecase.ValidateRegistrationUseCase
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.CurrentUser
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddMasterTutorViewModel @Inject constructor(
    private val registerUser: RegisterUser,
    private val validateRegistrationUseCase: ValidateRegistrationUseCase
) : BaseViewModel<AddUserUiEvent, AddUserUiState, AddUserUiEffect>() {

    override fun createInitialState(): AddUserUiState = AddUserUiState()

    override fun handleEvent(event: AddUserUiEvent) {
        when (event) {
            is AddUserUiEvent.OnEmailChanged -> {
                setState { copy(email = event.email) }
            }

            is AddUserUiEvent.OnUserNameChanged -> {
                setState { copy(name = event.name) }
            }

            AddUserUiEvent.OnAddUserClick -> {
                registerMasterTutor()
            }
        }
    }

    private fun registerMasterTutor() {
        viewModelScope.launch {
            setState { copy(showFullScreenLoader = true) }

            val user = User(
                id = currentState.email.orEmpty(),
                name = currentState.name.orEmpty(),
                email = currentState.email.orEmpty(),
                userType = UserType.MASTER_TUTOR
            )

            when (val resource = registerUser(
                model = user,
                pass = DEFAULT_PASSWORD,
                expertiseIn = emptyList(),
                languages = emptyList(),
                availabilityDay = emptyList(),
                timeSlots = emptyList(),
                addByUser = CurrentUser.user.value
            )) {
                is Resource.Error -> {
                    setEffect { AddUserUiEffect.ShowErrorMessage(resource.throwable.message.orEmpty()) }
                }
                is Resource.Success -> {
                    setState {
                        copy(
                            name = "",
                            email = "",
                        )
                    }
                    setEffect { AddUserUiEffect.ShowAddUserSuccess }
                }
            }
            setState { copy(showFullScreenLoader = false) }
        }
    }

    private fun validateRegistrationData() {
        viewModelScope.launch {
            uiState.onEach {
                val validationResult = validateRegistrationUseCase(
                    name = currentState.name,
                    email = currentState.email,
                    userType = UserType.MASTER_TUTOR,
                    password = DEFAULT_PASSWORD,
                    confirmPassword = DEFAULT_PASSWORD,
                    selectedTopics = null,
                    selectedAvailabilityDay = null,
                    selectedTimeSlots = null,
                    selectedLanguages = null,
                )
                setState {
                    copy(
                        validationResult = validationResult,
                        isAddButtonEnabled = validationResult.isValid
                    )
                }
            }.launchIn(this)
        }
    }

    init {
        validateRegistrationData()
    }
}

internal sealed class AddUserUiEvent : UiEvent {
    data class OnEmailChanged(val email: String) : AddUserUiEvent()
    data class OnUserNameChanged(val name: String) : AddUserUiEvent()
    data object OnAddUserClick : AddUserUiEvent()
}

internal data class AddUserUiState(
    val name: String? = null,
    val email: String? = null,
    val isAddButtonEnabled: Boolean = false,
    val validationResult: ValidationResult? = null,
    val showFullScreenLoader: Boolean = false
) : UiState

internal sealed class AddUserUiEffect : UiEffect {
    data object ShowAddUserSuccess : AddUserUiEffect()
    data class ShowErrorMessage(val message: String) : AddUserUiEffect()
}
