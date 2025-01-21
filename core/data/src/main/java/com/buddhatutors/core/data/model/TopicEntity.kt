package com.buddhatutors.core.data.model

import com.buddhatutors.core.data.EntityMapper
import com.buddhatutors.model.Topic
import javax.inject.Inject

data class TopicEntity(
    val id: String = "",
    val label: String = "",
    val isVisible: Boolean = false,
)


class TopicEntityMapper @Inject constructor() :
    EntityMapper<TopicEntity, Topic> {

    override fun toEntity(domain: Topic): TopicEntity {
        return TopicEntity(id = domain.id, label = domain.label, isVisible = domain.isVisible)
    }

    override fun toDomain(entity: TopicEntity): Topic {
        return Topic(
            id = entity.id,
            label = entity.label,
            isVisible = entity.isVisible
        )
    }

}