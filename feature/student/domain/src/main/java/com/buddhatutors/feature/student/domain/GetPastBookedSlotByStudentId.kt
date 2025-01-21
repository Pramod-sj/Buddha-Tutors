package com.buddhatutors.feature.student.domain

import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import javax.inject.Inject

class GetPastBookedSlotByStudentId @Inject constructor(
    private val bookedSlotDataSource: BookedSlotDataSource
) {

    suspend operator fun invoke(studentId: String): Resource<List<BookedSlot>> {
        return bookedSlotDataSource.getAllPastBookedSlotsByStudentId(studentId)
    }

}