package com.buddhatutors.data.datasourceimpl

import com.buddhatutors.data.model.toEntity
import com.buddhatutors.data.model.tutorlisting.TutorListingE
import com.buddhatutors.data.model.tutorlisting.toDomain
import com.buddhatutors.data.model.tutorlisting.toEntity
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.TutorListing
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class TutorListingDataSourceImpl @Inject constructor(
    firestore: FirebaseFirestore,
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
                            it.result.toObject(TutorListingE::class.java)?.toDomain()
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
                            value.toObjects(TutorListingE::class.java).mapNotNull { it.toDomain() }
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

    override suspend fun updateTutorVerifiedStatus(
        tutor: Tutor,
        verifiedByUser: User,
        isApproved: Boolean
    ): Resource<TutorListing> {

        val existingTutorListingItem =
            when (val resource = getTutorListingById(tutor.id)) {
                is Resource.Error -> null
                is Resource.Success -> resource.data
            }

        return suspendCoroutine { continuation ->

            val currentDateTime =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
                    .format(Calendar.getInstance().time)

            val tutorListing = TutorListingE(
                tutor = tutor.toEntity(),
                verification = TutorListingE.VerificationE(
                    approved = isApproved,
                    verifiedByUserId = verifiedByUser.id,
                    verifiedByUserName = verifiedByUser.name,
                    verifiedDateTime = currentDateTime
                ),
                bookedSlots = existingTutorListingItem?.bookedSlots?.map { it.toEntity() }
                    .orEmpty()
            )

            tutorsDocumentReference
                .document(tutor.id)
                .set(tutorListing)
                .addOnCompleteListener {
                    val newTutorListing = tutorListing.toDomain()
                    if (it.isSuccessful && newTutorListing != null) {
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
}