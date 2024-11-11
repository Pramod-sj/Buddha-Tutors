package com.buddhatutors.domain.usecase.student

import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import javax.inject.Inject

class BookTutorSlot @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource
) {

    suspend operator fun invoke(tutorId: String, bookedSlot: BookedSlot): Resource<Unit> {
        return tutorListingDataSource.bookTutorSlot(tutorId = tutorId, bookedSlot = bookedSlot)
    }

}