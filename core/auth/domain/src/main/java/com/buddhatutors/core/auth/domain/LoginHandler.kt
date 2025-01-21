package com.buddhatutors.core.auth.domain

import com.buddhatutors.core.auth.domain.model.AuthLoginRequestPayload


const val GOOGLE_SIGN_IN_METHOD_NAME = "googleSignIn"
const val EMAIL_SIGN_IN_METHOD_NAME = "emailSignIn"
const val EMAIL_SIGN_UP_METHOD_NAME = "emailSignUp"


interface AuthLoginResult

interface LoginHandler {

    suspend fun signIn(authLoginRequestPayload: AuthLoginRequestPayload): AuthLoginResult

}

