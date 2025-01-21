package com.buddhatutors.core.data.firebase.firestore

import com.buddhatutors.core.data.firebase.firestore.constant.FirebaseCollectionConstant
import com.buddhatutors.core.data.model.BookedSlotEMapper
import com.buddhatutors.core.data.model.tutorlisting.BookedSlotEntity
import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class FSBookedSlotDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val bookedSlotEMapper: BookedSlotEMapper
) : BookedSlotDataSource {

    private val bookSlotsDocumentReference =
        firestore.collection(FirebaseCollectionConstant.REF_BOOKED_SLOTS)

    override suspend fun bookTutorSlot(bookedSlot: BookedSlot): Resource<BookedSlot> {
        if (isSlotAlreadyBooked(bookedSlot = bookedSlot)) {
            return Resource.Error(Throwable("Slot already booked!"))
        }
        return suspendCoroutine { continuation ->
            val newBookedSlot = bookedSlotEMapper.toEntity(bookedSlot)
                .copy(id = bookSlotsDocumentReference.document().id)
            bookSlotsDocumentReference
                .document(newBookedSlot.id)
                .set(newBookedSlot)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        continuation.resume(
                            Resource.Success(bookedSlotEMapper.toDomain(newBookedSlot))
                        )
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Throwable("Something went wrong!"))
                        )
                    }
                }
        }
    }

    override suspend fun getAllUpcomingBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("tutorId", tutorId)
                .whereGreaterThan("bookedSlotEndsTimeInMillis", System.currentTimeMillis())
                .orderBy("bookedSlotStartsTimeInMillis", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotEntity::class.java).mapNotNull {
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

    override suspend fun getAllPastBookedSlotsByTutorId(tutorId: String): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("tutorId", tutorId)
                .whereLessThan("bookedSlotStartsTimeInMillis", System.currentTimeMillis())
                .orderBy("bookedSlotStartsTimeInMillis", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotEntity::class.java).mapNotNull {
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

    override suspend fun getAllUpcomingBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("bookedByStudentId", studentId)
                .whereGreaterThan("bookedSlotEndsTimeInMillis", System.currentTimeMillis())
                .orderBy("bookedSlotStartsTimeInMillis", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotEntity::class.java).mapNotNull {
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

    override suspend fun getAllPastBookedSlotsByStudentId(studentId: String): Resource<List<BookedSlot>> {
        return suspendCoroutine { continuation ->
            bookSlotsDocumentReference
                .whereEqualTo("bookedByStudentId", studentId)
                .whereLessThan("bookedSlotStartsTimeInMillis", System.currentTimeMillis())
                .orderBy("bookedSlotStartsTimeInMillis", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val bookedSlotList = if (value?.isEmpty == false) {
                            value.toObjects(BookedSlotEntity::class.java).mapNotNull {
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
                            value.toObjects(BookedSlotEntity::class.java).mapNotNull {
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
                            result.result?.toObjects(BookedSlotEntity::class.java)

                        continuation.resume(!bookedSlotFromFirebase.isNullOrEmpty())

                    } else {

                        continuation.resume(false)

                    }
                }
        }
    }
}