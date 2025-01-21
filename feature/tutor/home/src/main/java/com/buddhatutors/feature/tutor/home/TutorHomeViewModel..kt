package com.buddhatutors.feature.tutor.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.core.meet.MeetOpener
import com.buddhatutors.domain.usecase.tutor.GetPastBookedSlotByTutorId
import com.buddhatutors.domain.usecase.tutor.GetUpcomingBookedSlotByTutorId
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class TutorHomeViewModel @Inject constructor(
    private val getUpcomingBookedSlotByTutorId: GetUpcomingBookedSlotByTutorId,
    private val getPastBookedSlotByTutorId: GetPastBookedSlotByTutorId,
    private val meetOpener: MeetOpener
) : BaseViewModel<TutorHomeUiEvent, TutorHomeUiState, TutorHomeUiEffect>() {

    override fun createInitialState(): TutorHomeUiState = TutorHomeUiState()

    override fun handleEvent(event: TutorHomeUiEvent) {
        when (event) {

            is TutorHomeUiEvent.BookedSlotItemClick -> {
                setEffect { TutorHomeUiEffect.NavigateToBookingDetail(event.bookedSlot) }
            }

            TutorHomeUiEvent.ProfileIconClick -> {
                setEffect { TutorHomeUiEffect.NavigateToProfileScreen }
            }

            is TutorHomeUiEvent.OpenMeetClick -> {
                event.context.get()?.let {
                    meetOpener.open(it, event.bookedSlot.meetInfo?.meetUrl.orEmpty())
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            com.buddhatutors.domain.CurrentUser.user.value?.id?.let { userId ->

                setState { copy(showLoaderForUpcomingScreen = true) }

                delay(1000L)

                when (val resource = getUpcomingBookedSlotByTutorId(userId)) {
                    is com.buddhatutors.model.Resource.Error -> {

                    }

                    is com.buddhatutors.model.Resource.Success -> {
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

                when (val resource = getPastBookedSlotByTutorId(userId)) {
                    is com.buddhatutors.model.Resource.Error -> {

                    }

                    is com.buddhatutors.model.Resource.Success -> {
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

internal data class TutorHomeUiState(
    val showLoaderForUpcomingScreen: Boolean = false,
    val showLoaderForPastScreen: Boolean = false,
    val upcomingBookedSlots: List<BookedSlot> = emptyList(),
    val pastBookedSlots: List<BookedSlot> = emptyList(),
) : UiState

internal sealed class TutorHomeUiEvent : UiEvent {

    data class BookedSlotItemClick(val bookedSlot: BookedSlot) : TutorHomeUiEvent()

    data object ProfileIconClick : TutorHomeUiEvent()

    data class OpenMeetClick(val context: WeakReference<Context>, val bookedSlot: BookedSlot) :
        TutorHomeUiEvent()

}

internal sealed class TutorHomeUiEffect : UiEffect {

    data class NavigateToBookingDetail(val bookedSlot: BookedSlot) : TutorHomeUiEffect()

    data object NavigateToProfileScreen : TutorHomeUiEffect()

    data object LoggedOutSuccess : TutorHomeUiEffect()

}