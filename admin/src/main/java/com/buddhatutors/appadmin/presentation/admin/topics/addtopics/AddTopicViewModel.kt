package com.buddhatutors.appadmin.presentation.admin.topics.addtopics

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.domain.usecase.topic.AddTopic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddTopicViewModel @Inject constructor(
    private val addTopic: AddTopic
) : BaseViewModel<AddTopicUiEvent, AddTopicUiState, AddTopicUiEffect>() {

    override fun createInitialState(): AddTopicUiState = AddTopicUiState()

    override fun handleEvent(event: AddTopicUiEvent) {
        when (event) {
            AddTopicUiEvent.AddTopicButton -> {
                viewModelScope.launch {
                    when (addTopic(Topic(label = currentState.topicText.orEmpty()))) {
                        is Resource.Error -> {

                        }

                        is Resource.Success -> {
                            setEffect { AddTopicUiEffect.AddTopicSuccess }
                        }
                    }
                }
            }

            is AddTopicUiEvent.OnTopicTextChanged -> {
                setState {
                    copy(
                        topicText = event.topicText,
                        isTopicEnteredValid = !event.topicText.isNullOrEmpty()
                    )
                }
            }
        }
    }


}

internal data class AddTopicUiState(
    val topicText: String? = null,
    val isTopicEnteredValid: Boolean? = null
) : UiState


internal sealed class AddTopicUiEvent : UiEvent {

    data class OnTopicTextChanged(val topicText: String?) : AddTopicUiEvent()

    data object AddTopicButton : AddTopicUiEvent()

}

internal sealed class AddTopicUiEffect : UiEffect {

    data object AddTopicSuccess : AddTopicUiEffect()

}