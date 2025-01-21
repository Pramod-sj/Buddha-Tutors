package com.buddhatutors.core.auth.domain.usecase

import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.model.Resource
import javax.inject.Inject

class SendEmailVerification @Inject constructor(
    private val authService: AuthService
) {

    suspend operator fun invoke(): Resource<Boolean> {
        return authService.sendVerificationEmail()
    }

}