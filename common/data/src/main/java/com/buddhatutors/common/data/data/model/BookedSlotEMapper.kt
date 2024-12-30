package com.buddhatutors.common.data.data.model

import com.buddhatutors.common.data.EntityMapper
import com.buddhatutors.common.data.data.model.tutorlisting.BookedSlotE
import com.buddhatutors.common.data.data.model.tutorlisting.StudentInfoE
import com.buddhatutors.common.data.data.model.tutorlisting.TutorInfoE
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.StudentInfo
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.TutorInfo
import com.buddhatutors.common.utils.DateUtils
import javax.inject.Inject

class BookedSlotEMapper @Inject constructor(
    private val meetInfoEMapper: MeetInfoEMapper,
    private val topicEMapper: TopicEMapper
) : EntityMapper<BookedSlotE, BookedSlot> {

    override fun toDomain(entity: BookedSlotE): BookedSlot {
        return BookedSlot(
            id = entity.id,
            date = entity.date,
            startTime = entity.startTime,
            endTime = entity.endTime,
            bookedByStudentId = entity.bookedByStudentId,
            bookedAtDateTime = entity.bookedAtDateTime,
            topic = entity.topic?.let { topicEMapper.toDomain(it) },
            meetInfo = entity.meetInfo?.let { meetInfoEMapper.toDomain(it) },
            tutorId = entity.tutorId,
            bookedByStudent = entity.bookedByStudent?.let {
                StudentInfo(
                    id = it.id.orEmpty(),
                    name = it.name.orEmpty()
                )
            },
            tutorInfo = entity.tutorInfo?.let {
                TutorInfo(id = it.id.orEmpty(), name = it.name.orEmpty())
            }
        )
    }

    override fun toEntity(domain: BookedSlot): BookedSlotE {
        return BookedSlotE(
            id = domain.id,
            date = domain.date,
            startTime = domain.startTime,
            endTime = domain.endTime,
            bookedByStudentId = domain.bookedByStudentId,
            bookedAtDateTime = domain.bookedAtDateTime,
            topic = domain.topic?.let { topicEMapper.toEntity(it) },
            meetInfo = domain.meetInfo?.let { meetInfoEMapper.toEntity(it) },
            tutorId = domain.tutorId,
            bookedByStudent = domain.bookedByStudent.let { StudentInfoE(it?.id, it?.name) },
            bookedSlotStartsTimeInMillis = DateUtils.convertSpecifiedDateStringToTimeInMillis(
                dateString = domain.date + " " + domain.startTime,
                dateFormat = "yyyy-MM-dd hh:mm a",
            ),
            bookedSlotEndsTimeInMillis = DateUtils.convertSpecifiedDateStringToTimeInMillis(
                dateString = domain.date + " " + domain.endTime,
                dateFormat = "yyyy-MM-dd hh:mm a",
            ),
            tutorInfo = domain.tutorInfo?.let {
                TutorInfoE(id = it.id, name = it.name)
            }
        )
    }
}