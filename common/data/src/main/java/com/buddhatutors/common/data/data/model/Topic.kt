package com.buddhatutors.common.data.data.model

import com.buddhatutors.common.data.EntityMapper
import com.buddhatutors.common.domain.model.Topic
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import org.checkerframework.checker.units.qual.Time
import javax.inject.Inject

data class TopicE(
    val id: String = "",
    val label: String = "",
    val isVisible: Boolean = false,
)


class TopicEMapper @Inject constructor() :
    EntityMapper<TopicE, Topic> {

    override fun toEntity(domain: Topic): TopicE {
        return TopicE(id = domain.id, label = domain.label, isVisible = domain.isVisible)
    }

    override fun toDomain(entity: TopicE): Topic {
        return Topic(
            id = entity.id,
            label = entity.label,
            isVisible = entity.isVisible
        )
    }

}