package com.buddhatutors.core.auth.domain

import com.buddhatutors.model.Resource


interface ForgotPasswordService {

    suspend fun sendForgotPasswordEmail(email: String): Resource<Boolean>

}