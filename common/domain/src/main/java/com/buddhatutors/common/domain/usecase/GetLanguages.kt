package com.buddhatutors.common.domain.usecase

import com.buddhatutors.common.domain.model.Resource
import javax.inject.Inject

class GetLanguages @Inject constructor() {

    suspend operator fun invoke(): Resource<List<String>> {
        return Resource.Success(listOf("English", "Hindi", "Tamil", "Telugu"))
    }

}