package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.common.domain.model.Resource
import javax.inject.Inject

class SendEmailVerification @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    suspend operator fun invoke(): Resource<Boolean> {
        return authDataSource.sendVerificationEmail()
    }

}