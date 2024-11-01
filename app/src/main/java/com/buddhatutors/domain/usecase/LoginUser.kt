package com.buddhatutors.domain.usecase

import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.model.User
import com.buddhatutors.framework.Resource
import com.buddhatutors.framework.SessionManager
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class LoginUser @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(email: String, pass: String): Resource<User> {
        return if (email.isEmpty()) {
            Resource.Error(Throwable("Email cannot be empty"))
        } else if (pass.isEmpty()) {
            Resource.Error(Throwable("Password cannot be empty"))
        } else {
            val resource = authDataSource.login(email, pass)
            if (resource is Resource.Success) {
                sessionManager.setUser(resource.data)
            }
            resource
        }
    }

}