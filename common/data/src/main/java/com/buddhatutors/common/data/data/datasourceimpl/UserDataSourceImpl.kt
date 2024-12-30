package com.buddhatutors.common.data.data.datasourceimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.buddhatutors.common.data.Constant
import com.buddhatutors.common.data.Constant.CREATED_AT_FIELD_NAME
import com.buddhatutors.common.data.data.FirestoreUsersPagingSource
import com.buddhatutors.common.data.data.model.UserEMapper
import com.buddhatutors.common.data.data.model.UserEntity
import com.buddhatutors.common.data.toMap
import com.buddhatutors.common.domain.datasource.UserDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class UserDataSourceImpl @Inject constructor(
    private val userEMapper: UserEMapper
) : UserDataSource {

    private val userDataRef = FirebaseFirestore.getInstance().collection("users")

    override suspend fun getUser(userId: String): Resource<User> {
        return suspendCoroutine { continuation ->
            userDataRef
                .document(userId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val user = if (value?.exists() == true) {
                            value.toObject(UserEntity::class.java)?.let {
                                userEMapper.toDomain(it)
                            }
                        } else null
                        if (user != null) {
                            continuation.resume(
                                Resource.Success(user)
                            )
                        } else {
                            continuation.resume(
                                Resource.Error(
                                    Exception("No user found")
                                )
                            )
                        }
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getUserByEmail(emailId: String): Resource<User> {
        return suspendCoroutine { continuation ->
            userDataRef
                .whereEqualTo("email", emailId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val user = if (value?.isEmpty == false) {
                            value.documents.firstOrNull()
                                ?.toObject(UserEntity::class.java)?.let {
                                    userEMapper.toDomain(it)
                                }
                        } else null
                        if (user != null) {
                            continuation.resume(
                                Resource.Success(user)
                            )
                        } else {
                            continuation.resume(
                                Resource.Error(
                                    Exception("No user found")
                                )
                            )
                        }
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getUserByType(type: UserType): Resource<List<User>> {
        return suspendCoroutine { continuation ->
            userDataRef
                .where(Filter.equalTo("userType", type.id))
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val users = if (value?.isEmpty == false) {
                            value.mapNotNull {
                                it.toObject(UserEntity::class.java)
                                    .let { userEMapper.toDomain(it) }
                            }
                        } else emptyList()
                        continuation.resume(
                            Resource.Success(
                                users
                            )
                        )
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override fun getUserByTypePaginated(type: UserType): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = Constant.PAGE_SIZE),
        ) {
            FirestoreUsersPagingSource(
                userDataRef
                    .orderBy(Constant.CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(Constant.PAGE_LIMIT)
            )
        }.flow.map { pagingData -> pagingData.map { userEMapper.toDomain(it) } }
    }

    override suspend fun addUser(user: User): Resource<User> {
        return suspendCoroutine { continuation ->

            val userEntity = userEMapper.toEntity(user)
                .toMap().toMutableMap().apply {
                    put(CREATED_AT_FIELD_NAME, Timestamp.now())
                }

            userDataRef
                .document(user.id.ifEmpty { user.email })
                .set(userEntity)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(
                            Resource.Success(
                                user
                            )
                        )
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }

        }
    }

}