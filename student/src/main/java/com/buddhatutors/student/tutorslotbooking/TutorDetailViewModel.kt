package com.buddhatutors.student.tutorslotbooking

import android.app.PendingIntent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.navigationCustomArgument
import com.buddhatutors.common.utils.DateUtils
import com.buddhatutors.domain.ContextWrapper
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.GoogleScopeResolutionException
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.usecase.AuthoriseGoogleCalendarAccessUseCase
import com.buddhatutors.domain.usecase.admin.GetTutorListingByTutorId
import com.buddhatutors.domain.usecase.student.BookTutorSlot
import com.buddhatutors.student.tutorslotbooking.TutorDetailUiEvent.BookSlotButtonClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class SlotDateUiModel(
    val day: String,
    val formattedDateString: String, //dd-MMM
    val dateString: String //yyyy-MM-dd
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
    private val bookTutorSlot: BookTutorSlot,
    private val getTutorListingByTutorId: GetTutorListingByTutorId,
    private val authoriseGoogleCalendarAccessUseCase: AuthoriseGoogleCalendarAccessUseCase
) : BaseViewModel<TutorDetailUiEvent, TutorDetailUiState, TutorDetailUiEffect>() {

    companion object {
        private const val TAG = "TutorDetailViewModel"
    }

    override fun createInitialState(): TutorDetailUiState = TutorDetailUiState()

    private var slotBookingJob: Job? = null

    override fun handleEvent(event: TutorDetailUiEvent) {
        when (event) {
            is TutorDetailUiEvent.SelectDate -> {

                val timeSlots = currentState.tutorListing?.let { tutorListing ->
                    generateTimeSlots(
                        dateSlotUiModel = event.slotDateUiModel,
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

            is BookSlotButtonClick -> {
                slotBookingJob?.cancel()
                slotBookingJob = viewModelScope.launch {

                    setState { copy(showFullScreenLoader = true) }

                    val resource = authoriseGoogleCalendarAccessUseCase(event.contextWrapper)

                    when (resource) {
                        is Resource.Error -> {
                            if (resource.throwable is GoogleScopeResolutionException) {
                                val pendingIntent =
                                    ((resource.throwable as GoogleScopeResolutionException)
                                        .pendingIntent)
                                setEffect {
                                    TutorDetailUiEffect
                                        .ShowCalendarApiScopeResolutionDialog(pendingIntent)
                                }
                            } else {
                                //handle error message state
                                Log.e(TAG, "ERROR:", resource.throwable)
                                setEffect { TutorDetailUiEffect.ShowErrorMessage(resource.throwable.message.orEmpty()) }
                            }
                        }

                        is Resource.Success -> bookSlot()

                    }

                    setState { copy(showFullScreenLoader = false) }


                }


            }

            is TutorDetailUiEvent.SelectTopic -> {
                setState { copy(selectedTopic = event.topic) }
            }
        }
    }

    private suspend fun bookSlot() {
        val student = CurrentUser.user.value ?: return
        val tutor = (currentState.tutorListing) ?: return
        val resource = bookTutorSlot(
            loggedInUser = student,
            tutor = tutor,
            bookedSlot = BookedSlot(
                date = currentState.selectedDateSlot?.dateString.orEmpty(),
                startTime = currentState.selectedTimeSlot?.startTime.orEmpty(),
                endTime = currentState.selectedTimeSlot?.endTime.orEmpty(),
                bookedByStudentId = student.id,
                bookedAtDateTime = DateUtils.convertTimeInMillisToSpecifiedDateString(
                    timeInMillis = Calendar.getInstance().timeInMillis
                ),
                topic = currentState.tutorListing?.expertiseIn?.firstOrNull()
            )
        )
        when (resource) {
            is Resource.Error -> {
                //handle error case
                Log.e("TAG", "ERROR", resource.throwable)
                setEffect { TutorDetailUiEffect.ShowErrorMessage(resource.throwable.message.orEmpty()) }

            }

            is Resource.Success -> {

                setState { copy(selectedTimeSlot = null, selectedTopic = null) }

                fetchTutorListing()

                Log.i("TAG", "SUCCESS")
            }
        }
    }

    private fun generateDates(tutorListing: TutorListing?): List<SlotDateUiModel> {

        val days = tutorListing?.availableDays?.map {
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
                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    .format(todayCalendar.time)
                upcomingDays.add(
                    SlotDateUiModel(
                        day = formatDayString,
                        formattedDateString = formatDateString,
                        dateString = dateString
                    )
                )
            }
            todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return upcomingDays
    }

    private fun generateTimeSlots(
        dateSlotUiModel: SlotDateUiModel,
        tutorListing: TutorListing
    ): List<SlotTimeUiModel> {

        return tutorListing.availableTimeSlots.map { slot ->

            val isSlotBooked = tutorListing.bookedSlots.any {
                it.date == dateSlotUiModel.dateString
                        && it.startTime == slot.start.orEmpty()
                        && it.endTime == slot.end.orEmpty()
            }

            SlotTimeUiModel(
                dateString = dateSlotUiModel.formattedDateString,
                startTime = slot.start.orEmpty(),
                endTime = slot.end.orEmpty(),
                isSlotBooked = isSlotBooked
            )

        }
    }

    private fun fetchTutorListing() {
        viewModelScope.launch {

            currentState.tutorListing?.tutorUser?.id?.let {
                when (val resource = getTutorListingByTutorId(it)) {
                    is Resource.Error -> Unit

                    is Resource.Success -> {
                        setState { copy(tutorListing = resource.data) }
                    }
                }
            }

        }
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

        viewModelScope.launch {
            uiState.map { it.tutorListing }
                .collect { tutorListing ->
                    setState {
                        copy(
                            dateSlots = generateDates(tutorListing),
                            timeSlots = currentState.selectedDateSlot?.let { selectedDateSlot ->
                                tutorListing?.let { tutorListing ->
                                    generateTimeSlots(
                                        dateSlotUiModel = selectedDateSlot,
                                        tutorListing = tutorListing
                                    )
                                }.orEmpty()
                            }.orEmpty()
                        )
                    }
                }
        }


        viewModelScope.launch {
            uiState.onEach {
                setState {
                    copy(isBookSlotButtonEnabled = it.selectedDateSlot != null && it.selectedTopic != null && it.selectedTimeSlot != null)
                }
            }.launchIn(this)
        }

        //fetchTutorListing()
    }

}

data class TutorDetailUiState(
    val tutorListing: TutorListing? = null,

    val dateSlots: List<SlotDateUiModel> = emptyList(),
    val timeSlots: List<SlotTimeUiModel> = emptyList(),

    val selectedTopic: Topic? = null,
    val selectedDateSlot: SlotDateUiModel? = null,
    val selectedTimeSlot: SlotTimeUiModel? = null,

    val isBookSlotButtonEnabled: Boolean = false,

    val showFullScreenLoader: Boolean = false
) : UiState


sealed class TutorDetailUiEvent : UiEvent {

    data class SelectTopic(val topic: Topic) : TutorDetailUiEvent()

    data class SelectDate(val slotDateUiModel: SlotDateUiModel) : TutorDetailUiEvent()

    data class SelectTimeSlot(val slotTimeUiModel: SlotTimeUiModel) : TutorDetailUiEvent()

    data class BookSlotButtonClick(val contextWrapper: ContextWrapper) : TutorDetailUiEvent()

}

sealed class TutorDetailUiEffect : UiEffect {

    data class ShowCalendarApiScopeResolutionDialog(val pendingIntent: PendingIntent) :
        TutorDetailUiEffect()


    data class ShowErrorMessage(val message: String) : TutorDetailUiEffect()

}