package com.buddhatutors.common.data.data.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.buddhatutors.common.data.Constant.CREATED_AT_FIELD_NAME
import com.buddhatutors.common.data.Constant.PAGE_LIMIT
import com.buddhatutors.common.data.Constant.PAGE_SIZE
import com.buddhatutors.common.data.FirebaseCollectionConstant
import com.buddhatutors.common.data.data.FireStoreTopicsPagingSource
import com.buddhatutors.common.data.data.model.TopicEMapper
import com.buddhatutors.common.data.toMap
import com.buddhatutors.common.domain.datasource.TopicDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.Topic
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.full.memberProperties

internal class TopicDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val topicEMapper: TopicEMapper
) : TopicDataSource {

    private val topicFireStoreRef = firestore.collection(FirebaseCollectionConstant.REF_TOPIC)

    override suspend fun addTopic(topic: Topic): Resource<Topic> {
        return suspendCoroutine { continuation ->
            val topicId = topicFireStoreRef.document().id

            val topicMap =
                topicEMapper.toEntity(topic.copy(id = topicId))
                    .toMap().toMutableMap().apply {
                        put(CREATED_AT_FIELD_NAME, Timestamp.now())
                    }

            topicFireStoreRef
                .document(topicId)
                .set(topicMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Resource.Success(topic))
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception?.cause ?: Throwable("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getTopics(): Resource<List<Topic>> {
        return suspendCoroutine { continuation ->
            topicFireStoreRef.get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val topics =
                            result.result.documents.mapNotNull { it.toObject(Topic::class.java) }
                        continuation.resume(Resource.Success(topics))
                    } else {
                        continuation.resume(
                            Resource.Error(
                                result.exception?.cause ?: Throwable("")
                            )
                        )
                    }
                }
        }
    }

    override fun getTopicsPaginated(): Flow<PagingData<Topic>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
        ) {
            FireStoreTopicsPagingSource(
                topicFireStoreRef
                    .orderBy(CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(PAGE_LIMIT)
            )
        }.flow.map { pager ->
            pager.map { topicEMapper.toDomain(it) }
        }
    }

    override suspend fun deleteTopic(id: String): Resource<Boolean> {
        return suspendCoroutine { continuation ->
            topicFireStoreRef.document(id)
                .delete()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Resource.Success(true))
                    } else {
                        continuation.resume(
                            Resource.Error(
                                result.exception?.cause ?: Throwable("")
                            )
                        )
                    }
                }
        }
    }
}