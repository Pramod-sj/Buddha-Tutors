package com.buddhatutors.common.domain.usecase.topic

import androidx.paging.PagingData
import com.buddhatutors.common.domain.datasource.TopicDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.Topic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopics @Inject constructor(
    private val topicDataSource: TopicDataSource
) {

    suspend operator fun invoke(): Resource<List<Topic>> {
        return topicDataSource.getTopics()
    }

    fun paginated(): Flow<PagingData<Topic>> {
        return topicDataSource.getTopicsPaginated()
    }

}