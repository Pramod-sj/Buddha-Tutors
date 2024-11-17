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

data class AuthResultFailure(val throwable: Throwable) : AuthLoginResult

data class EmailNotVerifiedException(override val message: String) : Throwable()

class EmailPasswordLoginHandler @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginHandler {

    override suspend fun signIn(authLoginRequestPayload: AuthLoginRequestPayload): AuthLoginResult {
        return if (authLoginRequestPayload is AuthLoginRequestPayload.EmailPasswordLoginRequestPayload) {
            suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(
                    authLoginRequestPayload.email,
                    authLoginRequestPayload.password
                ).addOnCompleteListener { result ->
                    val user = result.result.user
                    if (result.isSuccessful && user != null) {
                        if (user.isEmailVerified) {
                            continuation.resume(AuthResultSuccess(result.result.user?.uid.orEmpty()))
                        } else {
                            continuation.resume(AuthResultFailure(EmailNotVerifiedException("Your email is not verified. Please verify your email to continue.")))
                        }
                    } else {
                        continuation.resume(AuthResultFailure(Throwable(result.exception?.message.orEmpty())))
                    }
                }
            }
        } else AuthResultFailure(Throwable("Authentication payload not supported"))
    }

}


class FirebaseGoogleLoginHandler @Inject constructor(
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
                            continuation.resume(AuthResultFailure(Throwable(it.exception?.message.orEmpty())))
                        }
                    }
            }
        } else AuthResultFailure(Throwable("Authentication payload not supported"))
    }

}
