package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.AuthLoginRequestPayload
import com.buddhatutors.domain.AuthSignupRequestPayload
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.User


interface AuthDataSource {

    suspend fun signUp(
        method: String,
        authSignupRequestPayload: AuthSignupRequestPayload
    ): Resource<User>

    suspend fun signIn(
        method: String,
        authLoginRequestPayload: AuthLoginRequestPayload
    ): Resource<User>

    suspend fun logout(): Resource<Boolean>

}