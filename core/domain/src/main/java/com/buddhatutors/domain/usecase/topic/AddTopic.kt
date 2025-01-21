package com.buddhatutors.domain.usecase.topic

import com.buddhatutors.domain.datasource.TopicDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.Topic
import javax.inject.Inject

class AddTopic @Inject constructor(
    private val topicDataSource: TopicDataSource
) {

    suspend operator fun invoke(topic: Topic): Resource<Topic> {
        return topicDataSource.addTopic(topic)
    }

}