package com.buddhatutors.auth.domain

import com.buddhatutors.common.domain.model.Resource

interface ForgotPasswordService {

    suspend fun sendForgotPasswordEmail(email: String): Resource<Boolean>

}