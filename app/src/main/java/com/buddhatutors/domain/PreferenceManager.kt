package com.buddhatutors.domain

import kotlinx.coroutines.flow.Flow

internal interface PreferenceManager<T> {

    suspend fun set(key: String, data: T)

    fun get(key: String): Flow<T?>

    suspend fun remove(key: String)

}