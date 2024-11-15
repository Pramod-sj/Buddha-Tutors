package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.User

interface TutorListingDataSource {

    suspend fun getTutorListingById(tutorId: String): Resource<TutorListing>

    suspend fun getTutorListing(): Resource<List<TutorListing>>

    suspend fun getVerifiedTutorListing(): Resource<List<TutorListing>>

    suspend fun getUnVerifiedTutorListing(): Resource<List<TutorListing>>

    suspend fun addTutorListing(
        tutor: TutorListing
    ): Resource<TutorListing>

    suspend fun updateTutorVerifiedStatus(
        tutor: TutorListing,
        verifiedByUser: User,
        isApproved: Boolean
    ): Resource<TutorListing>

    suspend fun bookTutorSlot(
        tutorId: String,
        bookedSlot: BookedSlot,
    ): Resource<Unit>
}