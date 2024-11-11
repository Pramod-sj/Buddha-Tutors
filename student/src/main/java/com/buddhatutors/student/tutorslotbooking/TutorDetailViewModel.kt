package com.buddhatutors.student.tutorslotbooking

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.navigationCustomArgument
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.usecase.student.GetAllVerifiedTutorListing
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class SlotDateUiModel(
    val day: String,
    val dateString: String,
)

data class SlotTimeUiModel(
    val dateString: String,
    val startTime: String,
    val endTime: String,
    val isSlotBooked: Boolean
)


@HiltViewModel
class TutorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TutorDetailUiEvent, TutorDetailUiState, Nothing>() {

    override fun createInitialState(): TutorDetailUiState = TutorDetailUiState()

    override fun handleEvent(event: TutorDetailUiEvent) {
        when (event) {
            is TutorDetailUiEvent.SelectDate -> {

                val timeSlots = currentState.tutorListing?.let { tutorListing ->
                    generateTimeSlots(
                        dateString = event.slotDateUiModel.dateString,
                        tutorListing = tutorListing
                    )
                }.orEmpty()

                setState {
                    copy(
                        selectedDateSlot = event.slotDateUiModel,
                        timeSlots = timeSlots
                    )
                }
            }

            is TutorDetailUiEvent.SelectTimeSlot -> {
                if (event.slotTimeUiModel == currentState.selectedTimeSlot) {
                    setState { copy(selectedTimeSlot = null) }
                } else {
                    setState { copy(selectedTimeSlot = event.slotTimeUiModel) }
                }
            }
        }
    }

    private fun generateDates(tutorListing: TutorListing?): List<SlotDateUiModel> {

        val days = tutorListing?.tutor?.availabilityDay?.map {
            when (it.lowercase()) {
                "mon" -> Calendar.MONDAY
                "tue" -> Calendar.TUESDAY
                "wed" -> Calendar.WEDNESDAY
                "thu" -> Calendar.THURSDAY
                "fri" -> Calendar.FRIDAY
                "sat" -> Calendar.SATURDAY
                else -> Calendar.SUNDAY
            }
        }.orEmpty()

        val upcomingDays = mutableListOf<SlotDateUiModel>()

        val todayCalendar = Calendar.getInstance()

        while (upcomingDays.size < 7) {
            if (todayCalendar.get(Calendar.DAY_OF_WEEK) in days) {
                val formatDayString = SimpleDateFormat("EEE", Locale.ENGLISH)
                    .format(todayCalendar.time)
                val formatDateString = SimpleDateFormat("dd-MMM", Locale.ENGLISH)
                    .format(todayCalendar.time)
                upcomingDays.add(
                    SlotDateUiModel(
                        day = formatDayString,
                        dateString = formatDateString,
                    )
                )
            }
            todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return upcomingDays
    }

    private fun generateTimeSlots(
        dateString: String,
        tutorListing: TutorListing
    ): List<SlotTimeUiModel> {

        val isSlotBooked = tutorListing.bookedSlots.any {
            it.date == dateString && it.startTime == tutorListing.tutor.timeAvailability?.start.orEmpty() && it.endTime == tutorListing.tutor.timeAvailability?.end.orEmpty()
        }

        return listOf(
            SlotTimeUiModel(
                dateString = dateString,
                startTime = tutorListing.tutor.timeAvailability?.start.orEmpty(),
                endTime = tutorListing.tutor.timeAvailability?.end.orEmpty(),
                isSlotBooked = isSlotBooked
            )
        )

    }

    init {
        setState {

            val tutorListing = savedStateHandle.toRoute<StudentGraph.TutorDetail>(
                mapOf(navigationCustomArgument<TutorListing>())
            ).tutorListing

            val datesUiModels = generateDates(tutorListing)

            copy(tutorListing = tutorListing, dateSlots = datesUiModels)
        }

        currentState.dateSlots.firstOrNull()?.let { dateSlot ->
            setEvent(TutorDetailUiEvent.SelectDate(dateSlot))
        }
    }

}

data class TutorDetailUiState(
    val tutorListing: TutorListing? = null,

    val dateSlots: List<SlotDateUiModel> = emptyList(),
    val timeSlots: List<SlotTimeUiModel> = emptyList(),

    val selectedDateSlot: SlotDateUiModel? = null,
    val selectedTimeSlot: SlotTimeUiModel? = null

) : UiState


sealed class TutorDetailUiEvent : UiEvent {

    data class SelectDate(val slotDateUiModel: SlotDateUiModel) : TutorDetailUiEvent()

    data class SelectTimeSlot(val slotTimeUiModel: SlotTimeUiModel) : TutorDetailUiEvent()

}