package com.buddhatutors.user.presentation.student.session

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.domain.CurrentUser
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.user.domain.usecase.student.GetPastBookedSlotByStudentId
import com.buddhatutors.user.domain.usecase.student.GetUpcomingBookedSlotByStudentId
import com.buddhatutors.common.navigation.meet.MeetOpener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class SessionHomeViewModel @Inject constructor(
    private val getUpcomingBookedSlotByStudentId: GetUpcomingBookedSlotByStudentId,
    private val getPastBookedSlotByStudentId: GetPastBookedSlotByStudentId,
    private val meetOpener: MeetOpener
) : BaseViewModel<SessionHomeUiEvent, SessionHomeUiState, SessionHomeUiEffect>() {

    override fun createInitialState(): SessionHomeUiState = SessionHomeUiState()

    override fun handleEvent(event: SessionHomeUiEvent) {
        when (event) {

            is SessionHomeUiEvent.BookedSlotItemClick -> {

            }

            is SessionHomeUiEvent.OpenMeetClick -> {
                event.context.get()?.let {
                    meetOpener.open(it, event.bookedSlot.meetInfo?.meetUrl.orEmpty())
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            CurrentUser.user.value?.id?.let { userId ->

                setState { copy(showLoaderForUpcomingScreen = true) }

                delay(2000L)

                when (val resource = getUpcomingBookedSlotByStudentId(userId)) {
                    is Resource.Error -> {

                    }

                    is Resource.Success -> {
                        setState { copy(upcomingBookedSlots = resource.data) }
                    }
                }

                setState {
                    copy(
                        showLoaderForUpcomingScreen = false,
                        showLoaderForPastScreen = true
                    )
                }

                delay(1000L)

                when (val resource = getPastBookedSlotByStudentId(userId)) {
                    is Resource.Error -> {

                    }

                    is Resource.Success -> {
                        setState { copy(pastBookedSlots = resource.data) }
                    }
                }

                setState {
                    copy(
                        showLoaderForUpcomingScreen = false,
                        showLoaderForPastScreen = false
                    )
                }


            }
        }
    }

}

internal data class SessionHomeUiState(
    val showLoaderForUpcomingScreen: Boolean = false,
    val showLoaderForPastScreen: Boolean = false,
    val upcomingBookedSlots: List<BookedSlot> = emptyList(),
    val pastBookedSlots: List<BookedSlot> = emptyList(),
) : UiState

internal sealed class SessionHomeUiEvent : UiEvent {

    data class BookedSlotItemClick(val bookedSlot: BookedSlot) : SessionHomeUiEvent()

    data class OpenMeetClick(val context: WeakReference<Context>, val bookedSlot: BookedSlot) :
        SessionHomeUiEvent()

}

internal sealed class SessionHomeUiEffect : UiEffect {

}