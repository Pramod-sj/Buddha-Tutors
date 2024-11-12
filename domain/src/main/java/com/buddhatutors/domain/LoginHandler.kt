package com.buddhatutors.domain

import com.buddhatutors.domain.model.user.User


const val GOOGLE_SIGN_IN_METHOD_NAME = "googleSignIn"
const val EMAIL_SIGN_IN_METHOD_NAME = "emailSignIn"
const val EMAIL_SIGN_UP_METHOD_NAME = "emailSignUp"


sealed class AuthLoginRequestPayload {

    data class EmailPasswordLoginRequestPayload(val email: String, val password: String) :
        AuthLoginRequestPayload()

    data class OAuthLoginRequestPayload(val authCredential: OAuthCredential) :
        AuthLoginRequestPayload()

}


interface OAuthCredential


interface AuthLoginResult


interface LoginHandler {

    suspend fun signIn(authLoginRequestPayload: AuthLoginRequestPayload): AuthLoginResult

}


sealed class AuthSignupRequestPayload {

    abstract val user: User

    data class EmailPasswordAuthSignupRequestPayload(
        override val user: User,
        val password: String,
    ) : AuthSignupRequestPayload()

}


interface AuthSignupResult


interface SignupHandler {

    suspend fun signUp(authSignupRequestPayload: AuthSignupRequestPayload): AuthSignupResult

}

