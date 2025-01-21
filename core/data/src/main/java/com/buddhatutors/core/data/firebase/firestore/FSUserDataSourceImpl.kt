package com.buddhatutors.core.data.firebase.firestore

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.buddhatutors.core.data.firebase.firestore.constant.Constant
import com.buddhatutors.core.data.firebase.firestore.constant.Constant.CREATED_AT_FIELD_NAME
import com.buddhatutors.core.data.firebase.firestore.constant.FirebaseCollectionConstant.REF_USER
import com.buddhatutors.core.data.firebase.firestore.paging_source.FireStoreUsersPagingSource
import com.buddhatutors.core.data.model.UserEMapper
import com.buddhatutors.core.data.model.UserEntity
import com.buddhatutors.core.data.toMap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class FSUserDataSourceImpl @Inject constructor(
    private val userEMapper: UserEMapper
) : com.buddhatutors.domain.datasource.UserDataSource {

    private val userDataRef = FirebaseFirestore.getInstance().collection(REF_USER)

    override suspend fun getUser(userId: String): com.buddhatutors.model.Resource<com.buddhatutors.model.user.User> {
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
                                com.buddhatutors.model.Resource.Success(user)
                            )
                        } else {
                            continuation.resume(
                                com.buddhatutors.model.Resource.Error(
                                    Exception("No user found")
                                )
                            )
                        }
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getUserByEmail(emailId: String): com.buddhatutors.model.Resource<com.buddhatutors.model.user.User> {
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
                                com.buddhatutors.model.Resource.Success(user)
                            )
                        } else {
                            continuation.resume(
                                com.buddhatutors.model.Resource.Error(
                                    Exception("No user found")
                                )
                            )
                        }
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getUserByType(type: com.buddhatutors.model.user.UserType): com.buddhatutors.model.Resource<List<com.buddhatutors.model.user.User>> {
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
                            com.buddhatutors.model.Resource.Success(
                                users
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override fun getUserByTypePaginated(type: com.buddhatutors.model.user.UserType): Flow<PagingData<com.buddhatutors.model.user.User>> {
        return Pager(
            config = PagingConfig(pageSize = Constant.PAGE_SIZE),
        ) {
            FireStoreUsersPagingSource(
                userDataRef
                    .whereEqualTo("userType", type.id)
                    .orderBy(CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(Constant.PAGE_LIMIT)
            )
        }.flow.map { pagingData -> pagingData.map { userEMapper.toDomain(it) } }
    }

    override suspend fun addUser(user: com.buddhatutors.model.user.User): com.buddhatutors.model.Resource<com.buddhatutors.model.user.User> {
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
                            com.buddhatutors.model.Resource.Success(
                                user
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }

        }
    }

}