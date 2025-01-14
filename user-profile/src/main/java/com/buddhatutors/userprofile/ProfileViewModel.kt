package com.buddhatutors.userprofile

import androidx.lifecycle.viewModelScope
import com.buddhatutors.auth.domain.usecase.LogoutUser
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.CurrentUser
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val logoutUser: LogoutUser
) : BaseViewModel<ProfileUiEvent, ProfileUiState, ProfileUiEffect>() {

    override fun createInitialState(): ProfileUiState = ProfileUiState()

    override fun handleEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.LogoutClick -> {
                viewModelScope.launch {
                    val userType = currentState.user?.userType?.id ?: -1
                    when (val resource = logoutUser()) {
                        is Resource.Error -> {
                            setEffect {
                                ProfileUiEffect.ShowMessage(resource.throwable.message.orEmpty())
                            }
                        }

                        is Resource.Success -> {
                            setEffect { ProfileUiEffect.NavigateToLoginPage(userType) }
                        }
                    }
                }
            }

            ProfileUiEvent.UpdateTutorAvailability -> {
                setEffect { ProfileUiEffect.NavigateToEditTutorAvailability }
            }
        }

    }

    init {
        setState { copy(user = CurrentUser.user.value) }
    }
}


data class ProfileUiState(
    val user: User? = null
) : UiState

sealed class ProfileUiEvent : UiEvent {

    data object LogoutClick : ProfileUiEvent()

    data object UpdateTutorAvailability : ProfileUiEvent()

}

sealed class ProfileUiEffect : UiEffect {

    data class ShowMessage(val message: String) : ProfileUiEffect()

    data class NavigateToLoginPage(val useType: Int) : ProfileUiEffect()

    data object NavigateToEditTutorAvailability : ProfileUiEffect()

}


