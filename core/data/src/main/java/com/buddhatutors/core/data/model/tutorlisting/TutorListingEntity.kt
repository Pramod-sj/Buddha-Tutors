package com.buddhatutors.core.data.model.tutorlisting

import com.buddhatutors.core.data.EntityMapper
import com.buddhatutors.core.data.model.MeetInfoEntity
import com.buddhatutors.core.data.model.TopicEntity
import com.buddhatutors.core.data.model.TopicEntityMapper
import com.buddhatutors.core.data.model.UserEMapper
import com.buddhatutors.core.data.model.UserEntity
import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.tutorlisting.Verification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import javax.inject.Inject

data class TutorListingEntity(
    val tutorUser: UserEntity = UserEntity(),
    val expertiseIn: List<TopicEntity> = emptyList(),
    val languages: List<String> = emptyList(),
    val availableDays: List<String> = emptyList(),
    val availableTimeSlots: List<TimeSlotEntity> = emptyList(),
    val verification: VerificationEntity? = null,
    val addedByUser: UserEntity? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,

    val filteringOptions: Map<String, Boolean> = emptyMap()
)

data class VerificationEntity(
    val approved: Boolean = false,
    val verifiedByUserId: String = "",
    val verifiedByUserName: String = "",
    val verifiedDateTime: String = ""
)

data class BookedSlotEntity(
    val id: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val bookedByStudentId: String = "",
    val bookedAtDateTime: String = "",
    val topic: TopicEntity? = null,
    val meetInfo: MeetInfoEntity? = null,
    val tutorId: String = "",

    val bookedByStudent: StudentInfoEntity? = null,
    val tutorInfo: TutorInfoEntity? = null,

    val bookedSlotStartsTimeInMillis: Long = 0,
    val bookedSlotEndsTimeInMillis: Long = 0
)

data class StudentInfoEntity(
    val id: String? = null,
    val name: String? = null
)

data class TutorInfoEntity(
    val id: String? = null,
    val name: String? = null
)

data class TimeSlotEntity(
    val start: String? = null,
    val end: String? = null,
)

class TimeSlotEMapper @Inject constructor() :
    EntityMapper<TimeSlotEntity, TimeSlot> {
    override fun toDomain(entity: TimeSlotEntity): TimeSlot {
        return TimeSlot(entity.start, entity.end)
    }

    override fun toEntity(domain: TimeSlot): TimeSlotEntity {
        return TimeSlotEntity(domain.start, domain.end)
    }

}

class TutorListingEMapper @Inject constructor(
    private val userEMapper: UserEMapper,
    private val verificationEMapper: VerificationEMapper,
    private val topicEntityMapper: TopicEntityMapper,
    private val timeSlotEMapper: TimeSlotEMapper
) : EntityMapper<TutorListingEntity, TutorListing> {

    override fun toDomain(entity: TutorListingEntity): TutorListing {
        return TutorListing(
            verification = entity.verification?.let { verificationEMapper.toDomain(it) },
            tutorUser = entity.tutorUser.let { userEMapper.toDomain(it) },
            expertiseIn = entity.expertiseIn.map { topicEntityMapper.toDomain(it) },
            languages = entity.languages,
            availableDays = entity.availableDays,
            availableTimeSlots = entity.availableTimeSlots.map { timeSlotEMapper.toDomain(it) },
            addedByUser = entity.addedByUser?.let { userEMapper.toDomain(it) }
        )
    }

    override fun toEntity(domain: TutorListing): TutorListingEntity {
        return TutorListingEntity(
            tutorUser = domain.tutorUser.let { userEMapper.toEntity(it) },
            expertiseIn = domain.expertiseIn.map { topicEntityMapper.toEntity(it) },
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
    EntityMapper<VerificationEntity, Verification> {

    override fun toDomain(entity: VerificationEntity): Verification {
        return Verification(
            isApproved = entity.approved,
            verifiedByUserId = entity.verifiedByUserId,
            verifiedByUserName = entity.verifiedByUserName,
            verifiedDateTime = entity.verifiedDateTime
        )
    }

    override fun toEntity(domain: Verification): VerificationEntity {
        return VerificationEntity(
            approved = domain.isApproved,
            verifiedByUserId = domain.verifiedByUserId,
            verifiedByUserName = domain.verifiedByUserId,
            verifiedDateTime = domain.verifiedByUserId

        )
    }

}