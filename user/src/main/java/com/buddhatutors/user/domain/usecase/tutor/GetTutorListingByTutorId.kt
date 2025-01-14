package com.buddhatutors.user.domain.usecase.tutor

import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.user.domain.repository.TutorRepository
import javax.inject.Inject

internal class GetTutorListingByTutorId @Inject constructor(
    private val tutorRepository: TutorRepository
) {

    suspend operator fun invoke(tutorId: String): Resource<TutorListing> {
        return tutorRepository.getTutorListing(tutorId)
    }

}