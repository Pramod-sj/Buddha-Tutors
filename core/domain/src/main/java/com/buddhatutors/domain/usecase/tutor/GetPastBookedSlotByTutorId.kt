package com.buddhatutors.domain.usecase.tutor

import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import javax.inject.Inject

 class GetPastBookedSlotByTutorId @Inject constructor(
    private val bookedSlotDataSource: BookedSlotDataSource
) {

    suspend operator fun invoke(tutorId: String): Resource<List<BookedSlot>> {
        return bookedSlotDataSource.getAllPastBookedSlotsByTutorId(tutorId)
    }

}