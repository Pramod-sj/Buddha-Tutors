package com.buddhatutors.user.domain.repository

import com.buddhatutors.common.domain.datasource.TutorListingDataSource
import javax.inject.Inject

internal class StudentTutorRepository @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
)