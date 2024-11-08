package com.buddhatutors.domain.usecase.auth

import android.util.Patterns
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.registration.ValidationResult
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ValidateRegistrationUseCase @Inject constructor() {

    operator fun invoke(
        name: String?,
        email: String?,
        password: String?,
        confirmPassword: String?,
        userType: UserType?,
        selectedAvailabilityDay: List<String>?,
        selectedTimeSlot: TimeSlot?,
        selectedTopics: List<Topic>?,
    ): ValidationResult {

        val isStudent = userType == UserType.STUDENT

        return ValidationResult(
            isNameValid = name?.isNotBlank() ?: true,
            isEmailValid = email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } ?: true,
            isPasswordValid = password?.let { password.length >= 6 } ?: true,
            isConfirmPasswordValid = password == confirmPassword,
            isAvailabilityDaySelected = if (isStudent) null else selectedAvailabilityDay?.isNotEmpty(),
            isTimeSlotValid = if (isStudent) null else isTimeSlotValid(
                start = selectedTimeSlot?.start,
                end = selectedTimeSlot?.end
            ),
            isTopicsSelected = if (isStudent) null else selectedTopics?.isNotEmpty()
        )
    }

    private fun isTimeSlotValid(start: String?, end: String?): Boolean {
        if (start == null || end == null) return false

        return try {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startTime = formatter.parse(start)
            val endTime = formatter.parse(end)
            startTime?.before(endTime) ?: false
        } catch (_: Exception) {
            false
        }
    }
}
