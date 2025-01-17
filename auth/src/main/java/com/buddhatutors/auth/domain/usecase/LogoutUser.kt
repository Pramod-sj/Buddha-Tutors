package com.buddhatutors.auth.domain.usecase

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.auth.domain.datasource.UserSessionDataSource
import com.buddhatutors.common.domain.model.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LogoutUser @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataSource: AuthDataSource,
    private val userSessionDataSource: UserSessionDataSource
) {

    suspend operator fun invoke(): Resource<Boolean> {
        authDataSource.logout()
        userSessionDataSource.clearSession()
        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        return Resource.Success(true)
    }

}