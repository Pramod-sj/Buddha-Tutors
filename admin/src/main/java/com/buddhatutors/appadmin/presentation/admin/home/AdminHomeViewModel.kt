package com.buddhatutors.appadmin.presentation.admin.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buddhatutors.auth.domain.usecase.LogoutUser
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.appadmin.domain.usecase.admin.GetPaginatedAllTutorUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class AdminHomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val logoutUser: LogoutUser,
    private val getPaginatedAllTutorUsers: GetPaginatedAllTutorUsers
) : BaseViewModel<AdminHomeUiEvent, AdminHomeUiState, AdminHomeUiEffect>() {

    val tutorsPagingData: Flow<PagingData<TutorListing>> =
        getPaginatedAllTutorUsers().cachedIn(viewModelScope)

    override fun createInitialState(): AdminHomeUiState = AdminHomeUiState()

    override fun handleEvent(event: AdminHomeUiEvent) {
        when (event) {
            AdminHomeUiEvent.Logout -> {
                viewModelScope.launch {
                    when (logoutUser()) {
                        is Resource.Error -> {

                        }

                        is Resource.Success -> {
                            setEffect { AdminHomeUiEffect.LoggedOutSuccess }
                        }
                    }
                }
            }

            is AdminHomeUiEvent.TutorCardClick -> {
                setEffect { AdminHomeUiEffect.NavigateToTutorVerificationScreen(event.tutor) }
            }

            AdminHomeUiEvent.LogoutClick -> {
                setEffect { AdminHomeUiEffect.NavigateToProfileScreen }
            }

            AdminHomeUiEvent.ProfileIconClick -> {
                setEffect { AdminHomeUiEffect.NavigateToProfileScreen }
            }

            AdminHomeUiEvent.AddUserFABClick -> setEffect { AdminHomeUiEffect.NavigateToAddUserScreen }

            AdminHomeUiEvent.RefreshHome -> {
                //This event is already handle in UI using Paging3
            }
        }
    }

}

internal data class AdminHomeUiState(
    val exampleData: String = "",
) : UiState

internal sealed class AdminHomeUiEvent : UiEvent {

    data object Logout : AdminHomeUiEvent()

    data class TutorCardClick(val tutor: TutorListing) : AdminHomeUiEvent()

    data object LogoutClick : AdminHomeUiEvent()

    data object ProfileIconClick : AdminHomeUiEvent()

    data object AddUserFABClick : AdminHomeUiEvent()

    data object RefreshHome : AdminHomeUiEvent()

}

internal sealed class AdminHomeUiEffect : UiEffect {

    data object LoggedOutSuccess : AdminHomeUiEffect()

    data class NavigateToTutorVerificationScreen(val tutor: TutorListing) : AdminHomeUiEffect()

    data object NavigateToProfileScreen : AdminHomeUiEffect()

    data object NavigateToAddUserScreen : AdminHomeUiEffect()

}