package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.Admin
import com.buddhatutors.domain.model.user.MasterTutor
import com.buddhatutors.domain.model.user.Student
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

internal class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDataSource: UserDataSource
) : AuthDataSource {

    override suspend fun register(user: User, pass: String): Resource<User> {
        return suspendCancellableCoroutine { continuation ->
            var job: Job? = null
            firebaseAuth.createUserWithEmailAndPassword(user.email, pass)
                .addOnCompleteListener { task ->
                    job = CoroutineScope(continuation.context).launch {
                        if (task.isSuccessful) {

                            val userWithGuid = when (user) {
                                is Student -> user.copy(id = task.result.user?.uid.orEmpty())
                                is Tutor -> user.copy(id = task.result.user?.uid.orEmpty())
                                is Admin -> user.copy(id = task.result.user?.uid.orEmpty())
                                is MasterTutor -> user.copy(id = task.result.user?.uid.orEmpty())
                            }

                            when (val resource =
                                userDataSource.setUser(userWithGuid)) {
                                is Resource.Error -> {
                                    continuation.resume(Resource.Error(resource.throwable))
                                }

                                is Resource.Success -> {
                                    continuation.resume(Resource.Success(user))
                                }
                            }
                        } else {
                            continuation.resume(Resource.Error(task.exception ?: Exception("")))
                        }
                    }
                }
            continuation.invokeOnCancellation {
                job?.cancel()
            }
        }
    }

    override suspend fun login(email: String, pass: String): Resource<User> {
        return suspendCancellableCoroutine { continuation ->

            var getUserDataJob: Job? = null

            firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        if (user != null) {
                            getUserDataJob = CoroutineScope(continuation.context).launch {
                                try {
                                    val resource = userDataSource.getUser(user.uid)
                                    continuation.resume(resource)
                                } catch (e: Exception) {
                                    continuation.resume(Resource.Error(e))
                                }
                            }
                        } else {
                            // Handle the case where user is null
                            continuation.resume(Resource.Error(Exception("User is null")))
                        }
                    } else {
                        // Resume continuation with an error
                        continuation.resume(
                            Resource.Error(
                                task.exception ?: Exception("Unknown error")
                            )
                        )
                    }
                }

            // If coroutine is cancelled, cancel the task
            continuation.invokeOnCancellation {
                getUserDataJob?.cancel()
                firebaseAuth.signOut() // or handle any required cleanup
            }
        }
    }

    override suspend fun logout(): Resource<Boolean> {
        FirebaseAuth.getInstance().signOut()
        return Resource.Success(true)
    }

}