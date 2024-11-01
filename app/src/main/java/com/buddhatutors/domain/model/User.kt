package com.buddhatutors.domain.model

import com.buddhatutors.domain.model.registration.TimeSlot

sealed class User(
    open val id: String = "",
    open val name: String,
    open val email: String,
    open val userType: UserType
) {

    constructor() : this("", "", "", UserType.STUDENT)

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

    }

    data class Student(
        override val id: String,
        override val name: String,
        override val email: String,
    ) : User(id = id, name = name, email = email, userType = UserType.STUDENT)

    data class Tutor(
        override val id: String,
        override val name: String,
        override val email: String,
        val expertiseIn: List<Topic>? = null,
        val availabilityDay: List<String>? = null,
        val timeAvailability: TimeSlot? = null,
    ) : User(id = id, name = name, email = email, userType = UserType.TUTOR)

}