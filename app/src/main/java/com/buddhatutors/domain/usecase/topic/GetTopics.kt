package com.buddhatutors.domain.usecase.topic

import com.buddhatutors.domain.datasource.TopicDataSource
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.framework.Resource
import javax.inject.Inject

class GetTopics @Inject constructor(
    private val topicDataSource: TopicDataSource
) {

    suspend operator fun invoke(): Resource<List<Topic>> {
        return topicDataSource.getTopics()
    }

}