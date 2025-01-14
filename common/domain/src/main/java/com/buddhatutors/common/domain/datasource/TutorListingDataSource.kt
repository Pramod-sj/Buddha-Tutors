package com.buddhatutors.common.domain.datasource

import androidx.paging.PagingData
import com.buddhatutors.common.domain.model.FilterOption
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.domain.model.user.User
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