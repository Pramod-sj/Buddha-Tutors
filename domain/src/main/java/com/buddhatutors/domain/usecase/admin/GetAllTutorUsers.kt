package com.buddhatutors.domain.usecase.admin

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import javax.inject.Inject

class GetAllTutorUsers @Inject constructor(
    private val tutorListing: TutorListingDataSource
) {

    suspend operator fun invoke(): Resource<List<TutorListing>> {
        return tutorListing.getAllTutorListing()
    }

}