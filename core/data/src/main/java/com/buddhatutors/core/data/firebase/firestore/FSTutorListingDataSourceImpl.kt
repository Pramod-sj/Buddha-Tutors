package com.buddhatutors.core.data.firebase.firestore

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.buddhatutors.core.data.firebase.firestore.constant.Constant
import com.buddhatutors.core.data.firebase.firestore.constant.FirebaseCollectionConstant
import com.buddhatutors.core.data.firebase.firestore.paging_source.FireStoreAllTutorPagingSource
import com.buddhatutors.core.data.model.tutorlisting.TutorListingEMapper
import com.buddhatutors.core.data.model.tutorlisting.TutorListingEntity
import com.buddhatutors.core.data.model.tutorlisting.VerificationEMapper
import com.buddhatutors.core.data.model.tutorlisting.VerificationEntity
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


internal class FSTutorListingDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val tutorListingEMapper: TutorListingEMapper,
    private val verificationEMapper: VerificationEMapper,
) : TutorListingDataSource {

    private val tutorsDocumentReference =
        firestore.collection(FirebaseCollectionConstant.REF_TUTOR_LISTING)

    init {
        FirebaseFirestore.setLoggingEnabled(true)
    }

    override suspend fun getTutorListingById(tutorId: String): com.buddhatutors.model.Resource<com.buddhatutors.model.tutorlisting.TutorListing> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .document(tutorId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val tutorListing = if (value.exists()) {
                            it.result.toObject(TutorListingEntity::class.java)?.let {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else null
                        if (tutorListing != null) {
                            continuation.resume(
                                com.buddhatutors.model.Resource.Success(
                                    tutorListing
                                )
                            )
                        } else {
                            continuation.resume(
                                com.buddhatutors.model.Resource.Error(
                                    Exception("Something went wrong!")
                                )
                            )
                        }
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getTutorListing(): com.buddhatutors.model.Resource<List<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingEntity::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(
                            com.buddhatutors.model.Resource.Success(
                                tutorListing
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getVerifiedTutorListing(): com.buddhatutors.model.Resource<List<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .whereEqualTo("verification.approved", true)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingEntity::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(
                            com.buddhatutors.model.Resource.Success(
                                tutorListing
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                result.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override fun getPaginatedVerifiedTutorListing(filterOption: com.buddhatutors.model.FilterOption?): Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return Pager(PagingConfig(pageSize = Constant.PAGE_SIZE)) {

            var queryRef = tutorsDocumentReference.limit(Constant.PAGE_LIMIT)

            queryRef = queryRef.whereEqualTo("verification.approved", true)

            // Apply filter for 'languages' if the list is not empty

            (filterOption?.topics?.map { it.id }.orEmpty() +
                    filterOption?.languages.orEmpty()).takeIf { it.isNotEmpty() }
                ?.let { options ->
                    options.forEach { fieldName ->
                        queryRef = queryRef.whereEqualTo("filteringOptions.${fieldName}", true)
                    }
                }

            FireStoreAllTutorPagingSource(queryRef)
        }.flow.map { data ->
            data.map { tutorListingE ->
                tutorListingEMapper.toDomain(tutorListingE)
            }
        }
    }

    override suspend fun getUnVerifiedTutorListing(): com.buddhatutors.model.Resource<List<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .whereEqualTo("verification.approved", false)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingEntity::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(
                            com.buddhatutors.model.Resource.Success(
                                tutorListing
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                result.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun addTutorListing(tutor: com.buddhatutors.model.tutorlisting.TutorListing): com.buddhatutors.model.Resource<com.buddhatutors.model.tutorlisting.TutorListing> {

        return suspendCoroutine { continuation ->

            val tutorListing = tutorListingEMapper.toEntity(tutor)

            tutorsDocumentReference
                .document(tutor.tutorUser.id)
                .set(tutorListing)
                .addOnCompleteListener {
                    val newTutorListing = tutorListingEMapper.toDomain(tutorListing)
                    if (it.isSuccessful) {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Success(
                                newTutorListing
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Throwable("Something went wrong!")
                            )
                        )
                    }
                }
        }

    }

    override suspend fun updateTutorListing(tutor: com.buddhatutors.model.tutorlisting.TutorListing): com.buddhatutors.model.Resource<com.buddhatutors.model.tutorlisting.TutorListing> {
        return suspendCoroutine { continuation ->
            val tutorListingEntity = tutorListingEMapper.toEntity(tutor)
            val updatedMap = mapOf(
                "tutorUser" to tutorListingEntity.tutorUser
                    .copy(
                        name = tutorListingEntity.tutorUser.name,
                        email = tutorListingEntity.tutorUser.email
                    ),
                "languages" to tutorListingEntity.languages,
                "expertiseIn" to tutorListingEntity.expertiseIn,
                "availableDays" to tutorListingEntity.availableDays,
                "availableTimeSlots" to tutorListingEntity.availableTimeSlots,
                "filteringOptions" to tutorListingEntity.filteringOptions
            )
            tutorsDocumentReference
                .document(tutor.tutorUser.id)
                .update(updatedMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(com.buddhatutors.model.Resource.Success(tutor))
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Throwable("Something went wrong!")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun updateTutorVerifiedStatus(
        tutor: com.buddhatutors.model.tutorlisting.TutorListing,
        verifiedByUser: com.buddhatutors.model.user.User,
        isApproved: Boolean
    ): com.buddhatutors.model.Resource<com.buddhatutors.model.tutorlisting.TutorListing> {

        return suspendCoroutine { continuation ->

            val currentDateTime =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
                    .format(Calendar.getInstance().time)

            val verification = VerificationEntity(
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
                            com.buddhatutors.model.Resource.Success(
                                tutor.copy(
                                    verification = verificationEMapper.toDomain(
                                        verification
                                    )
                                )
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                it.exception ?: Throwable("Something went wrong!")
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getAllTutorListing(): com.buddhatutors.model.Resource<List<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return suspendCoroutine { continuation ->
            tutorsDocumentReference
                .limit(100)
                .orderBy(Constant.CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val value = result.result
                        val tutorListing = if (value?.isEmpty == false) {
                            value.toObjects(TutorListingEntity::class.java).mapNotNull {
                                tutorListingEMapper.toDomain(it)
                            }
                        } else emptyList()
                        continuation.resume(
                            com.buddhatutors.model.Resource.Success(
                                tutorListing
                            )
                        )
                    } else {
                        continuation.resume(
                            com.buddhatutors.model.Resource.Error(
                                result.exception ?: Exception("")
                            )
                        )
                    }
                }
        }
    }

    override fun getPaginatedAllTutorListing(): Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return Pager(PagingConfig(pageSize = Constant.PAGE_SIZE)) {
            FireStoreAllTutorPagingSource(
                tutorsDocumentReference
                    .orderBy(Constant.CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(Constant.PAGE_LIMIT)
            )
        }.flow.map { data ->
            data.map { tutorListingE ->
                tutorListingEMapper.toDomain(tutorListingE)
            }
        }
    }

    override fun getPaginatedAllTutorListingByMasterTutorId(masterTutorId: String): Flow<PagingData<com.buddhatutors.model.tutorlisting.TutorListing>> {
        return Pager(PagingConfig(pageSize = Constant.PAGE_SIZE)) {
            FireStoreAllTutorPagingSource(
                tutorsDocumentReference
                    .whereEqualTo("addedByUser.id", masterTutorId)
                    .orderBy(Constant.CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(Constant.PAGE_LIMIT)
            )
        }.flow.map { data ->
            data.map { tutorListingE ->
                tutorListingEMapper.toDomain(tutorListingE)
            }
        }
    }

}