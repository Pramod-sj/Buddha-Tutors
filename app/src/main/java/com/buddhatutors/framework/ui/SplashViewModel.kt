package com.buddhatutors.framework.ui

import androidx.lifecycle.viewModelScope
import com.buddhatutors.domain.model.User.UserType.STUDENT
import com.buddhatutors.domain.model.User.UserType.TUTOR
import com.buddhatutors.domain.usecase.remoteconfig.FetchRemoteConfigUseCase
import com.buddhatutors.framework.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fetchRemoteConfigUseCase: FetchRemoteConfigUseCase
) : BaseViewModel<SplashViewModelUiEvent, SplashViewModelUiState, SplashViewModelUiEffect>() {


    override fun createInitialState(): SplashViewModelUiState {
        return SplashViewModelUiState("")
    }

    override fun handleEvent(event: SplashViewModelUiEvent) {
        when (event) {
            SplashViewModelUiEvent.InitializeApp -> {
                viewModelScope.launch {
                    fetchRemoteConfigUseCase()
                    delay(1000L)
                    when (SessionManager.user?.userType) {
                        STUDENT -> setEffect { SplashViewModelUiEffect.NavigateToStudentHome }
                        TUTOR -> setEffect { SplashViewModelUiEffect.NavigateToTutorHome }
                        else -> setEffect { SplashViewModelUiEffect.NavigateToLogin }
                    }
                }
            }
        }

    }

}


data class SplashViewModelUiState(
    val exampleData: String = "",
) : UiState

sealed class SplashViewModelUiEvent : UiEvent {

    data object InitializeApp : SplashViewModelUiEvent()

}

sealed class SplashViewModelUiEffect : UiEffect {

    data object NavigateToLogin : SplashViewModelUiEffect()

    data object NavigateToStudentHome : SplashViewModelUiEffect()

    data object NavigateToTutorHome : SplashViewModelUiEffect()

}