package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.auth.AuthResultFailure
import com.buddhatutors.auth.AuthResultSuccess
import com.buddhatutors.auth.AuthSignupResultFailure
import com.buddhatutors.auth.AuthSignupResultSuccess
import com.buddhatutors.domain.AuthLoginRequestPayload
import com.buddhatutors.domain.AuthSignupRequestPayload
import com.buddhatutors.domain.EmailVerificationService
import com.buddhatutors.domain.LoginHandler
import com.buddhatutors.domain.SignupHandler
import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    private val loginHandlers: Map<String, @JvmSuppressWildcards LoginHandler>,
    private val signupHandlers: Map<String, @JvmSuppressWildcards SignupHandler>,
    private val userDataSource: UserDataSource,
    private val userCreationService: UserCreationService,
    private val emailVerificationService: EmailVerificationService
) : AuthDataSource {

    override suspend fun signUp(
        method: String,
        authSignupRequestPayload: AuthSignupRequestPayload
    ): Resource<User> {
        val handler = signupHandlers[method]
            ?: throw IllegalArgumentException("Signup method $method not supported")

        return when (val result = handler.signUp(authSignupRequestPayload)) {
            is AuthSignupResultSuccess -> {
                val user = authSignupRequestPayload.user

                val userWithGuid = userCreationService.createUserWithGuid(user, result)

                when (val resource = userDataSource.setUser(userWithGuid)) {

                    is Resource.Error -> Resource.Error(resource.throwable)

                    is Resource.Success -> Resource.Success(userWithGuid)
                }
            }

            is AuthSignupResultFailure -> {
                Resource.Error(Throwable(result.message))
            }

            else -> {
                Resource.Error(Throwable("Signup result is unknown for $method"))
            }
        }
    }

    override suspend fun signIn(
        method: String,
        authLoginRequestPayload: AuthLoginRequestPayload
    ): Resource<User> {
        val handler = loginHandlers[method]
            ?: throw IllegalArgumentException("Login method $method not supported")

        return when (val result = handler.signIn(authLoginRequestPayload)) {
            is AuthResultSuccess -> {
                userDataSource.getUser(result.uid)
            }

            is AuthResultFailure -> {
                Resource.Error(result.throwable)
            }

            else -> {
                Resource.Error(Throwable("Login result is unknown for $method"))
            }
        }

    }

    override suspend fun sendVerificationEmail(): Resource<Boolean> {
        return emailVerificationService.sendVerificationEmail()
    }

    override suspend fun logout(): Resource<Boolean> {
        FirebaseAuth.getInstance().signOut()
        return Resource.Success(true)
    }

}


class UserCreationService @Inject constructor() {

    fun createUserWithGuid(user: User, result: AuthSignupResultSuccess): User {
        return user.copy(id = result.uid)
    }

}