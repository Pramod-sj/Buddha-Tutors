package com.buddhatutors.auth.domain.model.registration

// ValidationResult data class
data class ValidationResult(
    val isNameValid: Boolean?,
    val isEmailValid: Boolean?,
    val isPasswordValid: Boolean?,
    val isConfirmPasswordValid: Boolean?,
    val isAvailabilityDaySelected: Boolean? = null,
    val isTimeSlotValid: Boolean? = null,
    val isTopicsSelected: Boolean? = null,
    val isLanguageSelected: Boolean? = null
) {

    val isValid: Boolean
        get() = isNameValid == true && isEmailValid == true && isPasswordValid == true &&
                isConfirmPasswordValid == true &&
                (isAvailabilityDaySelected ?: true) &&
                (isTimeSlotValid ?: true) &&
                (isTopicsSelected ?: true) &&
                (isLanguageSelected ?: true)

}
