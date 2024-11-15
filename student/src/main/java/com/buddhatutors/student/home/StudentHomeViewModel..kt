package com.buddhatutors.student.home

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.usecase.auth.LogoutUser
import com.buddhatutors.domain.usecase.student.BookTutorSlot
import com.buddhatutors.domain.usecase.student.GetAllVerifiedTutorListing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class StudentHomeViewModel @Inject constructor(
    private val logoutUser: LogoutUser,
    private val getAllVerifiedTutorListing: GetAllVerifiedTutorListing
) : BaseViewModel<StudentHomeUiEvent, StudentHomeUiState, StudentHomeUiEffect>() {

    override fun createInitialState(): StudentHomeUiState = StudentHomeUiState()

    override fun handleEvent(event: StudentHomeUiEvent) {
        when (event) {
            StudentHomeUiEvent.Logout -> {
                viewModelScope.launch {
                    when (logoutUser()) {
                        is Resource.Error -> {

                        }

                        is Resource.Success -> {
                            setEffect { StudentHomeUiEffect.LoggedOutSuccess }
                        }
                    }
                }
            }

            is StudentHomeUiEvent.TutorListingItemClick -> {
                setEffect { StudentHomeUiEffect.NavigateToTutorListingScreen(event.tutorListing) }
            }
        }
    }

    init {
        viewModelScope.launch {
            when (val resource = getAllVerifiedTutorListing()) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    setState { copy(tutorListing = resource.data) }
                }
            }
        }
    }

}

internal data class StudentHomeUiState(
    val tutorListing: List<TutorListing> = emptyList(),
) : UiState

internal sealed class StudentHomeUiEvent : UiEvent {

    data class TutorListingItemClick(val tutorListing: TutorListing) : StudentHomeUiEvent()

    data object Logout : StudentHomeUiEvent()

}

internal sealed class StudentHomeUiEffect : UiEffect {

    data class NavigateToTutorListingScreen(val tutorListing: TutorListing) : StudentHomeUiEffect()

    data object LoggedOutSuccess : StudentHomeUiEffect()

}