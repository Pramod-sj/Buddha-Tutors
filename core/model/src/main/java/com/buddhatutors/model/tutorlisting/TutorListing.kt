package com.buddhatutors.model.tutorlisting

import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.Topic
import com.buddhatutors.model.user.User
import kotlinx.serialization.Serializable

/**
 * Represents a listing of a tutor, including the tutor's details,
 * verification status, and a list of booked slots.
 *
 * @param tutorId The details of the tutor.
 * @param verification The verification status of the tutor.
 * @param bookedSlots A list of booked slots for the tutor.
 *                    Defaults to an empty list if no slots are booked.
 */
@kotlinx.serialization.Serializable
data class TutorListing(
    val tutorUser: User = User(),
    val expertiseIn: List<Topic> = emptyList(),
    val languages: List<String> = emptyList(),
    val availableDays: List<String> = emptyList(),
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val verification: Verification? = null,
    val addedByUser: User? = null
)


/**
 * Represents the verification status of an entity.
 *
 * @param isApproved Indicates whether the tutor is approved to be listing for students/parents.
 * @param verifiedByUserId The ID of the user who verified the tutor.
 * @param verifiedByUserName The name of the user who verified the tutor.
 * @param verifiedDateTime The date and time when the entity was verified,
 *                         in the format "dd-MM-yyyy HH:mm:ss" (DateUtils.DATE_TIME_FORMAT).
 */
@Serializable
data class Verification(
    val isApproved: Boolean = false,
    val verifiedByUserId: String = "",
    val verifiedByUserName: String = "",
    val verifiedDateTime: String = ""// Format: DateUtils.DATE_TIME_FORMAT, e.g., "01-01-2025 15:55:00"
)
