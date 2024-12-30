package com.buddhatutors.auth.domain.usecase

import android.util.Patterns
import javax.inject.Inject


internal class ValidateForgotPasswordDataUseCase @Inject constructor() {

    operator fun invoke(email: String?): Boolean? {
        return email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
    }

}