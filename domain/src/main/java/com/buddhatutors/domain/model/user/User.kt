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
data class User(

    val id: String = "",

    val name: String = "",

    val email: String = "",

    val userType: UserType = UserType.STUDENT
)