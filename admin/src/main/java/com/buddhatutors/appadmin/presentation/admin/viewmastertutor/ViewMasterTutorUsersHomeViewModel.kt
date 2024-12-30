package com.buddhatutors.appadmin.presentation.admin.viewmastertutor


import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.buddhatutors.appadmin.domain.usecase.admin.GetAllMasterTutorUsers
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
internal class ViewMasterTutorUsersHomeViewModel @Inject constructor(
    getAllMasterTutorUsers: GetAllMasterTutorUsers
) : BaseViewModel<ViewMasterTutorUiEvent, ViewMasterTutorUiState, ViewMasterTutorUiEffect>() {

    override fun createInitialState(): ViewMasterTutorUiState = ViewMasterTutorUiState()

    override fun handleEvent(event: ViewMasterTutorUiEvent) {
        when (event) {
            ViewMasterTutorUiEvent.AddMasterTutorUserFABClick -> setEffect { ViewMasterTutorUiEffect.NavigateToAddViewMasterTutorUserScreen }
        }
    }

    init {

        val tutorsPagingData: Flow<PagingData<User>> =
            getAllMasterTutorUsers().cachedIn(viewModelScope)

        setState { copy(masterTutorPagingData = tutorsPagingData) }

    }

}

internal data class ViewMasterTutorUiState(
    val masterTutorPagingData: Flow<PagingData<User>> = flowOf(PagingData.empty()),
) : UiState

internal sealed class ViewMasterTutorUiEvent : UiEvent {

    data object AddMasterTutorUserFABClick : ViewMasterTutorUiEvent()

}

internal sealed class ViewMasterTutorUiEffect : UiEffect {

    data object NavigateToAddViewMasterTutorUserScreen : ViewMasterTutorUiEffect()

}