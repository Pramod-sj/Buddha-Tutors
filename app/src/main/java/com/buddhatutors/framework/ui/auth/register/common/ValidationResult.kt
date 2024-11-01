package com.buddhatutors.framework.ui.auth.register.common

// ValidationResult data class
data class ValidationResult(
    val isNameValid: Boolean,
    val isEmailValid: Boolean,
    val isPasswordValid: Boolean,
    val isConfirmPasswordValid: Boolean,
    val isAvailabilityDaySelected: Boolean? = null,
    val isTimeSlotValid: Boolean? = null,
    val isTopicsSelected: Boolean? = null
) {

    val isValid: Boolean
        get() = isNameValid && isEmailValid && isPasswordValid &&
                isConfirmPasswordValid &&
                (isAvailabilityDaySelected ?: true) &&
                (isTimeSlotValid ?: true) &&
                (isTopicsSelected ?: true)

}
