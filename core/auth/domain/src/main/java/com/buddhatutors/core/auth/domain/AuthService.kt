package com.buddhatutors.core.auth.domain

import com.buddhatutors.core.auth.domain.model.AuthLoginRequestPayload
import com.buddhatutors.core.auth.domain.model.AuthSignupRequestPayload
import com.buddhatutors.model.Resource
import com.buddhatutors.model.user.User


interface AuthService {

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