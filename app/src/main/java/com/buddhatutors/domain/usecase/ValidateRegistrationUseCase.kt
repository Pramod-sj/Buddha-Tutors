package com.buddhatutors.domain.usecase

import android.util.Patterns
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.User
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.framework.ui.auth.register.common.ValidationResult
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject

class ValidateRegistrationUseCase @Inject constructor() {

    operator fun invoke(
        name: String?,
        email: String?,
        password: String?,
        confirmPassword: String?,
        userType: User.UserType?,
        selectedAvailabilityDay: List<String>?,
        selectedTimeSlot: TimeSlot?,
        selectedTopics: List<Topic>?,
    ): ValidationResult {

        val isStudent = userType == User.UserType.STUDENT

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
