package com.buddhatutors.domain.usecase.auth

import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.model.Resource
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