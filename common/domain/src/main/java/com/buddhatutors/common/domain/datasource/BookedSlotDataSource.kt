package com.buddhatutors.common.domain.datasource

import com.buddhatutors.common.domain.model.Resource
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot

interface BookedSlotDataSource {

    suspend fun bookTutorSlot(bookedSlot: BookedSlot): Resource<BookedSlot>

    suspend fun getAllBookedSlots(): Resource<List<BookedSlot>>

    suspend fun getAllUpcomingBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>>

    suspend fun getAllPastBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>>

    suspend fun getAllUpcomingBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>>

    suspend fun getAllPastBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>>

}