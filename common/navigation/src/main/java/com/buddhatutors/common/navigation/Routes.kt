package com.buddhatutors.common.navigation

import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User
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
    object TermAndConditions

}


@Serializable
object AdminGraph {

    @Serializable
    object Home

    @Serializable
    data class AdminTutorVerification(val tutor: Tutor)

}


@Serializable
object MasterTutorGraph {

    @Serializable
    object Home

}


@Serializable
object StudentGraph {

    @Serializable
    object Home

    @Serializable
    data class TutorDetail(val tutorListing: TutorListing)

}


@Serializable
object TutorGraph {

    @Serializable
    object Home

}