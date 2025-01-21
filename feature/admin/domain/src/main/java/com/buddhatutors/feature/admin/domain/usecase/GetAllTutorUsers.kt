package com.buddhatutors.feature.admin.domain.usecase

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import javax.inject.Inject

class GetAllTutorUsers @Inject constructor(
    private val tutorListing: TutorListingDataSource
) {

    suspend operator fun invoke(): Resource<List<TutorListing>> {
        return tutorListing.getAllTutorListing()
    }

}