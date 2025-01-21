package com.buddhatutors.feature.admin.master_home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.core.auth.domain.usecase.LogoutUser
import com.buddhatutors.feature.admin.domain.usecase.GetPaginatedAllTutorUsersByMasterId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class MasterTutorHomeViewModel @Inject constructor(
    private val logoutUser: LogoutUser,
    private val getPaginatedAllTutorUsersByMasterId: GetPaginatedAllTutorUsersByMasterId
) : BaseViewModel<MasterTutorHomeUiEvent, MasterTutorHomeUiState, MasterTutorHomeUiEffect>() {

    override fun createInitialState(): MasterTutorHomeUiState = MasterTutorHomeUiState()

    override fun handleEvent(event: MasterTutorHomeUiEvent) {
        when (event) {
            MasterTutorHomeUiEvent.Logout -> {
                viewModelScope.launch {
                    when (logoutUser()) {
                        is com.buddhatutors.model.Resource.Error -> {

                        }

                        is com.buddhatutors.model.Resource.Success -> {
                            setEffect { MasterTutorHomeUiEffect.LoggedOutSuccess }
                        }
                    }
                }
            }

            is MasterTutorHomeUiEvent.TutorCardClick -> {
                setEffect { MasterTutorHomeUiEffect.NavigateToTutorVerificationScreen(event.tutor) }
            }

            MasterTutorHomeUiEvent.LogoutClick -> {
                setEffect { MasterTutorHomeUiEffect.NavigateToProfileScreen }
            }

            MasterTutorHomeUiEvent.ProfileIconClick -> {
                setEffect { MasterTutorHomeUiEffect.NavigateToProfileScreen }
            }

            MasterTutorHomeUiEvent.AddUserFABClick -> setEffect { MasterTutorHomeUiEffect.NavigateToAddUserScreen }

            MasterTutorHomeUiEvent.RefreshHome -> {
                //This event is already handle in UI using Paging3
            }
        }
    }

    init {
        val tutorsPagingData: Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> =
            getPaginatedAllTutorUsersByMasterId(com.buddhatutors.domain.CurrentUser.user.value?.id.orEmpty())
                .cachedIn(viewModelScope)
        setState { copy(tutorsPagingData = tutorsPagingData) }
    }
}

internal data class MasterTutorHomeUiState(
    val tutorsPagingData: Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> = flowOf(PagingData.empty()),
) : UiState

internal sealed class MasterTutorHomeUiEvent : UiEvent {

    data object Logout : MasterTutorHomeUiEvent()

    data class TutorCardClick(val tutor: com.buddhatutors.model.tutorlisting.TutorListing) : MasterTutorHomeUiEvent()

    data object LogoutClick : MasterTutorHomeUiEvent()

    data object ProfileIconClick : MasterTutorHomeUiEvent()

    data object AddUserFABClick : MasterTutorHomeUiEvent()

    data object RefreshHome : MasterTutorHomeUiEvent()

}

internal sealed class MasterTutorHomeUiEffect : UiEffect {

    data object LoggedOutSuccess : MasterTutorHomeUiEffect()

    data class NavigateToTutorVerificationScreen(val tutor: com.buddhatutors.model.tutorlisting.TutorListing) :
        MasterTutorHomeUiEffect()

    data object NavigateToProfileScreen : MasterTutorHomeUiEffect()

    data object NavigateToAddUserScreen : MasterTutorHomeUiEffect()

}