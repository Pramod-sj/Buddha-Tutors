package com.buddhatutors.data.model.tutorlisting

import com.buddhatutors.data.model.TutorE
import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.Verification
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.Tutor

data class TutorListingE(
    val tutor: TutorE? = null,
    val verification: VerificationE? = null,
    val bookedSlots: List<BookedSlotE> = emptyList(),
)

data class VerificationE(
    val approved: Boolean = false,
    val verifiedByUserId: String = "",
    val verifiedByUserName: String = "",
    val verifiedDateTime: String = ""
)

data class BookedSlotE(
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val bookedByStudentId: String = "",
    val bookedAtDateTime: String = ""
)

fun TutorListingE.toDomain(): TutorListing? {
    val tutor = tutor?.toDomain() as? Tutor ?: return null
    val verification = verification?.toDomain() ?: return null
    return TutorListing(
        tutor = tutor,
        verification = verification,
        bookedSlots = bookedSlots.map { it.toDomain() },
    )
}

fun VerificationE.toDomain(): Verification {
    return Verification(
        isApproved = approved,
        verifiedByUserId = verifiedByUserId,
        verifiedByUserName = verifiedByUserName,
        verifiedDateTime = verifiedDateTime
    )
}


fun BookedSlotE.toDomain(): BookedSlot {
    return BookedSlot(
        date = date,
        startTime = startTime,
        endTime = endTime,
        bookedByStudentId = bookedByStudentId,
        bookedAtDateTime = bookedAtDateTime
    )
}


fun BookedSlot.toEntity(): BookedSlotE {
    return BookedSlotE(
        date = date,
        startTime = startTime,
        endTime = endTime,
        bookedByStudentId = bookedByStudentId
    )
}