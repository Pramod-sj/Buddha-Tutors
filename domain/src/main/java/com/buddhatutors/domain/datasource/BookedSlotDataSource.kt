package com.buddhatutors.domain.datasource

import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot

interface BookedSlotDataSource {

    suspend fun bookTutorSlot(bookedSlot: BookedSlot): Resource<BookedSlot>

    suspend fun getAllBookedSlots(): Resource<List<BookedSlot>>

    suspend fun getAllBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>>

    suspend fun getAllBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>>

}