package com.buddhatutors.core.data.model

import com.buddhatutors.core.data.EntityMapper
import com.buddhatutors.model.meet.MeetInfo
import javax.inject.Inject

data class MeetInfoEntity(
    val eventId: String = "",
    val meetUrl: String = ""
)

class MeetInfoEMapper @Inject constructor() :
    EntityMapper<MeetInfoEntity, MeetInfo> {

    override fun toDomain(entity: MeetInfoEntity): MeetInfo {
        return MeetInfo(entity.eventId, entity.meetUrl)
    }

    override fun toEntity(domain: MeetInfo): MeetInfoEntity {
        return MeetInfoEntity(domain.eventId, domain.meetUrl)
    }
}