package com.buddhatutors.auth

import com.buddhatutors.domain.AuthLoginRequestPayload
import com.buddhatutors.domain.AuthLoginResult
import com.buddhatutors.domain.LoginHandler
import com.buddhatutors.domain.OAuthCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class OAuthCredentialImpl(val authCredential: AuthCredential) : OAuthCredential

data class AuthResultSuccess(val uid: String) : AuthLoginResult

data class AuthResultFailure(val message: String) : AuthLoginResult


class EmailPasswordLoginHandler @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginHandler {

    override suspend fun signIn(authLoginRequestPayload: AuthLoginRequestPayload): AuthLoginResult {
        return if (authLoginRequestPayload is AuthLoginRequestPayload.EmailPasswordLoginRequestPayload) {
            suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(
                    authLoginRequestPayload.email,
                    authLoginRequestPayload.password
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(AuthResultSuccess(it.result.user?.uid.orEmpty()))
                    } else {
                        continuation.resume(AuthResultFailure(it.exception?.message.orEmpty()))
                    }
                }
            }
        } else AuthResultFailure("Authentication payload not supported")
    }

}


class GoogleLoginHandler @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginHandler {

    override suspend fun signIn(authLoginRequestPayload: AuthLoginRequestPayload): AuthLoginResult {
        return if (authLoginRequestPayload is AuthLoginRequestPayload.OAuthLoginRequestPayload) {
            suspendCoroutine { continuation ->
                firebaseAuth.signInWithCredential((authLoginRequestPayload.authCredential as OAuthCredentialImpl).authCredential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            continuation.resume(AuthResultSuccess(it.result.user?.uid.orEmpty()))
                        } else {
                            continuation.resume(AuthResultFailure(it.exception?.message.orEmpty()))
                        }
                    }
            }
        } else AuthResultFailure("Authentication payload not supported")
    }

}
