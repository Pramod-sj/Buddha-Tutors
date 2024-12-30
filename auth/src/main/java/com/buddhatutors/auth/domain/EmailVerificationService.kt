package com.buddhatutors.auth.domain

import com.buddhatutors.common.domain.model.Resource

interface EmailVerificationService {

    suspend fun sendVerificationEmail(): Resource<Boolean>

}