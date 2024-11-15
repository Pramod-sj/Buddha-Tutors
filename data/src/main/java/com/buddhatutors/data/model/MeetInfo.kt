package com.buddhatutors.data.model

import com.buddhatutors.EntityMapper
import com.buddhatutors.domain.model.meet.MeetInfo
import javax.inject.Inject

data class MeetInfoE(
    val eventId: String = "",
    val meetUrl: String = ""
)

class MeetInfoEMapper @Inject constructor() : EntityMapper<MeetInfoE, MeetInfo> {

    override fun toDomain(entity: MeetInfoE): MeetInfo {
        return MeetInfo(entity.eventId, entity.meetUrl)
    }

    override fun toEntity(domain: MeetInfo): MeetInfoE {
        return MeetInfoE(domain.eventId, domain.meetUrl)
    }
}