package com.buddhatutors.ui.splash

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.model.user.UserType.ADMIN
import com.buddhatutors.domain.model.user.UserType.MASTER_TUTOR
import com.buddhatutors.domain.model.user.UserType.STUDENT
import com.buddhatutors.domain.model.user.UserType.TUTOR
import com.buddhatutors.domain.usecase.remoteconfig.FetchRemoteConfigUseCase
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
                        STUDENT -> setEffect { SplashViewModelUiEffect.NavigateToStudentFlow }
                        TUTOR -> setEffect { SplashViewModelUiEffect.NavigateToTutorHomeFlow }
                        ADMIN -> setEffect { SplashViewModelUiEffect.NavigateToAdminFlow }
                        MASTER_TUTOR -> setEffect { SplashViewModelUiEffect.NavigateToMasterTutorFlow }
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