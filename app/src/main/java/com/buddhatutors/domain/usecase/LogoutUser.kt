package com.buddhatutors.domain.usecase

import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.framework.Resource
import com.buddhatutors.framework.SessionManager
import javax.inject.Inject

internal class LogoutUser @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(): Resource<Boolean> {
        authDataSource.logout()
        sessionManager.clearUser()
        return Resource.Success(true)
    }

}