package com.buddhatutors.framework.ui.home

import androidx.lifecycle.viewModelScope
import com.buddhatutors.domain.usecase.LogoutUser
import com.buddhatutors.framework.Resource
import com.buddhatutors.framework.ui.BaseViewModel
import com.buddhatutors.framework.ui.UiEffect
import com.buddhatutors.framework.ui.UiEvent
import com.buddhatutors.framework.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val logoutUser: LogoutUser
) : BaseViewModel<HomeViewModelUiEvent, HomeViewModelUiState, HomeViewModelUiEffect>() {

    override fun createInitialState(): HomeViewModelUiState = HomeViewModelUiState()

    override fun handleEvent(event: HomeViewModelUiEvent) {
        when (event) {
            HomeViewModelUiEvent.Logout -> {
                viewModelScope.launch {
                    when (logoutUser()) {
                        is Resource.Error -> {

                        }

                        is Resource.Success -> {
                            setEffect { HomeViewModelUiEffect.LoggedOutSuccess }
                        }
                    }
                }
            }
        }
    }
}

internal data class HomeViewModelUiState(
    val exampleData: String = "",
) : UiState

internal sealed class HomeViewModelUiEvent : UiEvent {

    data object Logout : HomeViewModelUiEvent()

}

internal sealed class HomeViewModelUiEffect : UiEffect {

    data object LoggedOutSuccess : HomeViewModelUiEffect()

}