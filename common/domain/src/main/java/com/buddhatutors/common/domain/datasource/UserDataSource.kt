package com.buddhatutors.common.domain.datasource

import androidx.paging.PagingData
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun addUser(user: User): Resource<User>

    suspend fun getUser(userId: String): Resource<User>

    suspend fun getUserByEmail(emailId: String): Resource<User>

    suspend fun getUserByType(type: UserType): Resource<List<User>>

    fun getUserByTypePaginated(type: UserType): Flow<PagingData<User>>

}