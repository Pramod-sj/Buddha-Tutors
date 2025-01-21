package com.buddhatutors.feature.student.slot_booking

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.buddhatutors.common.BaseViewModel
import com.buddhatutors.common.UiEffect
import com.buddhatutors.common.UiEvent
import com.buddhatutors.common.UiState
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageHelper
import com.buddhatutors.common.utils.DateUtils
import com.buddhatutors.core.auth.google_oauth.AuthoriseGoogleCalendarAccessHelper
import com.buddhatutors.core.auth.google_oauth.google_scope_auth.GoogleScopeResolutionException
import com.buddhatutors.core.navigation.navigationCustomArgument
import com.buddhatutors.domain.usecase.tutor.GetUpcomingBookedSlotByTutorId
import com.buddhatutors.feature.student.domain.BookTutorSlot
import com.buddhatutors.feature.student.slot_booking.TutorDetailUiEvent.BookSlotButtonClick
import com.buddhatutors.feature.student.slot_booking.navigation.TutorDetailRoute
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
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
internal class TutorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookTutorSlot: BookTutorSlot,
    private val getUpcomingBookedSlotByTutorId: GetUpcomingBookedSlotByTutorId,
    private val authoriseGoogleCalendarAccessHelper: AuthoriseGoogleCalendarAccessHelper
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
                        tutorListing = tutorListing,
                        bookedSlotList = currentState.bookedSlotList
                    )
                }.orEmpty()

                setState {
                    copy(
                        selectedDateSlot = event.slotDateUiModel,
                        timeSlots = timeSlots,
                        selectedTimeSlot = null
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

                    val activity = event.activity.get() ?: return@launch

                    setState { copy(showFullScreenLoader = true) }

                    val resource = authoriseGoogleCalendarAccessHelper.startAuthProcess(activity)

                    when (resource) {
                        is Resource.Error -> {
                            if (resource.throwable is GoogleScopeResolutionException) {
                                val pendingIntent =
                                    ((resource.throwable as GoogleScopeResolutionException).pendingIntent)
                                setEffect {
                                    TutorDetailUiEffect.ShowCalendarApiScopeResolutionDialog(
                                        pendingIntent
                                    )
                                }
                            } else {
                                //handle error message state
                                Log.e(TAG, "ERROR:", resource.throwable)
                                MessageHelper.showMessage(Message.Warning(resource.throwable.message.orEmpty()))
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
        val student = com.buddhatutors.domain.CurrentUser.user.value ?: return
        val tutor = (currentState.tutorListing) ?: return
        val resource = bookTutorSlot(
            loggedInUser = student,
            tutor = tutor,
            bookedSlot = com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot(
                id = "",//unknown for now
                date = currentState.selectedDateSlot?.dateString.orEmpty(),
                startTime = currentState.selectedTimeSlot?.startTime.orEmpty(),
                endTime = currentState.selectedTimeSlot?.endTime.orEmpty(),
                bookedByStudentId = student.id,
                bookedAtDateTime = DateUtils.convertTimeInMillisToSpecifiedDateString(
                    timeInMillis = Calendar.getInstance().timeInMillis
                ),
                topic = currentState.selectedTopic,
                tutorId = tutor.tutorUser.id,
                bookedByStudent = com.buddhatutors.model.tutorlisting.slotbooking.StudentInfo(
                    id = student.id,
                    name = student.name
                ),
                tutorInfo = com.buddhatutors.model.tutorlisting.slotbooking.TutorInfo(
                    id = tutor.tutorUser.id,
                    name = tutor.tutorUser.name
                ),
                meetInfo = null
            )
        )
        when (resource) {
            is Resource.Error -> {
                //handle error case
                Log.e("TAG", "ERROR", resource.throwable)
                MessageHelper.showMessage(Message.Warning(resource.throwable.message.orEmpty()))
            }

            is Resource.Success -> {

                setState { copy(selectedTimeSlot = null, selectedTopic = null) }

                fetchTutorBookedSlots()

                MessageHelper.showMessage(Message.Success("Successfully booked the slot"))

                Log.i("TAG", "SUCCESS")
            }
        }
    }

    private fun generateDates(tutorListing: com.buddhatutors.model.tutorlisting.TutorListing?): List<SlotDateUiModel> {

        val days = tutorListing?.availableDays?.map {
            when (it) {
                com.buddhatutors.domain.Constant.MONDAY -> Calendar.MONDAY
                com.buddhatutors.domain.Constant.TUESDAY -> Calendar.TUESDAY
                com.buddhatutors.domain.Constant.WEDNESDAY -> Calendar.WEDNESDAY
                com.buddhatutors.domain.Constant.THURSDAY -> Calendar.THURSDAY
                com.buddhatutors.domain.Constant.FRIDAY -> Calendar.FRIDAY
                com.buddhatutors.domain.Constant.SATURDAY -> Calendar.SATURDAY
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
                        day = if (DateUtils.isToday(todayCalendar)) "Today" else formatDayString,
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
        tutorListing: com.buddhatutors.model.tutorlisting.TutorListing,
        bookedSlotList: List<com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot>
    ): List<SlotTimeUiModel> {

        return tutorListing.availableTimeSlots.mapNotNull { slot ->

            val dateTimeString = dateSlotUiModel.dateString + " " + slot.start

            val timeInMillis = DateUtils.convertSpecifiedDateStringToTimeInMillis(
                dateString = dateTimeString,
                dateFormat = "yyyy-MM-dd hh:mm a",
            )

            if (timeInMillis > System.currentTimeMillis()) {

                val isSlotBooked = bookedSlotList.any {
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
            } else null
        }
    }

    private fun fetchTutorBookedSlots() {
        viewModelScope.launch {

            currentState.tutorListing?.tutorUser?.id?.let {
                when (val resource = getUpcomingBookedSlotByTutorId(it)) {
                    is Resource.Error -> Unit

                    is Resource.Success -> {
                        setState {
                            copy(bookedSlotList = resource.data)
                        }
                    }
                }
            }

        }
    }

    init {
        setState {

            val tutorListing = savedStateHandle.toRoute<TutorDetailRoute>(
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
                                        tutorListing = tutorListing,
                                        bookedSlotList = currentState.bookedSlotList
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

        fetchTutorBookedSlots()
    }

}

data class TutorDetailUiState(
    val tutorListing: com.buddhatutors.model.tutorlisting.TutorListing? = null,
    val bookedSlotList: List<com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot> = emptyList(),

    val dateSlots: List<SlotDateUiModel> = emptyList(),
    val timeSlots: List<SlotTimeUiModel> = emptyList(),

    val selectedTopic: com.buddhatutors.model.Topic? = null,
    val selectedDateSlot: SlotDateUiModel? = null,
    val selectedTimeSlot: SlotTimeUiModel? = null,

    val isBookSlotButtonEnabled: Boolean = false,

    val showFullScreenLoader: Boolean = false
) : UiState


sealed class TutorDetailUiEvent : UiEvent {

    data class SelectTopic(val topic: com.buddhatutors.model.Topic) : TutorDetailUiEvent()

    data class SelectDate(val slotDateUiModel: SlotDateUiModel) : TutorDetailUiEvent()

    data class SelectTimeSlot(val slotTimeUiModel: SlotTimeUiModel) : TutorDetailUiEvent()

    data class BookSlotButtonClick(val activity: WeakReference<Activity>) : TutorDetailUiEvent()

}

sealed class TutorDetailUiEffect : UiEffect {

    data class ShowCalendarApiScopeResolutionDialog(val pendingIntent: PendingIntent) :
        TutorDetailUiEffect()

}