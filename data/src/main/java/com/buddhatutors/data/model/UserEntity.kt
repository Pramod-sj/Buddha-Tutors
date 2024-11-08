package com.buddhatutors.data.model

import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.user.Admin
import com.buddhatutors.domain.model.user.MasterTutor
import com.buddhatutors.domain.model.user.Student
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType

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

        UserType.STUDENT.id -> {
            Student(
                id = id,
                name = name,
                email = email
            )
        }

        UserType.TUTOR.id -> {
            Tutor(
                id = id,
                name = name,
                email = email,
                expertiseIn = expertiseIn,
                availabilityDay = availabilityDay,
                timeAvailability = timeAvailability,
            )
        }

        UserType.ADMIN.id -> {
            Admin(
                id = id,
                name = name,
                email = email
            )
        }

        UserType.MASTER_TUTOR.id -> {
            MasterTutor(
                id = id,
                name = name,
                email = email
            )
        }


        else -> null

    }
}


fun User.toEntity(): UserEntity {
    return when (this) {

        is Student -> {
            UserEntity(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }

        is Tutor -> {
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

        is Admin -> {
            UserEntity(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }

        is MasterTutor -> {
            UserEntity(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }
    }
}
