package com.buddhatutors.core.auth.domain.usecase

import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.model.Resource
import javax.inject.Inject

class SendForgotPasswordUseCase @Inject constructor(
    private val authService: AuthService
) {

    suspend operator fun invoke(email: String): Resource<Boolean> {
        return authService.sendForgotPasswordEmail(email)
    }


}