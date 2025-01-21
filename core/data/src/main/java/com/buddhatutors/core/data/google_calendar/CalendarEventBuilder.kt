package com.buddhatutors.core.data.google_calendar

import com.buddhatutors.common.utils.DateUtils
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.model.user.User
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.AclRule
import com.google.api.services.calendar.model.ConferenceData
import com.google.api.services.calendar.model.ConferenceSolutionKey
import com.google.api.services.calendar.model.CreateConferenceRequest
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import java.util.UUID
import javax.inject.Inject

internal class CalendarEventBuilder @Inject constructor() {

    private fun toIndianTimeString(date: String, time: String): String {
        val timeHHmm = DateUtils.convertDateStringToSpecifiedDateString(
            dateString = time,
            dateFormat = "hh:mm a",
            requiredDateFormat = "HH:mm:ss"
        )
        return "${date}T${timeHHmm}+05:30"
    }

    fun build(
        student: User,
        tutor: User,
        providerEmailId: String,
        bookedSlot: BookedSlot
    ): Event {
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
                providerEmailId,
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
