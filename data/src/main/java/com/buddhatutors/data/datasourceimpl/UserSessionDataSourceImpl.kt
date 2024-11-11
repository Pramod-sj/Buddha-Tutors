package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.AdminE
import com.buddhatutors.data.model.MasterTutorE
import com.buddhatutors.data.model.StudentE
import com.buddhatutors.data.model.TutorE
import com.buddhatutors.data.model.UserEntity
import com.buddhatutors.data.model.toDomain
import com.buddhatutors.data.model.toEntity
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
import com.google.gson.TypeAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import javax.inject.Inject

internal class UserSessionDataSourceImpl @Inject constructor(
    private val preferencesManager: PreferenceManager<String>
) : UserSessionDataSource {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(UserEntity::class.java, UserEntityAdapter())
        .create()

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
                .map { gson.fromJson(it, UserEntity::class.java)?.toDomain() }
        }
    }
}


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
}