package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.BookedSlotEMapper
import com.buddhatutors.data.model.UserEMapper
import com.buddhatutors.data.model.tutorlisting.TutorListingE
import com.buddhatutors.data.model.tutorlisting.TutorListingEMapper
import com.buddhatutors.data.model.tutorlisting.VerificationE
import com.buddhatutors.data.model.tutorlisting.VerificationEMapper
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class TutorListingDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val tutorListingEMapper: TutorListingEMapper,
    private val bookedSlotEMapper: BookedSlotEMapper,
    private val userEMapper: UserEMapper,
    private val verificationEMapper: VerificationEMapper
) : TutorListingDataSource {


    private val tutorsDocumentReference = firestore.collection("tutors_listing")

    override suspend fun getTutorListingById(tutorId: String): Resource<TutorListing> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .document(tutorId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val tutorListing = if (value.exists()) {
                            it.result.toObject(TutorListingE::class.java)?.let {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else null
                        if (tutorListing != null) {
                            continuation.resume(Resource.Success(tutorListing))
                        } else {
                            continuation.resume(Resource.Error(Exception("Something went wrong!")))
                        }
                    } else {
                        continuation.resume(
                            Resource.Error(it.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun getTutorListing(): Resource<List<TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingE::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(tutorListing))
                    } else {
                        continuation.resume(
                            Resource.Error(it.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun getVerifiedTutorListing(): Resource<List<TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .whereEqualTo("verification.approved", true)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingE::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(tutorListing))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun getUnVerifiedTutorListing(): Resource<List<TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .whereEqualTo("verification.approved", false)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingE::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(tutorListing))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    override suspend fun addTutorListing(tutor: TutorListing): Resource<TutorListing> {

        return suspendCoroutine { continuation ->

            val tutorListing = tutorListingEMapper.toEntity(tutor)

            tutorsDocumentReference
                .document(tutor.tutorUser.id)
                .set(tutorListing)
                .addOnCompleteListener {
                    val newTutorListing = tutorListingEMapper.toDomain(tutorListing)
                    if (it.isSuccessful) {
                        continuation.resume(Resource.Success(newTutorListing))
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Throwable("Something went wrong!")
                            )
                        )
                    }
                }
        }

    }

    override suspend fun updateTutorVerifiedStatus(
        tutor: TutorListing,
        verifiedByUser: User,
        isApproved: Boolean
    ): Resource<TutorListing> {

        return suspendCoroutine { continuation ->

            val currentDateTime =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
                    .format(Calendar.getInstance().time)

            val verification = VerificationE(
                approved = isApproved,
                verifiedByUserId = verifiedByUser.id,
                verifiedByUserName = verifiedByUser.name,
                verifiedDateTime = currentDateTime
            )

            tutorsDocumentReference
                .document(tutor.tutorUser.id)
                .update("verification", verification)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(
                            Resource.Success(
                                tutor.copy(
                                    verification = verificationEMapper.toDomain(
                                        verification
                                    )
                                )
                            )
                        )
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Throwable("Something went wrong!")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun bookTutorSlot(tutorId: String, bookedSlot: BookedSlot): Resource<Unit> {
        if (isSlotAlreadyBooked(tutorId = tutorId, bookedSlot = bookedSlot)) {
            return Resource.Error(Throwable("Slot already booked!"))
        }
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .document(tutorId)
                .update(
                    "bookedSlots",
                    FieldValue.arrayUnion(bookedSlotEMapper.toEntity(bookedSlot))
                )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Resource.Success(Unit))
                    } else {
                        continuation.resume(
                            Resource.Error(
                                it.exception ?: Throwable("Something went wrong!")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getAllTutorListing(): Resource<List<TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingE::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(Resource.Success(tutorListing))
                    } else {
                        continuation.resume(
                            Resource.Error(result.exception ?: Exception(""))
                        )
                    }
                }
        }
    }

    private suspend fun isSlotAlreadyBooked(
        tutorId: String, bookedSlot: BookedSlot
    ): Boolean {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .document(tutorId)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {

                        val tutor = result.result.toObject(TutorListingE::class.java)

                        val isSlotAlreadyBooked = tutor?.bookedSlots?.any {
                            it.date == bookedSlot.date &&
                                    it.startTime == bookedSlot.startTime &&
                                    it.endTime == bookedSlot.endTime
                        } ?: false

                        continuation.resume(isSlotAlreadyBooked)

                    } else {

                        continuation.resume(false)

                    }
                }
        }
    }
}