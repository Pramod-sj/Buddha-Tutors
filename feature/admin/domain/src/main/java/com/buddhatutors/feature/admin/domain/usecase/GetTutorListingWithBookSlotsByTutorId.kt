package com.buddhatutors.feature.admin.domain.usecase

import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TutorListingWithBookSlots(
    val tutorListing: TutorListing,
    val bookedSlotList: List<BookedSlot>
)


class GetTutorListingWithBookedSlotsByTutorId @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource,
    private val bookedSlotDataSource: BookedSlotDataSource
) {

    suspend operator fun invoke(tutorId: String): Resource<TutorListingWithBookSlots> {
        return withContext(Dispatchers.IO) {

            val tutorListingResourceAsync =
                async { tutorListingDataSource.getTutorListingById(tutorId = tutorId) }

            val bookedSlotListResourceAsync =
                async { bookedSlotDataSource.getAllUpcomingBookedSlotsByTutorId(tutorId) }

            val tutorListingResource = tutorListingResourceAsync.await()

            val bookedSlotResource = bookedSlotListResourceAsync.await()

            when (tutorListingResource) {
                is Resource.Error -> tutorListingResource
                is Resource.Success -> {
                    when (bookedSlotResource) {
                        is Resource.Error -> bookedSlotResource
                        is Resource.Success -> {

                            Resource.Success(
                                TutorListingWithBookSlots(
                                    tutorListing = tutorListingResource.data,
                                    bookedSlotList = bookedSlotResource.data
                                )
                            )

                        }
                    }
                }
            }

        }
    }

}