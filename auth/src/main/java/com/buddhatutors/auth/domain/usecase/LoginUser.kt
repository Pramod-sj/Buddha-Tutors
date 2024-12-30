package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.auth.domain.AuthLoginRequestPayload
import com.buddhatutors.auth.domain.EMAIL_SIGN_IN_METHOD_NAME
import com.buddhatutors.auth.domain.datasource.UserSessionDataSource
import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.user.User
import javax.inject.Inject


class LoginUser @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userSessionDataSource: UserSessionDataSource
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
            val resource = authDataSource.signIn(
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