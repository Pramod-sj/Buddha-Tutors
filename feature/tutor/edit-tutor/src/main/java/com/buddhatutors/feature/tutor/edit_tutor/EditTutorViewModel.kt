package com.buddhatutors.feature.tutor.edit_tutor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageHelper
import com.buddhatutors.core.auth.domain.model.registration.ValidationResult
import com.buddhatutors.core.auth.domain.usecase.ValidateRegistrationUseCase
import com.buddhatutors.core.navigation.navigationCustomArgument
import com.buddhatutors.domain.usecase.GetLanguages
import com.buddhatutors.domain.usecase.GetTutorListingByTutorId
import com.buddhatutors.domain.usecase.topic.GetTopics
import com.buddhatutors.domain.usecase.tutor.UpdateTutorListing
import com.buddhatutors.feature.tutor.edit_tutor.navigation.EditTutorAvailabilityRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EditTutorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTutorListingByTutorId: GetTutorListingByTutorId,
    private val updateTutorListing: UpdateTutorListing,
    private val getTopics: GetTopics,
    private val getLanguages: GetLanguages,
    private val validateRegistrationUseCase: ValidateRegistrationUseCase
) : BaseViewModel<EditTutorUiEvent, EditTutorUiState, EditTutorUiEffect>() {

    override fun createInitialState(): EditTutorUiState = EditTutorUiState()

    override fun handleEvent(event: EditTutorUiEvent) {
        when (event) {

            EditTutorUiEvent.UpdateButtonClick -> {
                updateTutorListing()
            }

            is EditTutorUiEvent.OnDayAvailabilityChanged -> {
                setEvent(EditTutorUiEvent.UpdateDaysDialogVisibility(false))
                setState { copy(selectedAvailabilityDay = event.days) }
            }

            is EditTutorUiEvent.OnTimeSlotAdded -> {
                val newList = currentState.selectedTimeSlots
                    .toMutableList()
                    .apply { add(event.timeSlot) }
                setState {
                    copy(
                        selectedTimeSlots = newList.toMutableSet().toList(),
                    )
                }
            }

            is EditTutorUiEvent.OnExpertiseTopicChanged -> {
                setState {
                    copy(selectedTopics = currentState.topics
                        .filter { topic -> topic.label in event.topics })
                }
            }

            is EditTutorUiEvent.UpdateTimePickerDialogVisibility -> {
                setState { copy(isTimepickerDialogVisible = event.isVisible) }
            }

            is EditTutorUiEvent.OnTimeSlotRemoved -> {
                val newList = currentState.selectedTimeSlots.toMutableList()
                    .apply { remove(event.timeSlot) }
                setState { copy(selectedTimeSlots = newList.toMutableSet().toList()) }
            }

            is EditTutorUiEvent.OnLanguageSelectionChanged -> {
                setEvent(EditTutorUiEvent.UpdateLanguageDialogVisibility(false))
                setState { copy(selectedLanguage = event.languages) }
            }

            is EditTutorUiEvent.UpdateTopicsDialogVisibility -> {
                setState { copy(isTopicSelectionDialogVisible = event.isVisible) }
            }

            is EditTutorUiEvent.OnTopicItemRemoved -> {
                val newList = currentState.selectedTopics.toMutableList()
                    .apply { remove(event.topic) }
                setState { copy(selectedTopics = newList.toMutableSet().toList()) }
            }

            is EditTutorUiEvent.UpdateDaysDialogVisibility -> {
                setState { copy(isDaySelectionDialogVisible = event.isVisible) }
            }

            is EditTutorUiEvent.UpdateLanguageDialogVisibility -> {
                setState { copy(isLanguageSelectionDialogVisible = event.isVisible) }
            }

            is EditTutorUiEvent.OnDayAvailabilityRemoved -> {
                val newList = currentState.selectedAvailabilityDay.toMutableList()
                    .apply { remove(event.day) }
                setState { copy(selectedAvailabilityDay = newList.toMutableSet().toList()) }
            }

            is EditTutorUiEvent.OnLanguageItemRemoved -> {
                val newList = currentState.selectedLanguage.toMutableList()
                    .apply { remove(event.language) }
                setState { copy(selectedLanguage = newList.toMutableSet().toList()) }
            }
        }
    }

    private fun updateTutorListing() {
        viewModelScope.launch {

            val tutorListing = currentState.currentTutorListing ?: return@launch

            setState { copy(showFullScreenLoader = true) }

            when (val resource = updateTutorListing(
                tutorListing = tutorListing.copy(
                    availableTimeSlots = currentState.selectedTimeSlots,
                    availableDays = currentState.selectedAvailabilityDay,
                    expertiseIn = currentState.selectedTopics,
                    languages = currentState.selectedLanguage
                )
            )) {
                is com.buddhatutors.model.Resource.Error -> {
                    MessageHelper.showMessage(Message.Warning(resource.throwable.message.orEmpty()))
                }

                is com.buddhatutors.model.Resource.Success -> {
                    setState {
                        copy(
                            currentTutorListing = resource.data,
                            selectedTopics = resource.data.expertiseIn,
                            selectedTimeSlots = resource.data.availableTimeSlots,
                            selectedAvailabilityDay = resource.data.availableDays,
                            selectedLanguage = resource.data.languages
                        )
                    }
                    MessageHelper.showMessage(Message.Success("Tutor data is now updated!"))
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
                    userType = com.buddhatutors.model.user.UserType.TUTOR,
                    selectedAvailabilityDay = currentState.selectedAvailabilityDay,
                    selectedTimeSlots = currentState.selectedTimeSlots,
                    selectedTopics = currentState.selectedTopics,
                    selectedLanguages = currentState.selectedLanguage,
                    password = "12345678",
                    confirmPassword = "12345678"
                )
                setState {
                    copy(
                        validationResult = validationResult,
                        isRegistrationValid = validationResult.isValid
                                && (currentState.currentTutorListing != currentState.currentTutorListing?.copy(
                            availableTimeSlots = currentState.selectedTimeSlots,
                            availableDays = currentState.selectedAvailabilityDay,
                            expertiseIn = currentState.selectedTopics,
                            languages = currentState.selectedLanguage
                        ))
                    )
                }
            }.launchIn(this)
        }
    }

    init {

        viewModelScope.launch {

            setState { copy(showInitialLoader = true) }

            val tutorId = savedStateHandle.toRoute<EditTutorAvailabilityRoute>(
                mapOf(navigationCustomArgument<String>())
            ).tutorId

            when (val resource = getTutorListingByTutorId(tutorId = tutorId)) {
                is com.buddhatutors.model.Resource.Error -> {
                    //handle error state or a re-calling strategy
                }

                is com.buddhatutors.model.Resource.Success -> {
                    val tutorListing = resource.data
                    setState {
                        copy(
                            currentTutorListing = tutorListing,
                            name = tutorListing.tutorUser.name,
                            email = tutorListing.tutorUser.email,
                            selectedTopics = tutorListing.expertiseIn,
                            selectedLanguage = tutorListing.languages,
                            selectedTimeSlots = tutorListing.availableTimeSlots,
                            selectedAvailabilityDay = tutorListing.availableDays,
                        )
                    }
                }
            }

            when (val topicResource = getTopics()) {
                is com.buddhatutors.model.Resource.Error -> {
                    //handle error state or a re-calling strategy
                }

                is com.buddhatutors.model.Resource.Success -> {
                    setState { copy(topics = topicResource.data) }
                }
            }

            when (val languageResource = getLanguages()) {
                is com.buddhatutors.model.Resource.Error -> {

                }

                is com.buddhatutors.model.Resource.Success -> {
                    setState { copy(languages = languageResource.data) }
                }
            }

            setState { copy(showInitialLoader = false) }

        }
        validateRegistrationData()
    }

}


internal sealed class EditTutorUiEvent : UiEvent {

    data class OnDayAvailabilityChanged(val days: List<String>) : EditTutorUiEvent()

    data class OnDayAvailabilityRemoved(val day: String) : EditTutorUiEvent()


    data class OnTimeSlotAdded(val timeSlot: com.buddhatutors.model.TimeSlot) :
        EditTutorUiEvent()

    data class OnTimeSlotRemoved(val timeSlot: com.buddhatutors.model.TimeSlot) :
        EditTutorUiEvent()


    data class OnExpertiseTopicChanged(val topics: List<String>) : EditTutorUiEvent()

    data class OnTopicItemRemoved(val topic: com.buddhatutors.model.Topic) : EditTutorUiEvent()


    data class OnLanguageSelectionChanged(val languages: List<String>) : EditTutorUiEvent()

    data class OnLanguageItemRemoved(val language: String) : EditTutorUiEvent()


    data object UpdateButtonClick : EditTutorUiEvent()

    data class UpdateTimePickerDialogVisibility(val isVisible: Boolean) : EditTutorUiEvent()

    data class UpdateTopicsDialogVisibility(val isVisible: Boolean) : EditTutorUiEvent()

    data class UpdateLanguageDialogVisibility(val isVisible: Boolean) : EditTutorUiEvent()

    data class UpdateDaysDialogVisibility(val isVisible: Boolean) : EditTutorUiEvent()

}


internal data class EditTutorUiState(

    val tutorId: String = "",
    val currentTutorListing: com.buddhatutors.model.tutorlisting.TutorListing? = null,

    val languages: List<String> = emptyList(),
    val topics: List<com.buddhatutors.model.Topic> = emptyList(),
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

    val selectedAvailabilityDay: List<String> = emptyList(),
    val selectedTimeSlots: List<com.buddhatutors.model.TimeSlot> = emptyList(),
    val selectedTopics: List<com.buddhatutors.model.Topic> = emptyList(),
    val selectedLanguage: List<String> = emptyList(),

    val isRegistrationValid: Boolean = false,

    val validationResult: ValidationResult? = null,

    val isTimepickerDialogVisible: Boolean = false,
    val isTopicSelectionDialogVisible: Boolean = false,
    val isLanguageSelectionDialogVisible: Boolean = false,
    val isDaySelectionDialogVisible: Boolean = false,

    val showInitialLoader: Boolean = false,
    val showFullScreenLoader: Boolean = false

) : UiState

internal sealed class EditTutorUiEffect : UiEffect {

}
