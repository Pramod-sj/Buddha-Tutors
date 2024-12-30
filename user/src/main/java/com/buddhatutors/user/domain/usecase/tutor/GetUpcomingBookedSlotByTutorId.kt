package com.buddhatutors.user.domain.usecase.tutor

import com.buddhatutors.common.domain.datasource.BookedSlotDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot
import javax.inject.Inject

internal class GetUpcomingBookedSlotByTutorId @Inject constructor(
    private val bookedSlotDataSource: BookedSlotDataSource
) {

    suspend operator fun invoke(tutorId: String): Resource<List<BookedSlot>> {
        return bookedSlotDataSource.getAllUpcomingBookedSlotsByTutorId(tutorId)
    }

}