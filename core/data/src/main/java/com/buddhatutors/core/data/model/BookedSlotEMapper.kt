package com.buddhatutors.core.data.model

import com.buddhatutors.core.data.EntityMapper
import com.buddhatutors.core.data.model.tutorlisting.BookedSlotEntity
import com.buddhatutors.core.data.model.tutorlisting.StudentInfoEntity
import com.buddhatutors.core.data.model.tutorlisting.TutorInfoEntity
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.model.tutorlisting.slotbooking.StudentInfo
import com.buddhatutors.model.tutorlisting.slotbooking.TutorInfo
import com.buddhatutors.common.utils.DateUtils
import javax.inject.Inject

class BookedSlotEMapper @Inject constructor(
    private val meetInfoEMapper: MeetInfoEMapper,
    private val topicEntityMapper: TopicEntityMapper
) : EntityMapper<BookedSlotEntity, BookedSlot> {

    override fun toDomain(entity: BookedSlotEntity): BookedSlot {
        return BookedSlot(
            id = entity.id,
            date = entity.date,
            startTime = entity.startTime,
            endTime = entity.endTime,
            bookedByStudentId = entity.bookedByStudentId,
            bookedAtDateTime = entity.bookedAtDateTime,
            topic = entity.topic?.let { topicEntityMapper.toDomain(it) },
            meetInfo = entity.meetInfo?.let { meetInfoEMapper.toDomain(it) },
            tutorId = entity.tutorId,
            bookedByStudent = entity.bookedByStudent?.let {
                StudentInfo(
                    id = it.id.orEmpty(),
                    name = it.name.orEmpty()
                )
            },
            tutorInfo = entity.tutorInfo?.let {
                TutorInfo(
                    id = it.id.orEmpty(),
                    name = it.name.orEmpty()
                )
            }
        )
    }

    override fun toEntity(domain: BookedSlot): BookedSlotEntity {
        return BookedSlotEntity(
            id = domain.id,
            date = domain.date,
            startTime = domain.startTime,
            endTime = domain.endTime,
            bookedByStudentId = domain.bookedByStudentId,
            bookedAtDateTime = domain.bookedAtDateTime,
            topic = domain.topic?.let { topicEntityMapper.toEntity(it) },
            meetInfo = domain.meetInfo?.let { meetInfoEMapper.toEntity(it) },
            tutorId = domain.tutorId,
            bookedByStudent = domain.bookedByStudent.let { StudentInfoEntity(it?.id, it?.name) },
            bookedSlotStartsTimeInMillis = DateUtils.convertSpecifiedDateStringToTimeInMillis(
                dateString = domain.date + " " + domain.startTime,
                dateFormat = "yyyy-MM-dd hh:mm a",
            ),
            bookedSlotEndsTimeInMillis = DateUtils.convertSpecifiedDateStringToTimeInMillis(
                dateString = domain.date + " " + domain.endTime,
                dateFormat = "yyyy-MM-dd hh:mm a",
            ),
            tutorInfo = domain.tutorInfo?.let {
                TutorInfoEntity(id = it.id, name = it.name)
            }
        )
    }
}