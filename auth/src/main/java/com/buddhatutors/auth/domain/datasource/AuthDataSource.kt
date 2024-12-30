package com.buddhatutors.auth.domain.datasource

import com.buddhatutors.auth.domain.AuthLoginRequestPayload
import com.buddhatutors.auth.domain.AuthSignupRequestPayload
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User


interface AuthDataSource {

    suspend fun signUp(
        method: String, authSignupRequestPayload: AuthSignupRequestPayload
    ): Resource<User>

    suspend fun signIn(
        method: String, authLoginRequestPayload: AuthLoginRequestPayload
    ): Resource<User>

    suspend fun sendVerificationEmail(): Resource<Boolean>

    suspend fun sendForgotPasswordEmail(email: String): Resource<Boolean>

    suspend fun logout(): Resource<Boolean>

}