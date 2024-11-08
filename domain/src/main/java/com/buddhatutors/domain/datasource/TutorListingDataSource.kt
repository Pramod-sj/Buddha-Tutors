package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.TutorListing
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User

interface TutorListingDataSource {

    suspend fun getTutorListingById(tutorId: String): Resource<TutorListing>

    suspend fun getTutorListing(): Resource<List<TutorListing>>

    suspend fun updateTutorVerifiedStatus(
        tutor: Tutor,
        verifiedByUser: User,
        isApproved: Boolean
    ): Resource<TutorListing>

}