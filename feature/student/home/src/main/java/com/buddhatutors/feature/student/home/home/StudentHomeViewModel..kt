package com.buddhatutors.feature.student.home.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.feature.student.domain.GetAllVerifiedTutorListing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class StudentHomeViewModel @Inject constructor(
    private val getAllVerifiedTutorListing: GetAllVerifiedTutorListing
) : BaseViewModel<StudentHomeUiEvent, StudentHomeUiState, StudentHomeUiEffect>() {

    override fun createInitialState(): StudentHomeUiState = StudentHomeUiState()

    override fun handleEvent(event: StudentHomeUiEvent) {
        when (event) {

            is StudentHomeUiEvent.TutorListingItemClick -> {
                setEffect { StudentHomeUiEffect.NavigateToTutorDetailScreen(event.tutorListing) }
            }

            StudentHomeUiEvent.ProfileIconClick -> {
                setEffect { StudentHomeUiEffect.NavigateToProfileScreen }
            }

            StudentHomeUiEvent.FilterButtonClick -> {
                setState { copy(showFilterScreen = true) }
            }

            StudentHomeUiEvent.HideFilterScreen -> {
                setState { copy(showFilterScreen = false) }
            }

            is StudentHomeUiEvent.ApplyFilterOption -> {
                setEvent(StudentHomeUiEvent.HideFilterScreen)
                setState { copy(filterOption = event.filterOption) }
            }
        }
    }

    init {
        viewModelScope.launch {
            uiState.map { it.filterOption }
                .distinctUntilChanged()
                .collect { filterOption ->
                    setState {
                        copy(
                            tutorListing = getAllVerifiedTutorListing(filterOption)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
        }
    }

}

internal data class StudentHomeUiState(
    val tutorListing: Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> = flow { },
    val showFilterScreen: Boolean = false,
    val filterOption: com.buddhatutors.model.FilterOption? = null
) : UiState

internal sealed class StudentHomeUiEvent : UiEvent {

    data class TutorListingItemClick(val tutorListing: com.buddhatutors.model.tutorlisting.TutorListing) :
        StudentHomeUiEvent()

    data object ProfileIconClick : StudentHomeUiEvent()

    data object FilterButtonClick : StudentHomeUiEvent()

    data object HideFilterScreen : StudentHomeUiEvent()

    data class ApplyFilterOption(val filterOption: com.buddhatutors.model.FilterOption?) :
        StudentHomeUiEvent()

}

internal sealed class StudentHomeUiEffect : UiEffect {

    data class NavigateToTutorDetailScreen(val tutorListing: com.buddhatutors.model.tutorlisting.TutorListing) :
        StudentHomeUiEffect()

    data object NavigateToProfileScreen : StudentHomeUiEffect()

    data object LoggedOutSuccess : StudentHomeUiEffect()

}