package com.buddhatutors.feature.admin.domain.usecase

import androidx.paging.PagingData
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaginatedAllTutorUsers @Inject constructor(
    private val tutorListing: TutorListingDataSource
) {

    operator fun invoke(): Flow<PagingData<TutorListing>> {
        return tutorListing.getPaginatedAllTutorListing()
    }

}