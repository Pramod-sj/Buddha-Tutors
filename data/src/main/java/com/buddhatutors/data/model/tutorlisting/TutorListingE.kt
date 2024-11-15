package com.buddhatutors.data.model.tutorlisting

import com.buddhatutors.EntityMapper
import com.buddhatutors.data.model.BookedSlotEMapper
import com.buddhatutors.data.model.MeetInfoE
import com.buddhatutors.data.model.TopicE
import com.buddhatutors.data.model.TutorE
import com.buddhatutors.data.model.UserEMapper
import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.meet.MeetInfo
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.Verification
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import javax.inject.Inject

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
    val bookedAtDateTime: String = "",
    val topic: TopicE? = null,
    val meetInfo: MeetInfoE? = null
)

class TutorListingEMapper @Inject constructor(
    private val bookedSlotEMapper: BookedSlotEMapper,
    private val userEMapper: UserEMapper,
    private val verificationEMapper: VerificationEMapper
) : EntityMapper<TutorListingE, TutorListing> {

    override fun toDomain(entity: TutorListingE): TutorListing {
        return TutorListing(
            tutor = entity.tutor?.let { userEMapper.toDomain(it) as? Tutor },
            verification = entity.verification?.let { verificationEMapper.toDomain(it) },
            bookedSlots = entity.bookedSlots.map { bookedSlotEMapper.toDomain(it) }
        )
    }

    override fun toEntity(domain: TutorListing): TutorListingE {
        return TutorListingE(
            tutor = domain.tutor?.let { userEMapper.toEntity(it) as? TutorE },
            verification = domain.verification?.let { verificationEMapper.toEntity(it) },
            bookedSlots = domain.bookedSlots.map { bookedSlotEMapper.toEntity(it) }
        )
    }

}

class VerificationEMapper @Inject constructor() : EntityMapper<VerificationE, Verification> {

    override fun toDomain(entity: VerificationE): Verification {
        return Verification(
            isApproved = entity.approved,
            verifiedByUserId = entity.verifiedByUserId,
            verifiedByUserName = entity.verifiedByUserName,
            verifiedDateTime = entity.verifiedDateTime
        )
    }

    override fun toEntity(domain: Verification): VerificationE {
        return VerificationE(
            approved = domain.isApproved,
            verifiedByUserId = domain.verifiedByUserId,
            verifiedByUserName = domain.verifiedByUserId,
            verifiedDateTime = domain.verifiedByUserId

        )
    }

}