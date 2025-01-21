package com.buddhatutors.model.registration

import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.Topic
import com.buddhatutors.model.user.UserType

internal data class UserRegistrationModel(
    val name: String,
    val email: String,
    val password: String,
    val userType: UserType,
    val expertiseIn: List<Topic>? = null,
    val availabilityDay: List<String>? = null,
    val timeAvailability: TimeSlot? = null,
)