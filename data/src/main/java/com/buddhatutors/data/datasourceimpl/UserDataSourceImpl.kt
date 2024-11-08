package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.data.model.toEntity
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import com.google.firebase.firestore.Filter
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

    override suspend fun getUserByType(type: UserType): Resource<List<User>> {
        return suspendCoroutine { continuation ->
            studentsDocumentReference
                .where(Filter.equalTo("userType", type.id))
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val users = if (value?.isEmpty == false) {
                            value.toObjects(UserEntity::class.java)
                                .mapNotNull { it?.toDomain() }
                        } else emptyList()
                        continuation.resume(Resource.Success(users))
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