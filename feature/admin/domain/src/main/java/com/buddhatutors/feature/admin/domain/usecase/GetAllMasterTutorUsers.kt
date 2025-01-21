package com.buddhatutors.feature.admin.domain.usecase

import androidx.paging.PagingData
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.model.user.User
import com.buddhatutors.model.user.UserType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMasterTutorUsers @Inject constructor(
    private val userDataSource: UserDataSource
) {

    operator fun invoke(): Flow<PagingData<User>> {
        return userDataSource.getUserByTypePaginated(UserType.MASTER_TUTOR)
    }

}