package com.buddhatutors.domain.usecase

import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import javax.inject.Inject

class GetBookedSlotByTutorId @Inject constructor(
    private val bookedSlotDataSource: BookedSlotDataSource
) {

    suspend operator fun invoke(tutorId: String): Resource<List<BookedSlot>> {
        return bookedSlotDataSource.getAllBookedSlotsByTutorId(tutorId)
    }

}