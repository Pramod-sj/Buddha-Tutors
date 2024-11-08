package com.buddhatutors.domain.usecase.admin

import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import javax.inject.Inject

class GetAllUnverifiedTutorUsers @Inject constructor(
    private val userDataSource: UserDataSource
) {

    suspend operator fun invoke(): Resource<List<Tutor>> {
        return when (val resource = userDataSource.getUserByType(UserType.TUTOR)) {
            is Resource.Error -> {
                Resource.Error(resource.throwable)
            }

            is Resource.Success -> {
                Resource.Success(resource.data.filterIsInstance<Tutor>())
            }
        }
    }

}