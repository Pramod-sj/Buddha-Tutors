package com.buddhatutors.common.data.data.model.tutorlisting

import com.buddhatutors.common.data.EntityMapper
import com.buddhatutors.common.data.data.model.MeetInfoE
import com.buddhatutors.common.data.data.model.TopicE
import com.buddhatutors.common.data.data.model.TopicEMapper
import com.buddhatutors.common.data.data.model.UserEMapper
import com.buddhatutors.common.data.data.model.UserEntity
import com.buddhatutors.common.domain.model.TimeSlot
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.domain.model.tutorlisting.Verification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import javax.inject.Inject

data class TutorListingE(
    val tutorUser: UserEntity = UserEntity(),
    val expertiseIn: List<TopicE> = emptyList(),
    val languages: List<String> = emptyList(),
    val availableDays: List<String> = emptyList(),
    val availableTimeSlots: List<TimeSlotE> = emptyList(),
    val verification: VerificationE? = null,
    val addedByUser: UserEntity? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,

    val filteringOptions: Map<String, Boolean> = emptyMap()
)

data class VerificationE(
    val approved: Boolean = false,
    val verifiedByUserId: String = "",
    val verifiedByUserName: String = "",
    val verifiedDateTime: String = ""
)

data class BookedSlotE(
    val id: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val bookedByStudentId: String = "",
    val bookedAtDateTime: String = "",
    val topic: TopicE? = null,
    val meetInfo: MeetInfoE? = null,
    val tutorId: String = "",

    val bookedByStudent: StudentInfoE? = null,
    val tutorInfo: TutorInfoE? = null,

    val bookedSlotStartsTimeInMillis: Long = 0,
    val bookedSlotEndsTimeInMillis: Long = 0
)

data class StudentInfoE(
    val id: String? = null,
    val name: String? = null
)

data class TutorInfoE(
    val id: String? = null,
    val name: String? = null
)

data class TimeSlotE(
    val start: String? = null,
    val end: String? = null,
)

class TimeSlotEMapper @Inject constructor() :
    EntityMapper<TimeSlotE, TimeSlot> {
    override fun toDomain(entity: TimeSlotE): TimeSlot {
        return TimeSlot(entity.start, entity.end)
    }

    override fun toEntity(domain: TimeSlot): TimeSlotE {
        return TimeSlotE(domain.start, domain.end)
    }

}

class TutorListingEMapper @Inject constructor(
    private val userEMapper: UserEMapper,
    private val verificationEMapper: VerificationEMapper,
    private val topicEMapper: TopicEMapper,
    private val timeSlotEMapper: TimeSlotEMapper
) : EntityMapper<TutorListingE, TutorListing> {

    override fun toDomain(entity: TutorListingE): TutorListing {
        return TutorListing(
            verification = entity.verification?.let { verificationEMapper.toDomain(it) },
            tutorUser = entity.tutorUser.let { userEMapper.toDomain(it) },
            expertiseIn = entity.expertiseIn.map { topicEMapper.toDomain(it) },
            languages = entity.languages,
            availableDays = entity.availableDays,
            availableTimeSlots = entity.availableTimeSlots.map { timeSlotEMapper.toDomain(it) },
            addedByUser = entity.addedByUser?.let { userEMapper.toDomain(it) }
        )
    }

    override fun toEntity(domain: TutorListing): TutorListingE {
        return TutorListingE(
            tutorUser = domain.tutorUser.let { userEMapper.toEntity(it) },
            expertiseIn = domain.expertiseIn.map { topicEMapper.toEntity(it) },
            languages = domain.languages,
            availableDays = domain.availableDays,
            availableTimeSlots = domain.availableTimeSlots.map { timeSlotEMapper.toEntity(it) },
            verification = domain.verification?.let { verificationEMapper.toEntity(it) },
            addedByUser = domain.addedByUser?.let { userEMapper.toEntity(it) },
            filteringOptions = (domain.languages + domain.expertiseIn.map { it.id }).associateWith { true }
        )
    }

}

class VerificationEMapper @Inject constructor() :
    EntityMapper<VerificationE, Verification> {

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