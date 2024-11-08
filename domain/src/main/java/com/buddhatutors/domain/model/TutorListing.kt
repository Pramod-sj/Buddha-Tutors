package com.buddhatutors.domain.model

import com.buddhatutors.domain.model.user.Tutor

data class TutorListing(
    val tutor: Tutor,
    val verification: Verification,
    val bookedSlots: List<BookedSlot> = emptyList(),
) {

    data class Verification(
        val isApproved: Boolean,
        val verifiedByUserId: String,
        val verifiedByUserName: String,
        val verifiedDateTime: String
    )

    data class BookedSlot(
        val dateTime: String,
        val slotBookedByStudentId: String
    )

}