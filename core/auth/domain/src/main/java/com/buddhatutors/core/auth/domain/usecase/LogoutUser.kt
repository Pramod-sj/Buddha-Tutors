package com.buddhatutors.core.auth.domain.usecase

import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.core.auth.domain.UserSessionPreference
import com.buddhatutors.model.Resource
import javax.inject.Inject

class LogoutUser @Inject constructor(
    private val authService: AuthService,
    private val userSessionDataSource: UserSessionPreference
) {

    suspend operator fun invoke(): Resource<Boolean> {
        authService.logout()
        userSessionDataSource.clearSession()
        //CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        return Resource.Success(true)
    }

}