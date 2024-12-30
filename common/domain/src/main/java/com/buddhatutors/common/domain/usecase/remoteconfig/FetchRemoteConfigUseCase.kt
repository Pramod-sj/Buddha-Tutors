package com.buddhatutors.common.domain.usecase.remoteconfig

import com.buddhatutors.common.domain.datasource.RemoteConfigSource
import javax.inject.Inject


class FetchRemoteConfigUseCase @Inject constructor(
    private val remoteConfigSource: RemoteConfigSource
) {

    suspend operator fun invoke(): Result<Boolean> = remoteConfigSource.fetchAndActivate()

}

class GetRemoteConfigValueUseCase @Inject constructor(
    private val remoteConfigSource: RemoteConfigSource
) {

    fun getString(key: String): String = remoteConfigSource.getString(key)

    fun getBoolean(key: String): Boolean = remoteConfigSource.getBoolean(key)

    fun getLong(key: String): Long = remoteConfigSource.getLong(key)

    fun getDouble(key: String): Double = remoteConfigSource.getDouble(key)

    fun <T> getTypedData(key: String, clazz: Class<T>): T? =
        remoteConfigSource.getTypedData(key, clazz)
}