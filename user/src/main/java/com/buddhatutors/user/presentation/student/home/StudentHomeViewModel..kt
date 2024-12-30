@file:OptIn(ExperimentalCoroutinesApi::class)

package com.buddhatutors.user.presentation.student.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.model.FilterOption
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.user.domain.usecase.student.GetAllVerifiedTutorListing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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
                setEffect { StudentHomeUiEffect.NavigateToTutorListingScreen(event.tutorListing) }
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
    val tutorListing: Flow<PagingData<TutorListing>> = flow { },
    val showFilterScreen: Boolean = false,
    val filterOption: FilterOption? = null
) : UiState

internal sealed class StudentHomeUiEvent : UiEvent {

    data class TutorListingItemClick(val tutorListing: TutorListing) : StudentHomeUiEvent()

    data object ProfileIconClick : StudentHomeUiEvent()

    data object FilterButtonClick : StudentHomeUiEvent()

    data object HideFilterScreen : StudentHomeUiEvent()

    data class ApplyFilterOption(val filterOption: FilterOption?) : StudentHomeUiEvent()

}

internal sealed class StudentHomeUiEffect : UiEffect {

    data class NavigateToTutorListingScreen(val tutorListing: TutorListing) : StudentHomeUiEffect()

    data object NavigateToProfileScreen : StudentHomeUiEffect()

    data object LoggedOutSuccess : StudentHomeUiEffect()

}