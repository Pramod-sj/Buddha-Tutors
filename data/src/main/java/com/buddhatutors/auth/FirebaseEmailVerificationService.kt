package com.buddhatutors.auth

import com.buddhatutors.domain.EmailVerificationService
import com.buddhatutors.domain.model.Resource
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseEmailVerificationService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : EmailVerificationService {

    override suspend fun sendVerificationEmail(): Resource<Boolean> {
        return suspendCoroutine { continuation ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                if (user.isEmailVerified) {
                    continuation.resume(Resource.Error(Throwable("User is already verified!")))
                } else {
                    user.sendEmailVerification().addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            continuation.resume(Resource.Success(true))
                        } else {
                            continuation.resume(Resource.Error(Throwable(result.exception?.message.orEmpty())))
                        }
                    }
                }
            } else {
                continuation.resume(Resource.Error(Throwable("Firebase User is not logged in ")))
            }
        }
    }

}