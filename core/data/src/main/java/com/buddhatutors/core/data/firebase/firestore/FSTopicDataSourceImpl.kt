package com.buddhatutors.core.data.firebase.firestore

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.buddhatutors.core.data.firebase.firestore.constant.Constant.CREATED_AT_FIELD_NAME
import com.buddhatutors.core.data.firebase.firestore.constant.Constant.PAGE_LIMIT
import com.buddhatutors.core.data.firebase.firestore.constant.Constant.PAGE_SIZE
import com.buddhatutors.core.data.firebase.firestore.constant.FirebaseCollectionConstant
import com.buddhatutors.core.data.firebase.firestore.paging_source.FireStoreTopicsPagingSource
import com.buddhatutors.core.data.model.TopicEntityMapper
import com.buddhatutors.core.data.toMap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class FSTopicDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val topicEntityMapper: TopicEntityMapper
) : com.buddhatutors.domain.datasource.TopicDataSource {

    private val topicFireStoreRef = firestore.collection(FirebaseCollectionConstant.REF_TOPIC)

    override suspend fun addTopic(topic: com.buddhatutors.model.Topic): com.buddhatutors.model.Resource<com.buddhatutors.model.Topic> {
        return suspendCoroutine { continuation ->
            val topicId = topicFireStoreRef.document().id

            val topicMap =
                topicEntityMapper.toEntity(topic.copy(id = topicId))
                    .toMap().toMutableMap().apply {
                        put(CREATED_AT_FIELD_NAME, Timestamp.now())
                    }

            topicFireStoreRef
                .document(topicId)
                .set(topicMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(com.buddhatutors.model.Resource.Success(topic))
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception?.cause ?: Throwable("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getTopics(): com.buddhatutors.model.Resource<List<com.buddhatutors.model.Topic>> {
        return suspendCoroutine { continuation ->
            topicFireStoreRef.get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val topics =
                            result.result.documents.mapNotNull { it.toObject(com.buddhatutors.model.Topic::class.java) }
                        continuation.resume(com.buddhatutors.model.Resource.Success(topics))
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                result.exception?.cause ?: Throwable("")
                            )
                        )
                    }
                }
        }
    }

    override fun getTopicsPaginated(): Flow<PagingData<com.buddhatutors.model.Topic>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
        ) {
            FireStoreTopicsPagingSource(
                topicFireStoreRef
                    .orderBy(CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(PAGE_LIMIT)
            )
        }.flow.map { pager ->
            pager.map { topicEntityMapper.toDomain(it) }
        }
    }

    override suspend fun deleteTopic(id: String): com.buddhatutors.model.Resource<Boolean> {
        return suspendCoroutine { continuation ->
            topicFireStoreRef.document(id)
                .delete()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        continuation.resume(com.buddhatutors.model.Resource.Success(true))
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                result.exception?.cause ?: Throwable("")
                            )
                        )
                    }
                }
        }
    }
}