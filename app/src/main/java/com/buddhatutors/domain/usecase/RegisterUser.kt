package com.buddhatutors.domain.usecase

import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.model.User
import com.buddhatutors.domain.model.registration.UserRegistrationModel
import com.buddhatutors.framework.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class RegisterUser @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    suspend operator fun invoke(model: User, pass: String): Resource<User> {
        return if (model.email.isEmpty()) {
            Resource.Error(Throwable("Email cannot be empty"))
        } else if (pass.isEmpty()) {
            Resource.Error(Throwable("Password cannot be empty"))
        } else {
            authDataSource.register(model, pass)
        }
    }

}