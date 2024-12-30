package com.buddhatutors.appadmin.domain.usecase.admin

import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import javax.inject.Inject

class GetAllTutorUsers @Inject constructor(
    private val tutorListing: TutorListingDataSource
) {

    suspend operator fun invoke(): Resource<List<TutorListing>> {
        return tutorListing.getAllTutorListing()
    }

}