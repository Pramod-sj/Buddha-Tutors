package com.buddhatutors.common.domain.datasource

import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.meet.MeetInfo
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.common.domain.model.user.User

/**
 * An interface for scheduling and managing meeting events.
 * Provides methods to handle the creation and cancellation of meeting events,
 * allowing integration with services that facilitate meeting scheduling (e.g., Google Calendar).
 */
interface MeetingDataSource {

    /**
     * Schedules a new meeting using the provided booking details.
     *
     * @param accessToken The access token required for authenticating with the meeting service (e.g., Google Calendar).
     * @param student The student who will participate in the meeting, including their details (e.g., name, email).
     * @param tutor The tutor who will participate in the meeting, including their details (e.g., name, email, expertise).
     * @param bookedSlot The details of the booked slot, including the start and end time, and any additional booking information.
     * @return A [Resource] containing the [MeetInfo] of the scheduled meeting. The [MeetInfo] includes a unique event ID
     *         and any other relevant information for referencing, updating, or cancelling the meeting in the future.
     */
    suspend fun scheduleMeet(
        accessToken: String,
        student: User,
        tutor: User,
        bookedSlot: BookedSlot
    ): Resource<MeetInfo>

    /**
     * Cancels an existing meeting using the provided event ID.
     *
     * @param accessToken The access token required for authenticating with the meeting service.
     * @param eventId The unique identifier of the meeting event to be cancelled.
     * @return A [Resource] containing a confirmation of the cancellation. If successful, the result will include the
     *         event ID of the cancelled meeting, confirming that the specific meeting has been successfully removed.
     */
    suspend fun cancelMeet(accessToken: String, eventId: String): Resource<Unit>
}