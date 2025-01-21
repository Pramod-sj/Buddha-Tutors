package com.buddhatutors.core.auth.domain.model


interface OAuthCredential

sealed class AuthLoginRequestPayload {

    data class EmailPasswordLoginRequestPayload(val email: String, val password: String) :
        AuthLoginRequestPayload()

    data class OAuthLoginRequestPayload(val authCredential: OAuthCredential) :
        AuthLoginRequestPayload()

}
