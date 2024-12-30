package com.buddhatutors.auth.data

import com.buddhatutors.auth.domain.ForgotPasswordService
import com.buddhatutors.common.domain.model.Resource
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class FirebaseForgotPasswordServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ForgotPasswordService {

    override suspend fun sendForgotPasswordEmail(email: String): Resource<Boolean> {
        return suspendCoroutine { continuation ->
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    continuation.resume(Resource.Success(true))
                } else {
                    continuation.resume(Resource.Error(Throwable(result.exception?.message.orEmpty())))
                }
            }
        }
    }

}