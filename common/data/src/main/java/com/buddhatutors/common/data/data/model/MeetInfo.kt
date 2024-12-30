package com.buddhatutors.common.data.data.model

import com.buddhatutors.common.data.EntityMapper
import javax.inject.Inject

data class MeetInfoE(
    val eventId: String = "",
    val meetUrl: String = ""
)

class MeetInfoEMapper @Inject constructor() :
    EntityMapper<MeetInfoE, com.buddhatutors.common.domain.model.meet.MeetInfo> {

    override fun toDomain(entity: MeetInfoE): com.buddhatutors.common.domain.model.meet.MeetInfo {
        return com.buddhatutors.common.domain.model.meet.MeetInfo(entity.eventId, entity.meetUrl)
    }

    override fun toEntity(domain: com.buddhatutors.common.domain.model.meet.MeetInfo): MeetInfoE {
        return MeetInfoE(domain.eventId, domain.meetUrl)
    }
}