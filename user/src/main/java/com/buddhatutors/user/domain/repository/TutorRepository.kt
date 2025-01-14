package com.buddhatutors.user.domain.repository

import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import javax.inject.Inject

internal class TutorRepository @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend fun getTutorListing(tutorId: String): Resource<TutorListing> {
        return tutorListingDataSource.getTutorListingById(tutorId)
    }

    suspend fun updateTutor(
        tutorListing: TutorListing
    ): Resource<TutorListing> {
        return tutorListingDataSource.updateTutorListing(tutorListing)
    }

}