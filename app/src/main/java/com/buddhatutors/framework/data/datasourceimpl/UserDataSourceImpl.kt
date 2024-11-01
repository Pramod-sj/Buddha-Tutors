package com.buddhatutors.framework.data.datasourceimpl

import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.domain.model.User
import com.buddhatutors.framework.Resource
import com.buddhatutors.framework.data.model.UserEntity
import com.buddhatutors.framework.data.model.toDomain
import com.buddhatutors.framework.data.model.toEntity
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class UserDataSourceImpl @Inject constructor() : UserDataSource {

    private val studentsDocumentReference = FirebaseFirestore.getInstance().collection("users")

    override suspend fun getUser(userId: String): Resource<User> {
        return suspendCoroutine { continuation ->
            studentsDocumentReference
                .document(userId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val user = if (value?.exists() == true) {
                            value.toObject(UserEntity::class.java)?.toDomain()
                        } else null
                        if (user != null) {
                            continuation.resume(Resource.Success(user))
                        } else {
                            continuation.resume(Resource.Error(Exception("No user found")))
                        }
                    } else {
                        continuation.resume(
                            Resource.Error(it.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun setUser(user: User): Resource<User> {
        return suspendCoroutine { continuation ->
            studentsDocumentReference
                .document(user.id)
                .set(user.toEntity())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Resource.Success(user))
                    } else {
                        continuation.resume(
                            Resource.Error(it.exception ?: Exception(""))
                        )
                    }
                }

        }
    }
}