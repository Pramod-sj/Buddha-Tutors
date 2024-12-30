package com.buddhatutors.auth.data.datasourceimpl

import com.buddhatutors.auth.data.AuthResultFailure
import com.buddhatutors.auth.data.AuthResultSuccess
import com.buddhatutors.auth.data.AuthSignupResultFailure
import com.buddhatutors.auth.data.AuthSignupResultSuccess
import com.buddhatutors.auth.domain.AuthLoginRequestPayload
import com.buddhatutors.auth.domain.AuthSignupRequestPayload
import com.buddhatutors.auth.domain.EmailVerificationService
import com.buddhatutors.auth.domain.ForgotPasswordService
import com.buddhatutors.auth.domain.LoginHandler
import com.buddhatutors.auth.domain.SignupHandler
import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.common.domain.datasource.UserDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    private val loginHandlers: Map<String, @JvmSuppressWildcards LoginHandler>,
    private val signupHandlers: Map<String, @JvmSuppressWildcards SignupHandler>,
    private val userDataSource: UserDataSource,
    private val userCreationService: UserCreationService,
    private val emailVerificationService: EmailVerificationService,
    private val forgotPasswordService: ForgotPasswordService
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

                when (val resource = userDataSource.addUser(userWithGuid)) {

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

    override suspend fun sendForgotPasswordEmail(email: String): Resource<Boolean> {
        return forgotPasswordService.sendForgotPasswordEmail(email)
    }

    override suspend fun logout(): Resource<Boolean> {
        FirebaseAuth.getInstance().signOut()
        return Resource.Success(true)
    }

}


class UserCreationService @Inject constructor() {

    fun createUserWithGuid(
        user: User,
        result: AuthSignupResultSuccess
    ): User {
        return user.copy(id = result.uid)
    }

}