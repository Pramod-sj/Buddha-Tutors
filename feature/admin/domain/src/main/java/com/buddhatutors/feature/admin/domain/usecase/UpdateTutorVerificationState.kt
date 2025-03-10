package com.buddhatutors.feature.admin.domain.usecase

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.user.User
import com.buddhatutors.model.user.UserType
import javax.inject.Inject

class UpdateTutorVerificationState @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(
        tutorListing: TutorListing,
        user: User,
        isApproved: Boolean
    ): Resource<TutorListing> {
        return if (user.userType in listOf(UserType.ADMIN, UserType.MASTER_TUTOR)) {
            tutorListingDataSource
                .updateTutorVerifiedStatus(
                    tutor = tutorListing,
                    verifiedByUser = user,
                    isApproved = isApproved
                )
        } else {
            Resource.Error(Throwable("Operation not allowed"))
        }
    }

}