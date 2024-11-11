package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User

interface TutorListingDataSource {

    suspend fun getTutorListingById(tutorId: String): Resource<TutorListing>

    suspend fun getTutorListing(): Resource<List<TutorListing>>

    suspend fun getVerifiedTutorListing(): Resource<List<TutorListing>>

    suspend fun updateTutorVerifiedStatus(
        tutor: Tutor,
        verifiedByUser: User,
        isApproved: Boolean
    ): Resource<TutorListing>

    suspend fun bookTutorSlot(
        tutorId: String,
        bookedSlot: BookedSlot,
    ): Resource<Unit>
}