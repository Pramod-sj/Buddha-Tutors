package com.buddhatutors.core.auth.data

import com.buddhatutors.core.auth.domain.AuthSignupResult
import com.buddhatutors.core.auth.domain.SignupHandler
import com.buddhatutors.core.auth.domain.model.AuthSignupRequestPayload
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class AuthSignupResultSuccess(val uid: String, val emailVerificationSent: Boolean) :
    AuthSignupResult

data class AuthSignupResultFailure(val message: String) :
    AuthSignupResult


class EmailPasswordSignupHandler @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : SignupHandler {

    override suspend fun signUp(authSignupRequestPayload: AuthSignupRequestPayload): AuthSignupResult {
        return if (authSignupRequestPayload is AuthSignupRequestPayload.EmailPasswordAuthSignupRequestPayload) {
            suspendCoroutine { continuation ->

                firebaseAuth.createUserWithEmailAndPassword(
                    authSignupRequestPayload.user.email,
                    authSignupRequestPayload.password
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.user
                        if (user != null) {
                            user.sendEmailVerification()
                                .addOnCompleteListener { result ->
                                    if (result.isSuccessful) {
                                        continuation.resume(
                                            AuthSignupResultSuccess(
                                                uid = user.uid,
                                                emailVerificationSent = true
                                            )
                                        )
                                    } else {
                                        firebaseAuth.signOut()
                                        continuation.resume(AuthSignupResultFailure("Failed to send verification to provided email id"))
                                    }
                                }
                        } else {
                            continuation.resume(AuthSignupResultFailure("User not present in Auth result"))
                        }
                    } else {
                        continuation.resume(AuthSignupResultFailure(task.exception?.message.orEmpty()))
                    }
                }
            }
        } else AuthSignupResultFailure("Authentication payload not supported")
    }

}