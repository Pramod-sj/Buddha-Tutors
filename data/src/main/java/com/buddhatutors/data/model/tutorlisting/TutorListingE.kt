package com.buddhatutors.data.model.tutorlisting

import com.buddhatutors.EntityMapper
import com.buddhatutors.data.model.BookedSlotEMapper
import com.buddhatutors.data.model.MeetInfoE
import com.buddhatutors.data.model.TopicE
import com.buddhatutors.data.model.TopicEMapper
import com.buddhatutors.data.model.UserEMapper
import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.Verification
import javax.inject.Inject

data class TutorListingE(
    val tutorUser: UserEntity = UserEntity(),
    val expertiseIn: List<TopicE> = emptyList(),
    val availableDays: List<String> = emptyList(),
    val availableTimeSlots: List<TimeSlotE> = emptyList(),
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

data class TimeSlotE(
    val start: String? = null,
    val end: String? = null,
)

class TimeSlotEMapper @Inject constructor() : EntityMapper<TimeSlotE, TimeSlot> {
    override fun toDomain(entity: TimeSlotE): TimeSlot {
        return TimeSlot(entity.start, entity.end)
    }

    override fun toEntity(domain: TimeSlot): TimeSlotE {
        return TimeSlotE(domain.start, domain.end)
    }

}

class TutorListingEMapper @Inject constructor(
    private val userEMapper: UserEMapper,
    private val bookedSlotEMapper: BookedSlotEMapper,
    private val verificationEMapper: VerificationEMapper,
    private val topicEMapper: TopicEMapper,
    private val timeSlotEMapper: TimeSlotEMapper
) : EntityMapper<TutorListingE, TutorListing> {

    override fun toDomain(entity: TutorListingE): TutorListing {
        return TutorListing(
            verification = entity.verification?.let { verificationEMapper.toDomain(it) },
            bookedSlots = entity.bookedSlots.map { bookedSlotEMapper.toDomain(it) },
            tutorUser = entity.tutorUser.let { userEMapper.toDomain(it) },
            expertiseIn = entity.expertiseIn.map { topicEMapper.toDomain(it) },
            availableDays = entity.availableDays,
            availableTimeSlots = entity.availableTimeSlots.map { timeSlotEMapper.toDomain(it) },
        )
    }

    override fun toEntity(domain: TutorListing): TutorListingE {
        return TutorListingE(
            tutorUser = domain.tutorUser.let { userEMapper.toEntity(it) },
            expertiseIn = domain.expertiseIn.map { topicEMapper.toEntity(it) },
            availableDays = domain.availableDays,
            availableTimeSlots = domain.availableTimeSlots.map { timeSlotEMapper.toEntity(it) },
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