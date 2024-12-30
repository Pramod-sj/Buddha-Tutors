package com.buddhatutors.user.domain.usecase.student

import androidx.paging.PagingData
import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.model.FilterOption
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetAllVerifiedTutorListing @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    operator fun invoke(filterOption: FilterOption? = null): Flow<PagingData<TutorListing>> {
        return tutorListingDataSource.getPaginatedVerifiedTutorListing(filterOption)
    }

}