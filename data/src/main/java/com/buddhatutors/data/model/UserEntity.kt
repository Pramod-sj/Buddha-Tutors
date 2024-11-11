package com.buddhatutors.data.model

import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.user.Admin
import com.buddhatutors.domain.model.user.MasterTutor
import com.buddhatutors.domain.model.user.Student
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType

sealed class UserEntity {
    abstract val id: String
    abstract val name: String
    abstract val email: String
    abstract val userType: Int
}

data class StudentE(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: Int = UserType.STUDENT.id
) : UserEntity()


data class TutorE(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: Int = UserType.TUTOR.id,
    val expertiseIn: List<Topic>? = null,
    val availabilityDay: List<String>? = null,
    val timeAvailability: TimeSlot? = null,
) : UserEntity()


data class AdminE(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: Int = UserType.ADMIN.id,
) : UserEntity()


data class MasterTutorE(
    override val id: String = "",
    override val name: String = "",
    override val email: String = "",
    override val userType: Int = UserType.MASTER_TUTOR.id,
) : UserEntity()

fun TutorE.toDomain(): Tutor {
    return Tutor(
        id = id,
        name = name,
        email = email,
        expertiseIn = expertiseIn,
        availabilityDay = availabilityDay,
        timeAvailability = timeAvailability,
    )
}

fun Tutor.toEntity(): TutorE {
    return TutorE(
        id = id,
        name = name,
        email = email,
        expertiseIn = expertiseIn,
        availabilityDay = availabilityDay,
        timeAvailability = timeAvailability,
    )
}


fun UserEntity.toDomain(): User {
    return when (this) {

        is StudentE -> {
            Student(
                id = id,
                name = name,
                email = email
            )
        }

        is TutorE -> {
            Tutor(
                id = id,
                name = name,
                email = email,
                expertiseIn = expertiseIn,
                availabilityDay = availabilityDay,
                timeAvailability = timeAvailability,
            )
        }

        is AdminE -> {
            Admin(
                id = id,
                name = name,
                email = email
            )
        }

        is MasterTutorE -> {
            MasterTutor(
                id = id,
                name = name,
                email = email
            )
        }
    }
}


fun User.toEntity(): UserEntity {
    return when (this) {

        is Student -> {
            StudentE(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }

        is Tutor -> {
            TutorE(
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
            AdminE(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }

        is MasterTutor -> {
            MasterTutorE(
                id = id,
                name = name,
                email = email,
                userType = userType.id
            )
        }
    }
}