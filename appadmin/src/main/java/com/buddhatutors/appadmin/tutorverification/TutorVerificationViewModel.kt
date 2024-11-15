package com.buddhatutors.appadmin.tutorverification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.navigationCustomArgument
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.usecase.admin.GetTutorListingByTutorId
import com.buddhatutors.domain.usecase.admin.UpdateTutorVerificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTutorListingByTutorId: GetTutorListingByTutorId,
    private val updateTutorVerificationState: UpdateTutorVerificationState
) : BaseViewModel<TutorDetailUiEvent, TutorVerificationUiState, Nothing>() {

    override fun createInitialState(): TutorVerificationUiState = TutorVerificationUiState()

    override fun handleEvent(event: TutorDetailUiEvent) {
        when (event) {
            TutorDetailUiEvent.ApproveTutorButtonClick -> {
                updateVerificationStatus(true)
            }

            TutorDetailUiEvent.RejectTutorButtonClick -> {
                updateVerificationStatus(false)
            }
        }
    }


    private fun updateVerificationStatus(isVerified: Boolean) {
        viewModelScope.launch {
            currentState.tutor?.let { tutor ->
                CurrentUser.user.value?.let { user ->
                    setState { copy(isLoading = false) }
                    val resource = updateTutorVerificationState(
                        tutor = tutor, user = user, isApproved = isVerified
                    )
                    when (resource) {
                        is Resource.Error -> {

                        }

                        is Resource.Success -> {
                            val tutorListing = resource.data
                            setState {
                                copy(
                                    tutorListing = tutorListing,
                                    isApproveButtonVisible = !(tutorListing.verification?.isApproved
                                        ?: false),
                                    isRejectButtonVisible = tutorListing.verification?.isApproved
                                        ?: false
                                )
                            }
                        }
                    }
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun fetchTutorListingIfExisting() {
        viewModelScope.launch {
            currentState.tutor?.id?.let { tutorId ->
                when (val resource = getTutorListingByTutorId(tutorId)) {
                    is Resource.Error -> {
                        setState {
                            copy(
                                tutorListing = null,
                                isApproveButtonVisible = true,
                                isRejectButtonVisible = true
                            )
                        }
                    }

                    is Resource.Success -> {
                        val tutorListing = resource.data
                        setState {
                            copy(
                                tutorListing = tutorListing,
                                isApproveButtonVisible = !(tutorListing.verification?.isApproved
                                    ?: false),
                                isRejectButtonVisible = tutorListing.verification?.isApproved
                                    ?: false
                            )
                        }
                    }
                }
            }
        }
    }

    init {
        setState {
            val tutor = savedStateHandle.toRoute<AdminGraph.AdminTutorVerification>(
                mapOf(navigationCustomArgument<Tutor>())
            ).tutor
            copy(tutor = tutor)
        }
        fetchTutorListingIfExisting()
    }

}

data class TutorVerificationUiState(
    val tutor: Tutor? = null, val tutorListing: TutorListing? = null,

    val isLoading: Boolean = false,

    val isApproveButtonVisible: Boolean = false, val isRejectButtonVisible: Boolean = false
) : UiState


sealed class TutorDetailUiEvent : UiEvent {

    data object ApproveTutorButtonClick : TutorDetailUiEvent()

    data object RejectTutorButtonClick : TutorDetailUiEvent()

}