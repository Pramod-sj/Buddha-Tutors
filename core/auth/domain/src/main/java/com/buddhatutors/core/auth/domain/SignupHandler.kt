package com.buddhatutors.core.auth.domain

import com.buddhatutors.core.auth.domain.model.AuthSignupRequestPayload


interface AuthSignupResult


interface SignupHandler {

    suspend fun signUp(authSignupRequestPayload: AuthSignupRequestPayload): AuthSignupResult

}