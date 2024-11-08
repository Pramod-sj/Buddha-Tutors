package com.buddhatutors.student.tutorslotbooking

import com.buddhatutors.domain.model.TutorListing
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class SlotUiModel(
    val formatDateString: String,
    val timeInMillis: Long
)

@HiltViewModel
class TutorDetailViewModel @Inject constructor() :
    com.buddhatutors.common.BaseViewModel<TutorDetailUiEvent, TutorDetailUiState, Nothing>() {

    override fun createInitialState(): TutorDetailUiState = TutorDetailUiState()

    override fun handleEvent(event: TutorDetailUiEvent) {
        TODO("Not yet implemented")
    }

    private fun generateSlots(): List<SlotUiModel> {

        val days = currentState.tutor?.bookedSlots?.map {
            when (it.day.lowercase()) {
                "mon" -> Calendar.MONDAY
                "tue" -> Calendar.TUESDAY
                "wed" -> Calendar.WEDNESDAY
                "thu" -> Calendar.THURSDAY
                "fri" -> Calendar.FRIDAY
                "sat" -> Calendar.SATURDAY
                else -> Calendar.SUNDAY
            }
        }.orEmpty()

        val upcomingDays = mutableListOf<SlotUiModel>()

        val todayCalendar = Calendar.getInstance()

        while (upcomingDays.size < 7) {
            if (todayCalendar.get(Calendar.DAY_OF_WEEK) in days) {
                val formatDateString = SimpleDateFormat("DDD dd-MMM", Locale.ENGLISH)
                upcomingDays.add(
                    SlotUiModel(
                        formatDateString = formatDateString.format(todayCalendar),
                        timeInMillis = todayCalendar.timeInMillis
                    )
                )
            }
            todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return upcomingDays
    }

}

data class TutorDetailUiState(
    val tutor: TutorListing? = null,
    val availableDaySlot: List<SlotUiModel> = emptyList()
) : com.buddhatutors.common.UiState


sealed class TutorDetailUiEvent : com.buddhatutors.common.UiEvent {

}