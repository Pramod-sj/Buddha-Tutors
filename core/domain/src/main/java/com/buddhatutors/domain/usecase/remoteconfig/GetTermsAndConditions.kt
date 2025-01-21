package com.buddhatutors.domain.usecase.remoteconfig

import com.buddhatutors.domain.datasource.RemoteConfigSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.TermCondition
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class GetTermsAndConditions @Inject constructor(
    private val remoteConfigSource: RemoteConfigSource
) {

    operator fun invoke(): Resource<List<TermCondition>> {

        val type =
            TypeToken.getParameterized(List::class.java, TermCondition::class.java).type

        val data: List<TermCondition>? = remoteConfigSource.getTypedData(
            RemoteConfigSource.TERM_AND_CONDITION_KEY, type
        )

        return if (data != null) Resource.Success(data)
        else Resource.Error(Throwable("No data"))

    }

}