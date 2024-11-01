package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.User
import com.buddhatutors.framework.Resource

internal interface UserDataSource {

    suspend fun setUser(user: User): Resource<User>

    suspend fun getUser(userId: String): Resource<User>

}