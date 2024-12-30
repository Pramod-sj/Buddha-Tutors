package com.buddhatutors.user.domain.usecase.student

import com.buddhatutors.common.domain.datasource.BookedSlotDataSource
import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot
import javax.inject.Inject

internal class GetPastBookedSlotByStudentId @Inject constructor(
    private val bookedSlotDataSource: BookedSlotDataSource
) {

    suspend operator fun invoke(studentId: String): Resource<List<BookedSlot>> {
        return bookedSlotDataSource.getAllPastBookedSlotsByStudentId(studentId)
    }

}