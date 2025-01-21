package com.buddhatutors.core.auth.domain.usecase

import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.core.auth.domain.EMAIL_SIGN_IN_METHOD_NAME
import com.buddhatutors.core.auth.domain.UserSessionPreference
import com.buddhatutors.core.auth.domain.model.AuthLoginRequestPayload
import com.buddhatutors.model.Resource
import com.buddhatutors.model.user.User
import javax.inject.Inject


class LoginUser @Inject constructor(
    private val authService: AuthService,
    private val userSessionDataSource: UserSessionPreference
) {

    suspend operator fun invoke(
        email: String,
        pass: String
    ): Resource<User> {
        return if (email.isEmpty()) {
            Resource.Error(Throwable("Email cannot be empty"))
        } else if (pass.isEmpty()) {
            Resource.Error(Throwable("Password cannot be empty"))
        } else {
            val resource = authService.signIn(
                method = EMAIL_SIGN_IN_METHOD_NAME,
                authLoginRequestPayload = AuthLoginRequestPayload.EmailPasswordLoginRequestPayload(
                    email = email, password = pass
                )
            )
            if (resource is Resource.Success) {
                userSessionDataSource.saveUserSession(resource.data)
            }
            resource
        }
    }

}