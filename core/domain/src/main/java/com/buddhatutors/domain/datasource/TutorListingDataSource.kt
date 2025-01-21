package com.buddhatutors.domain.datasource

import androidx.paging.PagingData
import com.buddhatutors.model.FilterOption
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.user.User
import kotlinx.coroutines.flow.Flow

interface TutorListingDataSource {

    suspend fun getTutorListingById(tutorId: String): Resource<TutorListing>

    suspend fun getTutorListing(): Resource<List<TutorListing>>

    suspend fun getVerifiedTutorListing(): Resource<List<TutorListing>>

    fun getPaginatedVerifiedTutorListing(filterOption: FilterOption?): Flow<PagingData<TutorListing>>

    suspend fun getUnVerifiedTutorListing(): Resource<List<TutorListing>>

    suspend fun addTutorListing(
        tutor: TutorListing
    ): Resource<TutorListing>

    suspend fun updateTutorListing(tutor: TutorListing): Resource<TutorListing>

    suspend fun updateTutorVerifiedStatus(
        tutor: TutorListing,
        verifiedByUser: User,
        isApproved: Boolean
    ): Resource<TutorListing>

    suspend fun getAllTutorListing(): Resource<List<TutorListing>>

    fun getPaginatedAllTutorListing(): Flow<PagingData<TutorListing>>

    fun getPaginatedAllTutorListingByMasterTutorId(masterTutorId: String): Flow<PagingData<TutorListing>>
}