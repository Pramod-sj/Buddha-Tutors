package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.auth.domain.AuthSignupRequestPayload
import com.buddhatutors.auth.domain.EMAIL_SIGN_UP_METHOD_NAME
import com.buddhatutors.auth.domain.datasource.AuthDataSource
import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.TimeSlot
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.domain.model.tutorlisting.Verification
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(
        model: User,
        pass: String,
        addByUser: User? = null,
        expertiseIn: List<Topic> = emptyList(),
        languages: List<String> = emptyList(),
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
                                languages = languages,
                                availableDays = availabilityDay,
                                availableTimeSlots = timeSlots,
                                verification = Verification(isApproved = false),
                                addedByUser = addByUser
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