package com.buddhatutors.feature.student.home.home.filter

import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.model.FilterOption
import com.buddhatutors.model.Resource
import com.buddhatutors.model.Topic
import com.buddhatutors.domain.usecase.GetLanguages
import com.buddhatutors.domain.usecase.topic.GetTopics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TutorFilterViewModel @Inject constructor(
    private val getTopics: GetTopics,
    private val getLanguages: GetLanguages
) : BaseViewModel<TutorFilterUiEvent, TutorFilterUiState, TutorFilterUiEffect>() {

    override fun createInitialState(): TutorFilterUiState = TutorFilterUiState()

    override fun handleEvent(event: TutorFilterUiEvent) {
        when (event) {
            is TutorFilterUiEvent.ApplyFilter -> {
                setEffect { TutorFilterUiEffect.FilterApplied(currentState.filterOption) }
            }

            TutorFilterUiEvent.ClearFilter -> {
                setState { copy(filterOption = null) }
            }

            is TutorFilterUiEvent.AddTopicFilter -> {
                addTopic(event.topic)
            }

            is TutorFilterUiEvent.RemoveTopicFilter -> {
                removeTopic(event.topic)
            }

            is TutorFilterUiEvent.AddLanguageFilter -> {
                addLanguage(event.language)
            }

            is TutorFilterUiEvent.RemoveLanguageFilter -> {
                removeLanguage(event.language)
            }

            is TutorFilterUiEvent.SetDefaultFilters -> {
                setState { copy(filterOption = event.filterOption) }
            }
        }
    }

    // Helper methods
    private fun addTopic(topic: Topic) {
        val updatedTopics = currentState.filterOption?.topics?.toMutableList() ?: mutableListOf()
        if (topic !in updatedTopics) {
            updatedTopics.add(topic)
        }
        setState {
            copy(
                filterOption = currentState.filterOption?.copy(topics = updatedTopics)
                    ?: FilterOption(topics = updatedTopics)
            )
        }
    }

    private fun removeTopic(topic: Topic) {
        val updatedTopics = currentState.filterOption?.topics?.toMutableList() ?: mutableListOf()
        updatedTopics.remove(topic)
        setState {
            copy(filterOption = currentState.filterOption?.copy(topics = updatedTopics))
        }
    }

    private fun addLanguage(language: String) {
        val updatedLanguages =
            currentState.filterOption?.languages?.toMutableList() ?: mutableListOf()
        if (language !in updatedLanguages) {
            updatedLanguages.add(language)
        }
        setState {
            copy(
                filterOption = currentState.filterOption?.copy(languages = updatedLanguages)
                    ?: FilterOption(languages = updatedLanguages)
            )
        }
    }

    private fun removeLanguage(language: String) {
        val updatedLanguages =
            currentState.filterOption?.languages?.toMutableList() ?: mutableListOf()
        updatedLanguages.remove(language)
        setState {
            copy(filterOption = currentState.filterOption?.copy(languages = updatedLanguages))
        }
    }

    init {
        viewModelScope.launch {
            when (val resource = getTopics()) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    setState { copy(topics = resource.data) }
                }
            }

            when (val resource = getLanguages()) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    setState { copy(languages = resource.data) }
                }
            }
        }
    }

}

internal data class TutorFilterUiState(
    val topics: List<Topic> = emptyList(),
    val languages: List<String> = emptyList(),

    val filterOption: FilterOption? = null
) : UiState

internal sealed class TutorFilterUiEvent : UiEvent {

    data class SetDefaultFilters(val filterOption: FilterOption?) :
        TutorFilterUiEvent()


    data class AddTopicFilter(val topic: Topic) : TutorFilterUiEvent()
    data class RemoveTopicFilter(val topic: Topic) : TutorFilterUiEvent()

    data class AddLanguageFilter(val language: String) : TutorFilterUiEvent()
    data class RemoveLanguageFilter(val language: String) : TutorFilterUiEvent()

    data object ApplyFilter : TutorFilterUiEvent()

    data object ClearFilter : TutorFilterUiEvent()

}

internal sealed class TutorFilterUiEffect : UiEffect {

    data class FilterApplied(val filterOption: FilterOption?) :
        TutorFilterUiEffect()

}