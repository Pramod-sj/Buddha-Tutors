package com.buddhatutors.model.tutorlisting.slotbooking

import com.buddhatutors.model.Topic
import com.buddhatutors.model.meet.MeetInfo
import com.buddhatutors.common.utils.DateUtils
import kotlinx.serialization.Serializable

/**
 * Represents a booked slot with a date and time range.
 *
 * @param date The date of the booking in the format "yyyy-MM-dd".
 * @param startTime The start time of the booking in the format "HH:mm" (24-hour format).
 * @param endTime The end time of the booking in the format "HH:mm" (24-hour format).
 * @param bookedByStudentId The ID of the student who booked the slot.
 */
@Serializable
data class BookedSlot(
    val id: String,
    val date: String, // Format: "yyyy-MM-dd", e.g., "2024-11-08"
    val startTime: String, // Format: "HH:mm", e.g., "14:00"
    val endTime: String, // Format: "HH:mm", e.g., "15:30"
    val bookedByStudentId: String, // The ID of the student who booked the slot
    val bookedAtDateTime: String,
    val topic: Topic?,
    val meetInfo: MeetInfo?,
    val tutorId: String,
    val bookedByStudent: StudentInfo?,
    val tutorInfo: TutorInfo?,
) {

    val fancyDate by lazy {
        DateUtils.convertDateStringToSpecifiedDateString(
            dateString = date,
            dateFormat = "yyyy-MM-dd",
            requiredDateFormat = "EEE d MMM"
        )
    }

}

@Serializable
data class StudentInfo(
    val id: String,
    val name: String
)

@Serializable
data class TutorInfo(
    val id: String,
    val name: String
)