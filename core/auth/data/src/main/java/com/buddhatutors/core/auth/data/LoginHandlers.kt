package com.buddhatutors.core.auth.data

import com.buddhatutors.core.auth.domain.AuthLoginResult
import com.buddhatutors.core.auth.domain.EmailNotVerifiedException
import com.buddhatutors.core.auth.domain.LoginHandler
import com.buddhatutors.core.auth.domain.model.AuthLoginRequestPayload
import com.buddhatutors.core.auth.domain.model.OAuthCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class OAuthCredentialImpl(val authCredential: AuthCredential) : OAuthCredential

data class AuthResultSuccess(val uid: String, val emailId: String) : AuthLoginResult

data class AuthResultFailure(val throwable: Throwable) : AuthLoginResult

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
                    if (result.isSuccessful) {
                        val user = result.result.user
                        if (user?.isEmailVerified == true) {
                            continuation.resume(
                                AuthResultSuccess(
                                    uid = result.result.user?.uid.orEmpty(),
                                    emailId = result.result.user?.email.orEmpty()
                                )
                            )
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
                            continuation.resume(
                                AuthResultSuccess(
                                    uid = it.result.user?.uid.orEmpty(),
                                    emailId = it.result.user?.email.orEmpty()
                                )
                            )
                        } else {
                            continuation.resume(AuthResultFailure(Throwable(it.exception?.message.orEmpty())))
                        }
                    }
            }
        } else AuthResultFailure(Throwable("Authentication payload not supported"))
    }

}
