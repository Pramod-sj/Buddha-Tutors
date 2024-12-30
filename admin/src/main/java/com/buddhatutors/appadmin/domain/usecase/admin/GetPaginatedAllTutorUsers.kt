package com.buddhatutors.appadmin.domain.usecase.admin

import androidx.paging.PagingData
import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaginatedAllTutorUsers @Inject constructor(
    private val tutorListing: TutorListingDataSource
) {

    operator fun invoke(): Flow<PagingData<TutorListing>> {
        return tutorListing.getPaginatedAllTutorListing()
    }

}