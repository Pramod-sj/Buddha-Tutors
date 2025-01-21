package com.buddhatutors.domain.usecase.tutor

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import javax.inject.Inject

internal class GetTutorListingByTutorId @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(tutorId: String): Resource<TutorListing> {
        return tutorListingDataSource.getTutorListingById(tutorId)
    }

}