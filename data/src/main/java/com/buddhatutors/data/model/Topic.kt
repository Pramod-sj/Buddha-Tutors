package com.buddhatutors.data.model

import com.buddhatutors.EntityMapper
import com.buddhatutors.domain.model.Topic
import javax.inject.Inject

data class TopicE(
    val id: String = "",
    val label: String = "",
    val isVisible: Boolean = false
)


class TopicEMapper @Inject constructor() : EntityMapper<TopicE, Topic> {

    override fun toEntity(domain: Topic): TopicE {
        return TopicE(id = domain.id, label = domain.label, isVisible = domain.isVisible)
    }

    override fun toDomain(entity: TopicE): Topic {
        return Topic(id = entity.id, label = entity.label, isVisible = entity.isVisible)
    }

}