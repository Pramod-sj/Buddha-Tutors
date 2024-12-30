package com.buddhatutors.appadmin.domain.usecase.admin

import androidx.paging.PagingData
import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import com.buddhatutors.common.domain.datasource.UserDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMasterTutorUsers @Inject constructor(
    private val userDataSource: UserDataSource
) {

    operator fun invoke(): Flow<PagingData<User>> {
        return userDataSource.getUserByTypePaginated(UserType.MASTER_TUTOR)
    }

}