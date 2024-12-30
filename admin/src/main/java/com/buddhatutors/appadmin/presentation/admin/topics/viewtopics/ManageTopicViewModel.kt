package com.buddhatutors.appadmin.presentation.admin.topics.viewtopics

import androidx.paging.PagingData
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.domain.usecase.topic.GetTopics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class ManageTopicViewModel @Inject constructor(
    private val getTopics: GetTopics
) :
    BaseViewModel<ManageTopicUiEvent, ManageTopicUiState, ManageTopicUiEffect>() {

    val topicsPagingData: Flow<PagingData<Topic>> = getTopics.paginated()

    override fun createInitialState(): ManageTopicUiState = ManageTopicUiState()

    override fun handleEvent(event: ManageTopicUiEvent) {
        when (event) {
            ManageTopicUiEvent.AddTopicButtonClick -> {
                setEffect { ManageTopicUiEffect.NavigateToAddTopic }
            }
        }
    }
}

internal data class ManageTopicUiState(val topics: List<Topic> = emptyList()) : UiState

internal sealed class ManageTopicUiEvent : UiEvent {

    data object AddTopicButtonClick : ManageTopicUiEvent()

}

internal sealed class ManageTopicUiEffect : UiEffect {

    data object NavigateToAddTopic : ManageTopicUiEffect()

}
