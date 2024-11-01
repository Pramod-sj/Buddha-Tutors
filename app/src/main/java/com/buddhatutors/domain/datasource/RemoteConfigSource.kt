package com.buddhatutors.domain.datasource

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type
import javax.inject.Inject

interface RemoteConfigSource {

    companion object {

        const val TERM_AND_CONDITION_KEY = "term_and_conditions"

    }


    suspend fun fetchAndActivate(): Result<Boolean>

    fun getString(key: String): String

    fun getBoolean(key: String): Boolean

    fun getLong(key: String): Long

    fun getDouble(key: String): Double

    fun <T> getTypedData(key: String, type: Type): T?

    val configUpdates: StateFlow<Map<String, Any>>

}

class FirebaseRemoteConfigManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : RemoteConfigSource {

    private val gson = Gson()

    private val _configUpdates = MutableStateFlow<Map<String, Any>>(emptyMap())

    override val configUpdates: StateFlow<Map<String, Any>> = _configUpdates


    init {
        remoteConfig.setDefaultsAsync(emptyMap())
    }

    override suspend fun fetchAndActivate(): Result<Boolean> {
        return try {
            val isActivated = remoteConfig.fetchAndActivate().await()
            _configUpdates.value = remoteConfig.all.mapValues { it.value.asString() }
            Result.success(isActivated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getString(key: String): String = remoteConfig.getString(key)

    override fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)

    override fun getLong(key: String): Long = remoteConfig.getLong(key)

    override fun getDouble(key: String): Double = remoteConfig.getDouble(key)

    override fun <T> getTypedData(key: String, type: Type): T? {
        val jsonString = remoteConfig.getString(key)
        return try {
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            null
        }
    }

}
