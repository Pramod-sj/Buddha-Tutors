package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.common.domain.model.Resource
import javax.inject.Inject

internal class SendForgotPasswordUseCase @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    suspend operator fun invoke(email: String): Resource<Boolean> {
        return authDataSource.sendForgotPasswordEmail(email)
    }


}