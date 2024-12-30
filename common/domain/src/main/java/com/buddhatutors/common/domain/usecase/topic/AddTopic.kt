package com.buddhatutors.common.domain.usecase.topic

import com.buddhatutors.common.domain.datasource.TopicDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.Topic
import javax.inject.Inject

class AddTopic @Inject constructor(
    private val topicDataSource: TopicDataSource
) {

    suspend operator fun invoke(topic: Topic): Resource<Topic> {
        return topicDataSource.addTopic(topic)
    }

}