package com.buddhatutors.domain.usecase.tutor

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.tutorlisting.TutorListing
import javax.inject.Inject

class UpdateTutorListing @Inject constructor(
    private val tutorRepository: TutorListingDataSource
) {

    suspend operator fun invoke(tutorListing: TutorListing): com.buddhatutors.model.Resource<TutorListing> {
        return tutorRepository.updateTutorListing(tutorListing)
    }

}