package com.buddhatutors.auth.presentation.register

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.domain.model.TimeSlot
import com.buddhatutors.auth.domain.model.registration.ValidationResult
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import com.buddhatutors.auth.domain.usecase.RegisterUser
import com.buddhatutors.auth.domain.usecase.ValidateRegistrationUseCase
import com.buddhatutors.common.domain.usecase.topic.GetTopics
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
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
                //setState { copy(userType = event.type) }
            }

            is RegisterUiEvent.OnDayAvailabilityChanged -> {
                setState { copy(selectedAvailabilityDay = event.days) }
            }

            is RegisterUiEvent.OnTimeSlotAdded -> {
                val newList = currentState.selectedTimeSlots
                    .toMutableList()
                    .apply { add(event.timeSlot) }
                setState {
                    copy(
                        selectedTimeSlots = newList.toMutableSet().toList(),
                    )
                }
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

            is RegisterUiEvent.UpdateTimePickerDialogVisibility -> {
                setState { copy(isTimepickerDialogVisible = event.isVisible) }
            }

            is RegisterUiEvent.OnTimeSlotRemoved -> {
                val newList = currentState.selectedTimeSlots.toMutableList()
                    .apply { remove(event.timeSlot) }
                setState { copy(selectedTimeSlots = newList.toMutableSet().toList()) }
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {

            setState { copy(showFullScreenLoader = true) }

            val user = User(
                id = "",
                name = currentState.name.orEmpty(),
                email = currentState.email.orEmpty(),
                userType = currentState.userType
            )

            when (val resource = registerUser(
                model = user,
                pass = currentState.password.orEmpty(),
                expertiseIn = currentState.topics,
                languages = emptyList(),
                availabilityDay = currentState.selectedAvailabilityDay,
                timeSlots = currentState.selectedTimeSlots,
            )) {
                is Resource.Error -> {
                    MessageHelper.showMessage(Message.Warning(resource.throwable.message.orEmpty()))
                }

                is Resource.Success -> {
                    setState {
                        copy(
                            name = "",
                            email = "",
                            password = "",
                            confirmPassword = "",
                            selectedTopics = emptyList(),
                            selectedTimeSlots = emptyList(),
                            userType = UserType.STUDENT,
                            selectedAvailabilityDay = emptyList(),
                        )
                    }
                    setEffect { RegisterUiEffect.ShowRegisterSuccess }
                }
            }
            setState { copy(showFullScreenLoader = false) }
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
                    selectedTimeSlots = currentState.selectedTimeSlots,
                    selectedTopics = currentState.topics,
                    selectedLanguages = emptyList()
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


internal sealed class RegisterUiEvent : com.buddhatutors.common.UiEvent {

    data class OnEmailChanged(val email: String) : RegisterUiEvent()

    data class OnPasswordChanged(val password: String) : RegisterUiEvent()

    data class OnConfirmPasswordChanged(val password: String) : RegisterUiEvent()

    data class OnUserNameChanged(val name: String) : RegisterUiEvent()

    data class OnUserTypeSelected(val type: UserType) : RegisterUiEvent()

    data class OnDayAvailabilityChanged(val days: List<String>) : RegisterUiEvent()

    data class OnTimeSlotAdded(val timeSlot: TimeSlot) : RegisterUiEvent()

    data class OnTimeSlotRemoved(val timeSlot: TimeSlot) : RegisterUiEvent()

    data class OnExpertiseTopicChanged(val topics: List<String>) : RegisterUiEvent()

    data object OnRegisterClick : RegisterUiEvent()

    data object OnTermsAndConditionsClick : RegisterUiEvent()

    data class OnTermsAndConditionsStateChanged(val isAccepted: Boolean) : RegisterUiEvent()

    data class UpdateTimePickerDialogVisibility(val isVisible: Boolean) : RegisterUiEvent()
}


internal data class RegisterUiState(

    val topics: List<Topic> = emptyList(),
    val days: List<Pair<String, String>> = listOf(
        "Mon" to "Monday",
        "Tue" to "Tuesday",
        "Wed" to "Wednesday",
        "Thu" to "Thursday",
        "Fri" to "Friday",
        "Sat" to "Saturday",
        "Sun" to "Sunday"
    ),

    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val userType: UserType = UserType.STUDENT,
    val selectedAvailabilityDay: List<String> = emptyList(),
    val selectedTimeSlots: List<TimeSlot> = emptyList(),
    val selectedTopics: List<Topic> = emptyList(),

    val isTermConditionAccepted: Boolean = false,

    val isRegistrationValid: Boolean = false,
    val isRegistrationInProgress: Boolean = false,

    val validationResult: ValidationResult? = null,


    val isTimepickerDialogVisible: Boolean = false,

    val showFullScreenLoader: Boolean = false
) : UiState

internal sealed class RegisterUiEffect : UiEffect {

    data object ShowRegisterSuccess : RegisterUiEffect()

    data object NavigateToTermAndConditionPage :
        RegisterUiEffect()
}
