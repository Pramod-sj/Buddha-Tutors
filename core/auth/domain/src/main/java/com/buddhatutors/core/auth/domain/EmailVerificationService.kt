package com.buddhatutors.core.auth.domain

import com.buddhatutors.model.Resource

interface EmailVerificationService {

    suspend fun sendVerificationEmail(): Resource<Boolean>

}