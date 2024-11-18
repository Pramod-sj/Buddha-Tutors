package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.BookedSlotEMapper
import com.buddhatutors.data.model.tutorlisting.BookedSlotE
import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BookedSlotDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val bookedSlotEMapper: BookedSlotEMapper
) : BookedSlotDataSource {

    private val bookSlotsDocumentReference = firestore.collection("booked_slots")

    override suspend fun bookTutorSlot(bookedSlot: BookedSlot): Resource<BookedSlot> {
        if (isSlotAlreadyBooked(bookedSlot = bookedSlot)) {
            return Resource.Error(Throwable("Slot already booked!"))
        }
        return suspendCoroutine { continuation ->
            val newBookedSlot = bookedSlot.copy(id = bookSlotsDocumentReference.document().id)
            bookSlotsDocumentReference
                .document(newBookedSlot.id)
                .set(newBookedSlot)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Resource.Success(newBookedSlot))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Throwable("Something went wrong!"))
                        )
                    }
                }
        }
    }

    override suspend fun getAllBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("tutorId", tutorId)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotE::class.java).mapNotNull {
                                bookedSlotEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(bookedSlotList))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun getAllBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("bookedByStudentId", studentId)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotE::class.java).mapNotNull {
                                bookedSlotEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(bookedSlotList))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun getAllBookedSlots(): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotE::class.java).mapNotNull {
                                bookedSlotEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(bookedSlotList))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Exception(""))
                        )
                    }
                }
        }
    }


    private suspend fun isSlotAlreadyBooked(bookedSlot: BookedSlot): Boolean {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("tutorId", bookedSlot.tutorId)
                .whereEqualTo("bookedByStudentId", bookedSlot.bookedByStudentId)
                .whereEqualTo("date", bookedSlot.date)
                .whereEqualTo("startTime", bookedSlot.startTime)
                .whereEqualTo("endTime", bookedSlot.endTime)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {

                        val bookedSlotFromFirebase =
                            result.result?.toObjects(BookedSlotE::class.java)

                        continuation.resume(!bookedSlotFromFirebase.isNullOrEmpty())

                    } else {

                        continuation.resume(false)

                    }
                }
        }
    }
}