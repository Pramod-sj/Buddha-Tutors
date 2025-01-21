package com.buddhatutors.core.auth.domain.usecase

import android.util.Patterns
import com.buddhatutors.core.auth.domain.model.registration.ValidationResult
import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.Topic
import com.buddhatutors.model.user.UserType
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
        selectedTimeSlots: List<TimeSlot>?,
        selectedTopics: List<Topic>?,
        selectedLanguages: List<String>?
    ): ValidationResult {

        val isNonTutor = userType == UserType.STUDENT || userType == UserType.MASTER_TUTOR

        return ValidationResult(
            isNameValid = name?.isNotBlank(),
            isEmailValid = email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() },
            isPasswordValid = password?.let { password.length >= 6 },
            isConfirmPasswordValid = password == confirmPassword,
            isAvailabilityDaySelected = if (isNonTutor) null else selectedAvailabilityDay?.isNotEmpty(),
            isTimeSlotValid = if (isNonTutor) null else !selectedTimeSlots.isNullOrEmpty() && selectedTimeSlots.all {
                isTimeSlotValid(start = it.start, end = it.end)
            },
            isTopicsSelected = if (isNonTutor) null else selectedTopics?.isNotEmpty(),
            isLanguageSelected = if (isNonTutor) null else selectedLanguages?.isNotEmpty()
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
