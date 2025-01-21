package com.buddhatutors.feature.admin.tutor_detail_verification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.core.navigation.navigationCustomArgument
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.usecase.GetTutorListingByTutorId
import com.buddhatutors.feature.admin.domain.usecase.UpdateTutorVerificationState
import com.buddhatutors.feature.admin.tutor_detail_verification.navigation.AdminTutorVerificationRoute
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.user.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTutorListingByTutorId: GetTutorListingByTutorId,
    private val updateTutorVerificationState: UpdateTutorVerificationState
) : BaseViewModel<TutorVerificationUiEvent, TutorVerificationUiState, TutorVerificationUiEffect>() {

    override fun createInitialState(): TutorVerificationUiState = TutorVerificationUiState()

    override fun handleEvent(event: TutorVerificationUiEvent) {
        when (event) {
            TutorVerificationUiEvent.ApproveTutorButtonClick -> {
                setEffect {
                    TutorVerificationUiEffect.ShowApprovalWarningPopup(
                        title = "Approve tutor",
                        desc = "By approving this tutor, their information and availability will be immediately visible to students.",
                        buttonLabel = "Proceed",
                        buttonClickHandler = {
                            updateVerificationStatus(true)
                        }
                    )
                }
            }

            TutorVerificationUiEvent.RejectTutorButtonClick -> {
                setEffect {
                    TutorVerificationUiEffect.ShowRejectionWarningPopup(
                        title = "Reject tutor",
                        desc = "Once rejected, this tutor will no longer be visible to students. Please confirm before proceeding.",
                        buttonLabel = "Proceed",
                        buttonClickHandler = {
                            updateVerificationStatus(false)
                        }
                    )
                }
            }
        }
    }


    private fun updateVerificationStatus(isVerified: Boolean) {
        viewModelScope.launch {
            currentState.tutorListing?.let { tutor ->
                CurrentUser.user.value?.let { user ->
                    setState { copy(isLoading = false) }
                    val resource = updateTutorVerificationState(
                        tutorListing = tutor, user = user, isApproved = isVerified
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
                                        ?: false,
                                    isTutorDataChanged = isTutorListingDataChanged(tutorListing)
                                )
                            }
                            setEffect {
                                TutorVerificationUiEffect.ShowMessage("${tutorListing.tutorUser.name} is now ${if (currentState.isApproveButtonVisible) "hidden from the tutor listing for students" else "visible in the tutor listing for students"}.")
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
            currentState.tutorListing?.tutorUser?.id?.let { tutorId ->
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

    private fun isTutorListingDataChanged(tutorListing: TutorListing?): Boolean {
        return tutorListing?.verification != ogTutorListing?.verification
    }

    private var ogTutorListing: TutorListing? = null

    init {
        setState {
            ogTutorListing = savedStateHandle.toRoute<AdminTutorVerificationRoute>(
                mapOf(navigationCustomArgument<TutorListing>())
            ).tutor
            copy(
                tutorListing = ogTutorListing,
                userType = CurrentUser.user.value?.userType ?: UserType.MASTER_TUTOR
            )
        }
        fetchTutorListingIfExisting()
    }

}

data class TutorVerificationUiState(
    val tutorListing: TutorListing? = null,

    val isLoading: Boolean = false,

    val isApproveButtonVisible: Boolean = false,
    val isRejectButtonVisible: Boolean = false,

    val isTutorDataChanged: Boolean = false,

    val userType: UserType = UserType.MASTER_TUTOR,
) : UiState


sealed class TutorVerificationUiEvent : UiEvent {

    data object ApproveTutorButtonClick : TutorVerificationUiEvent()

    data object RejectTutorButtonClick : TutorVerificationUiEvent()

}

sealed class TutorVerificationUiEffect : UiEffect {

    data class ShowApprovalWarningPopup(
        val title: String,
        val desc: String,
        val buttonLabel: String,
        val buttonClickHandler: () -> Unit
    ) : TutorVerificationUiEffect()

    data class ShowRejectionWarningPopup(
        val title: String,
        val desc: String,
        val buttonLabel: String,
        val buttonClickHandler: () -> Unit
    ) : TutorVerificationUiEffect()

    data class ShowMessage(val message: String) : TutorVerificationUiEffect()


}
