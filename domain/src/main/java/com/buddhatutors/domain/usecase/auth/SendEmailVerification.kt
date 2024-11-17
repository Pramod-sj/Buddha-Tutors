package com.buddhatutors.domain.usecase.auth

import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.model.Resource
import javax.inject.Inject

class SendEmailVerification @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    suspend operator fun invoke(): Resource<Boolean> {
        return authDataSource.sendVerificationEmail()
    }

}