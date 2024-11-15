package com.buddhatutors.appadmin.home

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.usecase.admin.GetAllUnverifiedTutorUsers
import com.buddhatutors.domain.usecase.auth.LogoutUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class AdminHomeViewModel @Inject constructor(
    private val logoutUser: LogoutUser,
    private val getAllUnverifiedTutorUsers: GetAllUnverifiedTutorUsers
) : BaseViewModel<AdminHomeUiEvent, AdminHomeUiState, AdminHomeUiEffect>() {


    init {
        viewModelScope.launch {
            when (val resource = getAllUnverifiedTutorUsers()) {
                is Resource.Error -> {
                    //handle error state
                }

                is Resource.Success -> {
                    setState { copy(tutorUsers = resource.data) }
                }
            }
        }
    }

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
        }
    }

}

internal data class AdminHomeUiState(
    val exampleData: String = "",
    val tutorUsers: List<TutorListing> = emptyList()
) : UiState

internal sealed class AdminHomeUiEvent : UiEvent {

    data object Logout : AdminHomeUiEvent()

    data class TutorCardClick(val tutor: TutorListing) : AdminHomeUiEvent()

}

internal sealed class AdminHomeUiEffect : UiEffect {

    data object LoggedOutSuccess : AdminHomeUiEffect()

    data class NavigateToTutorVerificationScreen(val tutor: TutorListing) : AdminHomeUiEffect()

}