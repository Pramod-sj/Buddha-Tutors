package com.buddhatutors.data.model.tutorlisting

import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.domain.model.TutorListing
import com.buddhatutors.domain.model.user.Tutor
import com.google.gson.annotations.SerializedName

data class TutorListingE(
    val tutor: UserEntity? = null,
    val verification: VerificationE? = null,
    val bookedSlots: List<BookedSlotE> = emptyList(),
) {

    data class VerificationE(
        val approved: Boolean = false,
        val verifiedByUserId: String = "",
        val verifiedByUserName: String = "",
        val verifiedDateTime: String = ""
    )

    data class BookedSlotE(
        val dateTime: String = "",
        val slotBookedByStudentId: String = ""
    )

}

fun TutorListingE.toDomain(): TutorListing? {
    val tutor = tutor?.toDomain() as? Tutor ?: return null
    val verification = verification?.toDomain() ?: return null
    return TutorListing(
        tutor = tutor,
        verification = verification,
        bookedSlots = bookedSlots.map { it.toDomain() },
    )
}

fun TutorListingE.VerificationE.toDomain(): TutorListing.Verification {
    return TutorListing.Verification(
        isApproved = approved,
        verifiedByUserId = verifiedByUserId,
        verifiedByUserName = verifiedByUserName,
        verifiedDateTime = verifiedDateTime
    )
}


fun TutorListingE.BookedSlotE.toDomain(): TutorListing.BookedSlot {
    return TutorListing.BookedSlot(
        dateTime = dateTime,
        slotBookedByStudentId = slotBookedByStudentId
    )
}


fun TutorListing.BookedSlot.toEntity(): TutorListingE.BookedSlotE {
    return TutorListingE.BookedSlotE(
        dateTime = dateTime,
        slotBookedByStudentId = slotBookedByStudentId
    )
}