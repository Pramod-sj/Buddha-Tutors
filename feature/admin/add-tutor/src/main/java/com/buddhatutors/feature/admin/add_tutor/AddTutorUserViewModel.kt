package com.buddhatutors.feature.admin.add_tutor

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.core.auth.domain.model.registration.ValidationResult
import com.buddhatutors.core.auth.domain.usecase.RegisterUser
import com.buddhatutors.core.auth.domain.usecase.ValidateRegistrationUseCase
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.usecase.GetLanguages
import com.buddhatutors.domain.usecase.topic.GetTopics
import com.buddhatutors.feature.admin.domain.Constant.DEFAULT_PASSWORD
import com.buddhatutors.model.Resource
import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.Topic
import com.buddhatutors.model.user.User
import com.buddhatutors.model.user.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddTutorUserViewModel @Inject constructor(
    private val registerUser: RegisterUser,
    private val getTopics: GetTopics,
    private val getLanguages: GetLanguages,
    private val validateRegistrationUseCase: ValidateRegistrationUseCase
) : BaseViewModel<AddUserUiEvent, AddUserUiState, AddUserUiEffect>() {

    override fun createInitialState(): AddUserUiState = AddUserUiState()

    override fun handleEvent(event: AddUserUiEvent) {
        when (event) {
            is AddUserUiEvent.OnEmailChanged -> {
                setState { copy(email = event.email) }
            }

            AddUserUiEvent.OnAddUserClick -> {
                registerUser()
            }

            is AddUserUiEvent.OnUserNameChanged -> {
                setState { copy(name = event.name) }
            }

            is AddUserUiEvent.OnUserTypeSelected -> {
                setState { copy(userType = event.type) }
            }

            is AddUserUiEvent.OnDayAvailabilityChanged -> {
                setEvent(AddUserUiEvent.UpdateDaysDialogVisibility(false))
                setState { copy(selectedAvailabilityDay = event.days) }
            }

            is AddUserUiEvent.OnTimeSlotAdded -> {
                val newList = currentState.selectedTimeSlots
                    .toMutableList()
                    .apply { add(event.timeSlot) }
                setState {
                    copy(
                        selectedTimeSlots = newList.toMutableSet().toList(),
                    )
                }
            }

            is AddUserUiEvent.OnExpertiseTopicChanged -> {
                setState {
                    copy(selectedTopics = currentState.topics
                        .filter { topic -> topic.label in event.topics })
                }
            }

            is AddUserUiEvent.UpdateTimePickerDialogVisibility -> {
                setState { copy(isTimepickerDialogVisible = event.isVisible) }
            }

            is AddUserUiEvent.OnTimeSlotRemoved -> {
                val newList = currentState.selectedTimeSlots.toMutableList()
                    .apply { remove(event.timeSlot) }
                setState { copy(selectedTimeSlots = newList.toMutableSet().toList()) }
            }

            is AddUserUiEvent.OnLanguageSelectionChanged -> {
                setEvent(AddUserUiEvent.UpdateLanguageDialogVisibility(false))
                setState { copy(selectedLanguage = event.languages) }
            }

            is AddUserUiEvent.UpdateTopicsDialogVisibility -> {
                setState { copy(isTopicSelectionDialogVisible = event.isVisible) }
            }

            is AddUserUiEvent.OnTopicItemRemoved -> {
                val newList = currentState.selectedTopics.toMutableList()
                    .apply { remove(event.topic) }
                setState { copy(selectedTopics = newList.toMutableSet().toList()) }
            }

            is AddUserUiEvent.UpdateDaysDialogVisibility -> {
                setState { copy(isDaySelectionDialogVisible = event.isVisible) }
            }

            is AddUserUiEvent.UpdateLanguageDialogVisibility -> {
                setState { copy(isLanguageSelectionDialogVisible = event.isVisible) }
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {

            setState { copy(showFullScreenLoader = true) }

            val user = User(
                id = currentState.email.orEmpty(),
                name = currentState.name.orEmpty(),
                email = currentState.email.orEmpty(),
                userType = currentState.userType
            )

            when (val resource = registerUser(
                model = user,
                pass = DEFAULT_PASSWORD,
                expertiseIn =
                if (currentState.userType == UserType.MASTER_TUTOR) emptyList()
                else currentState.selectedTopics,
                languages =
                if (currentState.userType == UserType.MASTER_TUTOR) emptyList()
                else currentState.selectedLanguage,
                availabilityDay =
                if (currentState.userType == UserType.MASTER_TUTOR) emptyList()
                else currentState.selectedAvailabilityDay,
                timeSlots =
                if (currentState.userType == UserType.MASTER_TUTOR) emptyList()
                else currentState.selectedTimeSlots,
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
                            selectedTopics = emptyList(),
                            selectedTimeSlots = emptyList(),
                            userType = UserType.TUTOR,
                            selectedAvailabilityDay = emptyList(),
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
                    userType = currentState.userType,
                    selectedAvailabilityDay = currentState.selectedAvailabilityDay,
                    selectedTimeSlots = currentState.selectedTimeSlots,
                    selectedTopics = currentState.topics,
                    selectedLanguages = currentState.selectedLanguage,
                    password = DEFAULT_PASSWORD,
                    confirmPassword = DEFAULT_PASSWORD
                )
                setState {
                    copy(
                        validationResult = validationResult,
                        isRegistrationValid = validationResult.isValid
                    )
                }
            }.launchIn(this)
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

            when (val languageResource = getLanguages()) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    setState { copy(languages = languageResource.data) }
                }
            }
        }
        validateRegistrationData()
    }

}


internal sealed class AddUserUiEvent : UiEvent {

    data class OnEmailChanged(val email: String) : AddUserUiEvent()

    data class OnUserNameChanged(val name: String) : AddUserUiEvent()

    data class OnUserTypeSelected(val type: UserType) :
        AddUserUiEvent()

    data class OnDayAvailabilityChanged(val days: List<String>) : AddUserUiEvent()

    data class OnTimeSlotAdded(val timeSlot: TimeSlot) :
        AddUserUiEvent()

    data class OnTimeSlotRemoved(val timeSlot: TimeSlot) :
        AddUserUiEvent()

    data class OnTopicItemRemoved(val topic: Topic) : AddUserUiEvent()

    data class OnExpertiseTopicChanged(val topics: List<String>) : AddUserUiEvent()

    data class OnLanguageSelectionChanged(val languages: List<String>) : AddUserUiEvent()

    data object OnAddUserClick : AddUserUiEvent()

    data class UpdateTimePickerDialogVisibility(val isVisible: Boolean) : AddUserUiEvent()

    data class UpdateTopicsDialogVisibility(val isVisible: Boolean) : AddUserUiEvent()

    data class UpdateLanguageDialogVisibility(val isVisible: Boolean) : AddUserUiEvent()

    data class UpdateDaysDialogVisibility(val isVisible: Boolean) : AddUserUiEvent()

}


internal data class AddUserUiState(

    val languages: List<String> = emptyList(),
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
    val userType: UserType = UserType.TUTOR,
    val selectedAvailabilityDay: List<String> = emptyList(),
    val selectedTimeSlots: List<TimeSlot> = emptyList(),
    val selectedTopics: List<Topic> = emptyList(),
    val selectedLanguage: List<String> = emptyList(),

    val isRegistrationValid: Boolean = false,

    val validationResult: ValidationResult? = null,

    val isTimepickerDialogVisible: Boolean = false,
    val isTopicSelectionDialogVisible: Boolean = false,
    val isLanguageSelectionDialogVisible: Boolean = false,
    val isDaySelectionDialogVisible: Boolean = false,

    val showFullScreenLoader: Boolean = false

) : UiState

internal sealed class AddUserUiEffect : UiEffect {

    data object ShowAddUserSuccess : AddUserUiEffect()

    data class ShowErrorMessage(val message: String) : AddUserUiEffect()

}
