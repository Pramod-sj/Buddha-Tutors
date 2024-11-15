package com.buddhatutors.data.datasourceimpl

import android.content.Context
import android.util.Log
import com.buddhatutors.common.utils.DateUtils
import com.buddhatutors.data.R
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.datasource.MeetingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.meet.MeetInfo
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.User
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.AclRule
import com.google.api.services.calendar.model.ConferenceData
import com.google.api.services.calendar.model.ConferenceSolutionKey
import com.google.api.services.calendar.model.CreateConferenceRequest
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

const val PROVIDER_EMAIL_ID = "buddhatutor@gmail.com"

class CalendarServiceFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun create(accessToken: String): Calendar {
        val jsonFactory = GsonFactory.getDefaultInstance()
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val credentials: GoogleCredentials =
            GoogleCredentials.create(AccessToken.newBuilder().setTokenValue(accessToken).build())

        return Calendar.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName(context.getString(R.string.app_name))
            .build()
    }
}

class CalendarEventBuilder @Inject constructor() {

    private fun toIndianTimeString(date: String, time: String): String {
        val timeHHmm = DateUtils.convertDateStringToSpecifiedDateString(
            dateString = time,
            dateFormat = "hh:mm a",
            requiredDateFormat = "HH:mm:ss"
        )
        return "${date}T${timeHHmm}+05:30"
    }

    fun build(student: User, tutor: User, bookedSlot: BookedSlot): Event {
        return Event().apply {
            summary = "BuddhaTutors: Session with ${tutor.name}"
            description =
                "This meeting is schedule for a learning session on ${bookedSlot.topic?.label}"
            start = EventDateTime().apply {
                dateTime = DateTime(toIndianTimeString(bookedSlot.date, bookedSlot.startTime))
                timeZone = "Asia/Kolkata"
            }
            end = EventDateTime().apply {
                dateTime = DateTime(toIndianTimeString(bookedSlot.date, bookedSlot.endTime))
                timeZone = "Asia/Kolkata"
            }
            attendees = listOf(
                PROVIDER_EMAIL_ID,
                student.email,
                tutor.email
            ).map { EventAttendee().setEmail(it) }
            reminders = Event.Reminders().apply {
                useDefault = false
                overrides = listOf(
                    EventReminder().setMethod("email").setMinutes(24 * 60), // 1 day before
                    EventReminder().setMethod("popup").setMinutes(10)       // 10 minutes before
                )
            }
            // Add Google Meet conference details
            conferenceData = ConferenceData().apply {
                createRequest = CreateConferenceRequest().apply {
                    requestId = UUID.randomUUID().toString()  // Unique request ID
                    conferenceSolutionKey = ConferenceSolutionKey().apply {
                        type = "hangoutsMeet"
                    }
                    notes = "Google Meet link for the meeting"
                }
            }
        }
    }

    fun buildEditPermissionRule(attendeeEmailId: String): AclRule {
        return AclRule().apply {
            role = "owner"  // Granting editor permissions
            scope = AclRule.Scope().apply {
                type = "user"
                value = attendeeEmailId  // Email of the attendee
            }
        }
    }
}

class MeetingDataSourceImpl @Inject constructor(
    private val calendarServiceFactory: CalendarServiceFactory,
    private val calendarEventBuilder: CalendarEventBuilder
) : MeetingDataSource {

    companion object {

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
            val event = calendarEventBuilder.build(student, tutor, bookedSlot)

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

                Resource.Success(MeetInfo(eventId = createdEvent.id, meetUrl = meetUrl))
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e)
            }
        }

    }

    override suspend fun cancelMeet(accessToken: String, eventId: String): Resource<Unit> {
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