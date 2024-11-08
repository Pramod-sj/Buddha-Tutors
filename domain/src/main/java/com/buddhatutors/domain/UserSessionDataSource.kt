package com.buddhatutors.domain

import com.buddhatutors.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserSessionDataSource {

    suspend fun saveAuthToken(token: User)

    suspend fun getAuthToken(): Flow<User?>

    suspend fun clearSession()

}