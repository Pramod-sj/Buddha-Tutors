package com.buddhatutors.common.navigation

import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.domain.model.tutorlisting.slotbooking.BookedSlot
import kotlinx.serialization.Serializable

@Serializable
object Splash


@Serializable
object AuthGraph {

    @Serializable
    object LoginUser

    @Serializable
    object RegisterUser

    @Serializable
    object ForgotPassword

    @Serializable
    object TermAndConditions

}


@Serializable
object AdminGraph {

    @Serializable
    object Home

    @Serializable
    object AddTutor

    @Serializable
    object AddMasterTutorUser

    @Serializable
    data class AdminTutorVerification(val tutor: TutorListing)

    @Serializable
    object ManageTopic

    @Serializable
    object AddTopic

}


@Serializable
object MasterTutorGraph {

    @Serializable
    object MasterTutorHome

    @Serializable
    data class AdminTutorVerification(val tutor: TutorListing)

    @Serializable
    object AddMasterTutorUser
}


@Serializable
object StudentGraph {

    @Serializable
    object Main

    @Serializable
    object TutorFilter

    @Serializable
    data class TutorDetail(val tutorListing: TutorListing)

}


@Serializable
object TutorGraph {

    @Serializable
    object Home

    @Serializable
    data class EditTutorAvailability(val tutorId: String)

    @Serializable
    data class BookedSlotDetail(val bookedSlot: BookedSlot)


}


@Serializable
object ProfileGraph {

    @Serializable
    object Home

}