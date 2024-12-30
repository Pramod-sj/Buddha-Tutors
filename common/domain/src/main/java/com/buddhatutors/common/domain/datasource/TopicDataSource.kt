package com.buddhatutors.common.domain.datasource

import androidx.paging.PagingData
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface TopicDataSource {

    suspend fun getTopics(): Resource<List<Topic>>

    fun getTopicsPaginated(): Flow<PagingData<Topic>>

    suspend fun addTopic(topic: Topic): Resource<Topic>

    suspend fun deleteTopic(id: String): Resource<Boolean>

}