package com.buddhatutors.feature.student.domain

import android.util.Log
import com.buddhatutors.core.auth.domain.UserSessionPreference
import com.buddhatutors.core.auth.domain.UserSessionPreference.Companion.KEY_CALENDAR_SCOPE_ACCESS_TOKEN
import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.domain.datasource.MeetingDataSource
import com.buddhatutors.model.Resource
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.model.user.User
import com.buddhatutors.model.user.UserType
import javax.inject.Inject

class BookTutorSlot @Inject constructor(
    private val bookedSlotDataSource: BookedSlotDataSource,
    private val meetingDataSource: MeetingDataSource,
    private val userSessionDataSource: UserSessionPreference
) {

    suspend operator fun invoke(
        loggedInUser: User,
        tutor: TutorListing,
        bookedSlot: BookedSlot
    ): Resource<BookedSlot> {

        if (loggedInUser.userType != UserType.STUDENT) {
            return Resource.Error(Throwable("Operation is not allowed by ${loggedInUser.userType}"))
        }

        val accessToken =
            userSessionDataSource.getUserToken(KEY_CALENDAR_SCOPE_ACCESS_TOKEN)
                ?: return Resource.Error(Throwable("accessToken cannot be null"))

        return when (val meetingResource = meetingDataSource.scheduleMeet(
            accessToken = accessToken,
            student = loggedInUser,
            tutor = tutor.tutorUser,
            bookedSlot = bookedSlot
        )) {
            is Resource.Error -> {
                Resource.Error(meetingResource.throwable)
            }

            is Resource.Success -> {
                val meetInfo = meetingResource.data
                Log.i("MEET INFO", meetInfo.toString())
                val resource = bookedSlotDataSource.bookTutorSlot(
                    bookedSlot = bookedSlot.copy(meetInfo = meetInfo)
                )
                when (resource) {
                    is Resource.Error -> {
                        meetingDataSource.cancelMeet(
                            accessToken = accessToken,
                            eventId = meetInfo.eventId
                        )
                        resource
                    }

                    is Resource.Success -> resource
                }
            }
        }

    }

}