package com.buddhatutors.auth.domain.datasource

import com.buddhatutors.common.domain.model.user.User
import kotlinx.coroutines.flow.Flow

const val KEY_ID_TOKEN = "id_token"

const val KEY_CALENDAR_SCOPE_ACCESS_TOKEN = "calendar_scope_access_token"

interface UserSessionDataSource {

    suspend fun saveUserSession(token: User)

    suspend fun getUserSession(): Flow<User?>

    suspend fun saveUserTokens(map: Map<String, String>)

    suspend fun getUserToken(key: String): String?

    suspend fun removeToken(key: String)

    suspend fun clearSession()

}