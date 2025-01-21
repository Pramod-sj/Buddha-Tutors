package com.buddhatutors.feature.admin.domain.usecase

import androidx.paging.PagingData
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaginatedAllTutorUsersByMasterId @Inject constructor(
    private val tutorListing: TutorListingDataSource
) {

    operator fun invoke(masterId: String): Flow<PagingData<TutorListing>> {
        return tutorListing.getPaginatedAllTutorListingByMasterTutorId(masterId)
    }

}