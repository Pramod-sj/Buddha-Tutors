package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.UserEMapper
import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.domain.PreferenceManager
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import javax.inject.Inject

internal class UserSessionDataSourceImpl @Inject constructor(
    private val preferencesManager: PreferenceManager<String>,
    private val userEMapper: UserEMapper
) : UserSessionDataSource {

    private val gson: Gson = GsonBuilder().create()

    companion object {
        private const val USER_PREF_KEY = "user"
        private const val USER_TOKEN_PREF_KEY = "user_tokens"
    }

    override suspend fun clearSession() {
        withContext(Dispatchers.IO) {
            preferencesManager.remove(USER_PREF_KEY)
            preferencesManager.remove(USER_TOKEN_PREF_KEY)
        }
    }

    override suspend fun saveUserSession(token: User) {
        withContext(Dispatchers.IO) {
            preferencesManager.set(
                USER_PREF_KEY,
                gson.toJson(userEMapper.toEntity(token))
            )
        }
    }

    override suspend fun getUserSession(): Flow<User?> {
        return withContext(Dispatchers.IO) {
            preferencesManager.get(USER_PREF_KEY)
                .map { gson.fromJson(it, UserEntity::class.java)?.let { userEMapper.toDomain(it) } }
        }
    }

    private val mapStringStringType =
        TypeToken.getParameterized(Map::class.java, String::class.java, String::class.java).type

    override suspend fun saveUserTokens(map: Map<String, String>) {
        withContext(Dispatchers.IO) {
            val currTokenMap = preferencesManager.get(USER_TOKEN_PREF_KEY).map {
                gson.fromJson<Map<String, String>>(it, mapStringStringType)
            }.firstOrNull().orEmpty().toMutableMap()
            currTokenMap.putAll(map)
            preferencesManager.set(USER_TOKEN_PREF_KEY, gson.toJson(currTokenMap))
        }
    }

    override suspend fun getUserToken(key: String): String? {
        return preferencesManager.get(USER_TOKEN_PREF_KEY).map {
            gson.fromJson<Map<String, String>>(it, mapStringStringType).orEmpty()
        }.map { it[key] }.firstOrNull()
    }

    override suspend fun removeToken(key: String) {
        withContext(Dispatchers.IO) {
            val currTokenMap = preferencesManager.get(USER_TOKEN_PREF_KEY).map {
                gson.fromJson<Map<String, String>>(it, mapStringStringType).orEmpty()
            }.firstOrNull().orEmpty().toMutableMap()
            currTokenMap.remove(key)
            preferencesManager.set(USER_TOKEN_PREF_KEY, gson.toJson(currTokenMap))
        }
    }
}

/*

// Custom TypeAdapter for UserEntity
class UserEntityAdapter : JsonDeserializer<UserEntity> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): UserEntity {
        val jsonObject = json.asJsonObject
        return when (val type = jsonObject.get("userType").asInt) {
            UserType.STUDENT.id -> context.deserialize<StudentE>(jsonObject, StudentE::class.java)
            UserType.TUTOR.id -> context.deserialize<TutorE>(jsonObject, TutorE::class.java)
            UserType.MASTER_TUTOR.id -> context.deserialize<MasterTutorE>(
                jsonObject,
                MasterTutorE::class.java
            )

            UserType.ADMIN.id -> context.deserialize<AdminE>(jsonObject, AdminE::class.java)
            else -> throw JsonParseException("Unknown type: $type")
        }
    }
}*/
