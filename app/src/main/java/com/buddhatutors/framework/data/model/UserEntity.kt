package com.buddhatutors.framework.data.model

import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.User
import com.buddhatutors.domain.model.registration.TimeSlot

data class UserEntity(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val expertiseIn: List<Topic> = emptyList(),
    val availabilityDay: List<String> = emptyList(),
    val timeAvailability: TimeSlot? = null,
    val userType: Int = -1
)


fun UserEntity.toDomain(): User? {
    return when (userType) {

        User.UserType.STUDENT.id -> {
            User.Student(
                id = id,
                name = name,
                email = email
            )
        }

        User.UserType.TUTOR.id -> {
            User.Tutor(
                id = id,
                name = name,
                email = email,
                expertiseIn = expertiseIn,
                availabilityDay = availabilityDay,
                timeAvailability = timeAvailability,
            )
        }

        else -> null

    }
}


fun User.toEntity(): UserEntity {
    return when (this) {

        is User.Student -> {
            UserEntity(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }

        is User.Tutor -> {
            UserEntity(
                id = id,
                name = name,
                email = email,
                expertiseIn = expertiseIn.orEmpty(),
                availabilityDay = availabilityDay.orEmpty(),
                timeAvailability = timeAvailability ?: TimeSlot(null, null),
                userType = userType.id
            )
        }
    }
}
