package com.buddhatutors.domain

import com.buddhatutors.domain.model.Resource

interface EmailVerificationService {

    suspend fun sendVerificationEmail(): Resource<Boolean>

}