package com.buddhatutors.domain.usecase.admin

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.TutorListing
import com.buddhatutors.domain.model.user.Admin
import com.buddhatutors.domain.model.user.MasterTutor
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import javax.inject.Inject

class UpdateTutorVerificationState @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(
        tutor: Tutor,
        user: User,
        isApproved: Boolean
    ): Resource<TutorListing> {
        return if (user is Admin || user is MasterTutor) {
            tutorListingDataSource
                .updateTutorVerifiedStatus(
                    tutor = tutor,
                    verifiedByUser = user,
                    isApproved = isApproved
                )
        } else {
            Resource.Error(Throwable("Operation not allowed"))
        }
    }

}