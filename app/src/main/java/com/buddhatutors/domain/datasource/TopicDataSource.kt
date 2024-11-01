package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.Topic
import com.buddhatutors.framework.Resource

interface TopicDataSource {

    suspend fun getTopics(): Resource<List<Topic>>

    suspend fun addTopic(topic: Topic): Resource<Topic>

    suspend fun deleteTopic(id: String): Resource<Boolean>

}