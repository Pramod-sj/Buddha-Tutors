package com.buddhatutors.auth

import com.buddhatutors.domain.AuthSignupRequestPayload
import com.buddhatutors.domain.AuthSignupResult
import com.buddhatutors.domain.SignupHandler
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class AuthSignupResultSuccess(val uid: String) : AuthSignupResult

data class AuthSignupResultFailure(val message: String) : AuthSignupResult


class EmailPasswordSignupHandler @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : SignupHandler {

    override suspend fun signUp(authSignupRequestPayload: AuthSignupRequestPayload): AuthSignupResult {
        return if (authSignupRequestPayload is AuthSignupRequestPayload.EmailPasswordAuthSignupRequestPayload) {
            suspendCoroutine { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(
                    authSignupRequestPayload.user.email,
                    authSignupRequestPayload.password
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(AuthSignupResultSuccess(it.result.user?.uid.orEmpty()))
                    } else {
                        continuation.resume(AuthSignupResultFailure(it.exception?.message.orEmpty()))
                    }
                }
            }
        } else AuthSignupResultFailure("Authentication payload not supported")
    }

}