package com.buddhatutors.domain.usecase.auth

import com.buddhatutors.domain.AuthSignupRequestPayload
import com.buddhatutors.domain.EMAIL_SIGN_UP_METHOD_NAME
import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.Verification
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(
        model: User,
        pass: String,

        expertiseIn: List<Topic> = emptyList(),
        availabilityDay: List<String> = emptyList(),
        timeSlots: List<TimeSlot> = emptyList(),
    ): Resource<User> {
        return if (model.email.isEmpty()) {
            Resource.Error(Throwable("Email cannot be empty"))
        } else if (pass.isEmpty()) {
            Resource.Error(Throwable("Password cannot be empty"))
        } else {
            val resource = authDataSource.signUp(
                method = EMAIL_SIGN_UP_METHOD_NAME,
                authSignupRequestPayload = AuthSignupRequestPayload.EmailPasswordAuthSignupRequestPayload(
                    user = model,
                    password = pass
                )
            )
            when (resource) {
                is Resource.Error -> resource
                is Resource.Success -> {
                    if (model.userType == UserType.TUTOR) {
                        val tutorListingResource = tutorListingDataSource.addTutorListing(
                            TutorListing(
                                tutorUser = resource.data,
                                expertiseIn = expertiseIn,
                                availableDays = availabilityDay,
                                availableTimeSlots = timeSlots,
                                verification = Verification(isApproved = false),
                            )
                        )
                        when (tutorListingResource) {
                            is Resource.Error -> tutorListingResource
                            is Resource.Success -> resource
                        }
                    } else resource
                }
            }
        }
    }

}