package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.auth.domain.datasource.UserSessionDataSource
import com.buddhatutors.common.domain.model.Resource
import javax.inject.Inject

class LogoutUser @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userSessionDataSource: UserSessionDataSource
) {

    suspend operator fun invoke(): Resource<Boolean> {
        authDataSource.logout()
        userSessionDataSource.clearSession()
        return Resource.Success(true)
    }

}