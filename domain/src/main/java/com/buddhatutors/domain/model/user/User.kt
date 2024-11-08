package com.buddhatutors.domain.model.user

import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.registration.TimeSlot
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


/**
 * Enum class representing different types of users in the application.
 *
 * @property id The unique identifier for each user type.
 */
enum class UserType(val id: Int, val value: String) {

    /**
     * Represents a student user type.
     * The associated ID for this type is 1.
     */
    STUDENT(1, "Students/Parent"),

    /**
     * Represents a tutor user type.
     * The associated ID for this type is 2.
     */
    TUTOR(2, "Tutor"),


    ADMIN(0, "Admin"),


    MASTER_TUTOR(3, "Master tutor")

}

@Serializable
sealed class User {

    abstract val id: String

    abstract val name: String

    abstract val email: String

    abstract val userType: UserType

}

@Serializable
data class Student(
    override val id: String,
    override val name: String,
    override val email: String,
    override val userType: UserType = UserType.STUDENT
) : User()


@Serializable
data class Tutor(
    override val id: String,
    override val name: String,
    override val email: String,
    override val userType: UserType = UserType.TUTOR,
    val expertiseIn: List<Topic>? = null,
    val availabilityDay: List<String>? = null,
    val timeAvailability: TimeSlot? = null,
) : User()


@Serializable
data class Admin(
    override val id: String,
    override val name: String,
    override val email: String,
    override val userType: UserType = UserType.ADMIN,
) : User()


@Serializable
data class MasterTutor(
    override val id: String,
    override val name: String,
    override val email: String,
    override val userType: UserType = UserType.MASTER_TUTOR,
) : User()