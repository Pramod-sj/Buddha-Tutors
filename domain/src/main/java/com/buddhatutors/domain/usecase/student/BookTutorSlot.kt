package com.buddhatutors.domain.usecase.student

import android.util.Log
import com.buddhatutors.domain.KEY_CALENDAR_SCOPE_ACCESS_TOKEN
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.datasource.MeetingDataSource
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.model.Resource
import com.buddhatutors.domain.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.domain.model.user.Student
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
import javax.inject.Inject

class BookTutorSlot @Inject constructor(
    private val tutorListingDataSource: TutorListingDataSource,
    private val meetingDataSource: MeetingDataSource,
    private val userSessionDataSource: UserSessionDataSource
) {

    suspend operator fun invoke(
        loggedInUser: User,
        tutor: Tutor,
        bookedSlot: BookedSlot
    ): Resource<Unit> {

        if (loggedInUser !is Student) {
            return Resource.Error(Throwable("Operation is not allowed by ${loggedInUser.userType}"))
        }

        val accessToken =
            userSessionDataSource.getUserToken(KEY_CALENDAR_SCOPE_ACCESS_TOKEN)
                ?: return Resource.Error(Throwable("accessToken cannot be null"))

        return when (val meetingResource = meetingDataSource.scheduleMeet(
            accessToken = accessToken,
            student = loggedInUser,
            tutor = tutor,
            bookedSlot = bookedSlot
        )) {
            is Resource.Error -> {
                Resource.Error(meetingResource.throwable)
            }

            is Resource.Success -> {
                val meetInfo = meetingResource.data
                Log.i("MEET INFO", meetInfo.toString())
                val resource = tutorListingDataSource.bookTutorSlot(
                    tutorId = tutor.id,
                    bookedSlot = bookedSlot.copy(meetInfo = meetInfo)
                )
                when (resource) {
                    is Resource.Error -> meetingDataSource.cancelMeet(
                        accessToken = accessToken,
                        eventId = meetInfo.eventId
                    )

                    is Resource.Success -> resource
                }
            }
        }

    }

}