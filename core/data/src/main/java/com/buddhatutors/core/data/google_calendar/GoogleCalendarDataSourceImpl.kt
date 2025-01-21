package com.buddhatutors.core.data.google_calendar

import android.util.Log
import com.buddhatutors.domain.datasource.MeetingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.meet.MeetInfo
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.model.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


internal class MeetingDataSourceImpl @Inject constructor(
    private val calendarServiceFactory: CalendarServiceFactory,
    private val calendarEventBuilder: CalendarEventBuilder
) : MeetingDataSource {

    companion object {

        internal const val PROVIDER_EMAIL_ID = "buddhatutor@gmail.com"

        const val CALENDAR_ID = "primary"

    }

    override suspend fun scheduleMeet(
        accessToken: String,
        student: User,
        tutor: User,
        bookedSlot: BookedSlot
    ): Resource<MeetInfo> {

        return withContext(Dispatchers.IO) {

            val calendarService = calendarServiceFactory.create(accessToken)

            //calendarService.events().delete("primary", "")

            // Create a new event
            val event = calendarEventBuilder.build(
                student = student,
                tutor = tutor,
                providerEmailId = PROVIDER_EMAIL_ID,
                bookedSlot = bookedSlot
            )

            // TimeBlock(timeSlot = "6:00 AM - 7:00 AM", formatDate = "12-Jun", isSelected = false) { }
            // Insert the event into the calendar
            try {

                val createdEvent =
                    calendarService.events().insert(CALENDAR_ID, event)
                        .setConferenceDataVersion(1)
                        .execute()

                val ruleUpdateResult = calendarService.acl().insert(
                    CALENDAR_ID,
                    calendarEventBuilder.buildEditPermissionRule(PROVIDER_EMAIL_ID)
                ).execute()

                println("Event created: ${createdEvent.htmlLink}")

                val meetUrl = createdEvent
                    ?.conferenceData
                    ?.entryPoints
                    ?.find { it.entryPointType == "video" }
                    ?.uri.orEmpty()

                Log.i("MEET LINK URL:", meetUrl)

                Resource.Success(
                    MeetInfo(
                        eventId = createdEvent.id,
                        meetUrl = meetUrl
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e)
            }
        }

    }

    override suspend fun cancelMeet(
        accessToken: String,
        eventId: String
    ): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            val calendarService = calendarServiceFactory.create(accessToken)
            try {
                calendarService.events().delete(CALENDAR_ID, eventId).execute()
                Resource.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e)
            }
        }
    }


}