package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.data.model.toEntity
import com.buddhatutors.domain.PreferenceManager
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.model.user.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserSessionDataSourceImpl @Inject constructor(
    private val preferencesManager: PreferenceManager<String>
) : UserSessionDataSource {

    private val gson = Gson()

    companion object {
        private const val USER_PREF_KEY = "user"
    }

    override suspend fun clearSession() {
        withContext(Dispatchers.IO) { preferencesManager.remove(USER_PREF_KEY) }
    }

    override suspend fun saveAuthToken(token: User) {
        withContext(Dispatchers.IO) {
            preferencesManager.set(
                USER_PREF_KEY,
                gson.toJson(token.toEntity())
            )
        }
    }

    override suspend fun getAuthToken(): Flow<User?> {
        return withContext(Dispatchers.IO) {
            preferencesManager.get(USER_PREF_KEY)
                .map {
                    gson.fromJson(it, UserEntity::class.java)?.toDomain()
                }
        }
    }
}