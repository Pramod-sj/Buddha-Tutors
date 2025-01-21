package com.buddhatutors.core.auth.domain.usecase

import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.core.auth.domain.EMAIL_SIGN_UP_METHOD_NAME
import com.buddhatutors.core.auth.domain.model.AuthSignupRequestPayload
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.Topic
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.tutorlisting.Verification
import com.buddhatutors.model.user.User
import com.buddhatutors.model.user.UserType
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val authService: AuthService,
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
            val resource = authService.signUp(
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