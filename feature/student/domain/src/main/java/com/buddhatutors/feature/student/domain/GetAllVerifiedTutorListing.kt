package com.buddhatutors.feature.student.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllVerifiedTutorListing @Inject constructor(
    private val tutorListingDataSource: com.buddhatutors.domain.datasource.TutorListingDataSource
) {

    operator fun invoke(filterOption: com.buddhatutors.model.FilterOption? = null): Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return tutorListingDataSource.getPaginatedVerifiedTutorListing(filterOption)
    }

}