package com.buddhatutors.framework.data.datasourceimpl

import com.buddhatutors.domain.datasource.TopicDataSource
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.framework.Resource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

class TopicDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : TopicDataSource {

    private val topicFireStoreRef = firestore.collection("expertise_topics")

    override suspend fun addTopic(topic: Topic): Resource<Topic> {
        return suspendCoroutine { continuation ->
            topicFireStoreRef.add(topic)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Resource.Success(topic))
                    } else {
                        continuation.resume(Resource.Error(it.exception?.cause ?: Throwable("")))
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