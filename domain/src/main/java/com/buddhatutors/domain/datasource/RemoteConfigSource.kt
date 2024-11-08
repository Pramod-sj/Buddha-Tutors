package com.buddhatutors.domain.datasource

import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Type

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