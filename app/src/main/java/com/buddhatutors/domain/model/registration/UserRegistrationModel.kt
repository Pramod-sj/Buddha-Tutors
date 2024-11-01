package com.buddhatutors.domain.model.registration

import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.User

internal data class UserRegistrationModel(
    val name: String,
    val email: String,
    val password: String,
    val userType: User.UserType,
    val expertiseIn: List<Topic>? = null,
    val availabilityDay: List<String>? = null,
    val timeAvailability: TimeSlot? = null,
)