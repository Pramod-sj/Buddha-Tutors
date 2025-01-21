package com.buddhatutors.core.auth.domain.usecase

import android.util.Patterns
import javax.inject.Inject

// ValidationResult data class
data class LoginValidationResult(
    val isEmailValid: Boolean? = null,
    val isPasswordValid: Boolean? = null,
) {

    val isValid: Boolean
        get() = isEmailValid == true && isPasswordValid == true

}


class ValidateLoginDataUseCase @Inject constructor() {


    operator fun invoke(email: String?, password: String?): LoginValidationResult {
        return LoginValidationResult(
            isEmailValid = email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() },
            isPasswordValid = password?.let { password.length >= 6 }
        )
    }

}