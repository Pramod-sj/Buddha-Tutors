package com.buddhatutors.domain.datasource

import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot

interface BookedSlotDataSource {

    suspend fun bookTutorSlot(bookedSlot: BookedSlot): Resource<BookedSlot>

    suspend fun getAllBookedSlots(): Resource<List<BookedSlot>>

    suspend fun getAllUpcomingBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>>

    suspend fun getAllPastBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>>

    suspend fun getAllUpcomingBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>>

    suspend fun getAllPastBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>>

}