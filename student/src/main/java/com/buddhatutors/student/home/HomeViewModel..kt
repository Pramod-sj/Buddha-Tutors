package com.buddhatutors.student.home

import androidx.lifecycle.viewModelScope
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.usecase.auth.LogoutUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val logoutUser: LogoutUser
) : com.buddhatutors.common.BaseViewModel<HomeViewModelUiEvent, HomeViewModelUiState, HomeViewModelUiEffect>() {

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
) : com.buddhatutors.common.UiState

internal sealed class HomeViewModelUiEvent : com.buddhatutors.common.UiEvent {

    data object Logout : HomeViewModelUiEvent()

}

internal sealed class HomeViewModelUiEffect : com.buddhatutors.common.UiEffect {

    data object LoggedOutSuccess : HomeViewModelUiEffect()

}