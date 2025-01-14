package com.buddhatutors.common.domain.usecase

import com.buddhatutors.common.domain.datasource.RemoteConfigSource
import com.buddhatutors.common.domain.model.Resource
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class GetLanguages @Inject constructor(
    private val remoteConfigSource: RemoteConfigSource
) {

    suspend operator fun invoke(): Resource<List<String>> {
        val type = TypeToken.getParameterized(List::class.java, String::class.java).type
        return remoteConfigSource.getTypedData<List<String>>("tutor_languages", type).let {
            Resource.Success(it.orEmpty())
        }
    }

}