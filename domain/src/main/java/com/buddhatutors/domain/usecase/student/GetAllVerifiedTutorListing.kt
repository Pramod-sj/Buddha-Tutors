package com.buddhatutors.domain.usecase.student

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import javax.inject.Inject

class GetAllVerifiedTutorListing @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(): Resource<List<TutorListing>> {
        return tutorListingDataSource.getVerifiedTutorListing()
    }

}