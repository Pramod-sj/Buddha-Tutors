package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.User
import com.buddhatutors.framework.Resource


internal interface AuthDataSource {

    suspend fun register(user: User, pass: String): Resource<User>

    suspend fun login(email: String, pass: String): Resource<User>

    suspend fun logout(): Resource<Boolean>

}