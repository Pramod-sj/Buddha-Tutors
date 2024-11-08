package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType

interface UserDataSource {

    suspend fun setUser(user: User): Resource<User>

    suspend fun getUser(userId: String): Resource<User>

    suspend fun getUserByType(type: UserType): Resource<List<User>>

}