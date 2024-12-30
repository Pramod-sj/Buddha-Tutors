package com.buddhatutors.appadmin.domain.usecase.admin

import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import javax.inject.Inject


class GetTutorListingByTutorId @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(tutorId: String): Resource<TutorListing> {
        return tutorListingDataSource.getTutorListingById(tutorId = tutorId)
    }

}