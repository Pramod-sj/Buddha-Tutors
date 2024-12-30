package com.buddhatutors.ui.splash

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.CurrentUser
import com.buddhatutors.common.domain.model.user.UserType
import com.buddhatutors.common.domain.usecase.remoteconfig.FetchRemoteConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fetchRemoteConfigUseCase: FetchRemoteConfigUseCase
) : BaseViewModel<SplashViewModelUiEvent, SplashViewModelUiState, SplashViewModelUiEffect>() {


    override fun createInitialState(): SplashViewModelUiState = SplashViewModelUiState

    override fun handleEvent(event: SplashViewModelUiEvent) {
        when (event) {
            SplashViewModelUiEvent.InitializeApp -> {
                viewModelScope.launch {
                    fetchRemoteConfigUseCase()
                    delay(1000L)
                    when (CurrentUser.user.value?.userType) {
                        UserType.STUDENT -> setEffect { SplashViewModelUiEffect.NavigateToStudentFlow }
                        UserType.TUTOR -> setEffect { SplashViewModelUiEffect.NavigateToTutorHomeFlow }
                        UserType.ADMIN -> setEffect { SplashViewModelUiEffect.NavigateToAdminFlow }
                        UserType.MASTER_TUTOR -> setEffect { SplashViewModelUiEffect.NavigateToMasterTutorFlow }
                        else -> setEffect { SplashViewModelUiEffect.NavigateToAuthFlow }
                    }
                }
            }
        }

    }

}


data object SplashViewModelUiState : UiState

sealed class SplashViewModelUiEvent : UiEvent {

    data object InitializeApp : SplashViewModelUiEvent()

}

sealed class SplashViewModelUiEffect : UiEffect {

    data object NavigateToAuthFlow : SplashViewModelUiEffect()

    data object NavigateToStudentFlow : SplashViewModelUiEffect()

    data object NavigateToTutorHomeFlow : SplashViewModelUiEffect()

    data object NavigateToAdminFlow : SplashViewModelUiEffect()

    data object NavigateToMasterTutorFlow : SplashViewModelUiEffect()

}