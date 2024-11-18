package com.buddhatutors.data.model

import com.buddhatutors.EntityMapper
import com.buddhatutors.data.model.tutorlisting.BookedSlotE
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
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
            tutorId = entity.tutorId
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
            tutorId = domain.tutorId
        )
    }
}