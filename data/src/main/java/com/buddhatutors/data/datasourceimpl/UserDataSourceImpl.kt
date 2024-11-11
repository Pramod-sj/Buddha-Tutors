package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.AdminE
import com.buddhatutors.data.model.MasterTutorE
import com.buddhatutors.data.model.StudentE
import com.buddhatutors.data.model.TutorE
import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.data.model.toEntity
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class UserDataSourceImpl @Inject constructor() : UserDataSource {

    private val studentsDocumentReference = FirebaseFirestore.getInstance().collection("users")

    private fun DocumentSnapshot.toUserObject(): UserEntity? {

        return when (toObject(UserEntity::class.java)?.userType) {

            UserType.STUDENT.id -> toObject(StudentE::class.java)

            UserType.TUTOR.id -> toObject(TutorE::class.java)

            UserType.ADMIN.id -> toObject(AdminE::class.java)

            UserType.MASTER_TUTOR.id -> toObject(MasterTutorE::class.java)

            else -> null
        }
    }

    override suspend fun getUser(userId: String): Resource<User> {
        return suspendCoroutine { continuation ->
            studentsDocumentReference
                .document(userId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val user = if (value?.exists() == true) {
                            value.toUserObject()?.toDomain()
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
                            value.mapNotNull { it.toUserObject()?.toDomain() }
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