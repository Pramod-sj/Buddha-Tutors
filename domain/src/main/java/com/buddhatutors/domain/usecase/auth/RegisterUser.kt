package com.buddhatutors.domain.usecase.auth

import com.buddhatutors.domain.AuthSignupRequestPayload
import com.buddhatutors.domain.EMAIL_SIGN_UP_METHOD_NAME
import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.user.User
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class RegisterUser @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    suspend operator fun invoke(model: User, pass: String): Resource<User> {
        return if (model.email.isEmpty()) {
            Resource.Error(Throwable("Email cannot be empty"))
        } else if (pass.isEmpty()) {
            Resource.Error(Throwable("Password cannot be empty"))
        } else {
            authDataSource.signUp(
                method = EMAIL_SIGN_UP_METHOD_NAME,
                authSignupRequestPayload = AuthSignupRequestPayload.EmailPasswordAuthSignupRequestPayload(
                    user = model,
                    password = pass
                )
            )
        }
    }

}