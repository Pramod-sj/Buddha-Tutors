package com.buddhatutors.core.auth.data.datasourceimpl

import com.buddhatutors.core.auth.data.AuthResultFailure
import com.buddhatutors.core.auth.data.AuthResultSuccess
import com.buddhatutors.core.auth.data.AuthSignupResultFailure
import com.buddhatutors.core.auth.data.AuthSignupResultSuccess
import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.core.auth.domain.EmailVerificationService
import com.buddhatutors.core.auth.domain.ForgotPasswordService
import com.buddhatutors.core.auth.domain.LoginHandler
import com.buddhatutors.core.auth.domain.SignupHandler
import com.buddhatutors.core.auth.domain.model.AuthLoginRequestPayload
import com.buddhatutors.core.auth.domain.model.AuthSignupRequestPayload
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.user.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

internal class AuthServiceImpl @Inject constructor(
    private val loginHandlers: Map<String, @JvmSuppressWildcards LoginHandler>,
    private val signupHandlers: Map<String, @JvmSuppressWildcards SignupHandler>,
    private val userDataSource: UserDataSource,
    private val userCreationService: UserCreationService,
    private val emailVerificationService: EmailVerificationService,
    private val forgotPasswordService: ForgotPasswordService
) : AuthService {

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