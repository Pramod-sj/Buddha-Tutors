package com.buddhatutors.domain.usecase.admin

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.TutorListing
import javax.inject.Inject

class GetTutorListingByTutorId @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {
    suspend operator fun invoke(tutorId: String): Resource<TutorListing> {
        return tutorListingDataSource.getTutorListingById(tutorId = tutorId)
    }

}