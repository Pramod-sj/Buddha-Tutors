package com.buddhatutors.framework.ui.auth.register

import androidx.lifecycle.viewModelScope
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.User
import com.buddhatutors.domain.model.User.UserType.STUDENT
import com.buddhatutors.domain.model.User.UserType.TUTOR
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.usecase.RegisterUser
import com.buddhatutors.domain.usecase.ValidateRegistrationUseCase
import com.buddhatutors.domain.usecase.topic.GetTopics
import com.buddhatutors.framework.Resource
import com.buddhatutors.framework.ui.BaseViewModel
import com.buddhatutors.framework.ui.UiEffect
import com.buddhatutors.framework.ui.UiEvent
import com.buddhatutors.framework.ui.UiState
import com.buddhatutors.framework.ui.auth.register.common.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RegisterViewModel @Inject constructor(
    private val registerUser: RegisterUser,
    private val getTopics: GetTopics,
    private val validateRegistrationUseCase: ValidateRegistrationUseCase
) : BaseViewModel<RegisterUiEvent, RegisterUiState, RegisterUiEffect>() {

    override fun createInitialState(): RegisterUiState = RegisterUiState()

    override fun handleEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.OnEmailChanged -> {
                setState { copy(email = event.email) }
            }

            is RegisterUiEvent.OnPasswordChanged -> {
                setState { copy(password = event.password) }
            }


            RegisterUiEvent.OnRegisterClick -> {
                registerUser()
            }

            is RegisterUiEvent.OnConfirmPasswordChanged -> {
                setState { copy(confirmPassword = event.password) }
            }

            is RegisterUiEvent.OnUserNameChanged -> {
                setState { copy(name = event.name) }
            }

            is RegisterUiEvent.OnUserTypeSelected -> {
                setState { copy(userType = event.type) }
            }

            is RegisterUiEvent.OnDayAvailabilityChanged -> {
                setState { copy(selectedAvailabilityDay = event.days) }
            }

            is RegisterUiEvent.OnTimeAvailabilityChanged -> {
                setState { copy(selectedTimeSlot = event.timeSlot) }
            }

            is RegisterUiEvent.OnExpertiseTopicChanged -> {
                setState {
                    copy(selectedTopics = currentState.topics
                        .filter { topic -> topic.label in event.topics })
                }
            }

            RegisterUiEvent.OnTermsAndConditionsClick -> {
                if (!currentState.isTermConditionAccepted) {
                    setEffect { RegisterUiEffect.NavigateToTermAndConditionPage }
                } else {
                    setState { copy(isTermConditionAccepted = false) }
                }
            }

            is RegisterUiEvent.OnTermsAndConditionsStateChanged -> {
                setState { copy(isTermConditionAccepted = event.isAccepted) }
            }

        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            setState { copy(isRegistrationInProgress = true) }
            when (val resource = registerUser(
                model = when (currentState.userType) {
                    STUDENT -> {
                        User.Student(
                            id = "",
                            name = currentState.name.orEmpty(),
                            email = currentState.email.orEmpty()
                        )
                    }

                    TUTOR -> {
                        User.Tutor(
                            id = "",
                            name = currentState.name.orEmpty(),
                            email = currentState.email.orEmpty(),
                            expertiseIn = currentState.selectedTopics,
                            availabilityDay = currentState.selectedAvailabilityDay,
                            timeAvailability = currentState.selectedTimeSlot
                        )
                    }
                },
                pass = currentState.password.orEmpty()
            )) {
                is Resource.Error -> {
                    setEffect { RegisterUiEffect.ShowErrorMessage(resource.throwable.message.orEmpty()) }
                }

                is Resource.Success -> {
                    setState {
                        copy(
                            name = "",
                            email = "",
                            password = "",
                            confirmPassword = "",
                            selectedTopics = emptyList(),
                            selectedTimeSlot = TimeSlot(null, null),
                            userType = STUDENT,
                            selectedAvailabilityDay = emptyList(),
                        )
                    }
                    setEffect { RegisterUiEffect.ShowRegisterSuccess }
                }
            }
            setState { copy(isRegistrationInProgress = false) }
        }
    }

    private fun validateRegistrationData() {

        viewModelScope.launch {
            uiState.collect {
                val validationResult = validateRegistrationUseCase(
                    name = currentState.name,
                    email = currentState.email,
                    password = currentState.password,
                    confirmPassword = currentState.confirmPassword,
                    userType = currentState.userType,
                    selectedAvailabilityDay = currentState.selectedAvailabilityDay,
                    selectedTimeSlot = currentState.selectedTimeSlot,
                    selectedTopics = currentState.topics
                )
                setState {
                    copy(
                        validationResult = validationResult,
                        isRegistrationValid = validationResult.isValid && currentState.isTermConditionAccepted
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            when (val topicResource = getTopics()) {
                is Resource.Error -> {
                    //handle error state or a re-calling strategy
                }

                is Resource.Success -> {
                    setState { copy(topics = topicResource.data) }
                }
            }
        }
        validateRegistrationData()
    }

}


internal sealed class RegisterUiEvent : UiEvent {

    data class OnEmailChanged(val email: String) : RegisterUiEvent()

    data class OnPasswordChanged(val password: String) : RegisterUiEvent()

    data class OnConfirmPasswordChanged(val password: String) : RegisterUiEvent()

    data class OnUserNameChanged(val name: String) : RegisterUiEvent()

    data class OnUserTypeSelected(val type: User.UserType) : RegisterUiEvent()

    data class OnDayAvailabilityChanged(val days: List<String>) : RegisterUiEvent()

    data class OnTimeAvailabilityChanged(val timeSlot: TimeSlot) : RegisterUiEvent()

    data class OnExpertiseTopicChanged(val topics: List<String>) : RegisterUiEvent()

    data object OnRegisterClick : RegisterUiEvent()

    data object OnTermsAndConditionsClick : RegisterUiEvent()

    data class OnTermsAndConditionsStateChanged(val isAccepted: Boolean) : RegisterUiEvent()

}


internal data class RegisterUiState(

    val topics: List<Topic> = emptyList(),

    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val userType: User.UserType = STUDENT,
    val selectedAvailabilityDay: List<String> = emptyList(),
    val selectedTimeSlot: TimeSlot = TimeSlot(null, null),
    val selectedTopics: List<Topic> = emptyList(),

    val isTermConditionAccepted: Boolean = false,

    val isRegistrationValid: Boolean = false,
    val isRegistrationInProgress: Boolean = false,

    val validationResult: ValidationResult? = null

) : UiState

internal sealed class RegisterUiEffect : UiEffect {

    data object ShowRegisterSuccess : RegisterUiEffect()

    data class ShowErrorMessage(val message: String) : RegisterUiEffect()

    data object NavigateToTermAndConditionPage :
        RegisterUiEffect()
}
