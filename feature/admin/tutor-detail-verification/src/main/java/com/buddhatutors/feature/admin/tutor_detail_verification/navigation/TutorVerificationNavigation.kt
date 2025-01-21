package com.buddhatutors.feature.admin.tutor_detail_verification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.core.navigation.navigationCustomArgument
import com.buddhatutors.feature.admin.tutor_detail_verification.TutorVerificationScreen
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.serialization.Serializable

@Serializable
data class AdminTutorVerificationRoute(val tutor: TutorListing)

fun NavController.navigateToAdminTutorVerificationScreen(
    tutorListing: TutorListing,
    navOptions: NavOptions? = null
) =
    navigate(route = AdminTutorVerificationRoute(tutorListing), navOptions)

fun NavGraphBuilder.registerAdminTutorVerification() {
    navComposable<AdminTutorVerificationRoute>(
        typeMap = mapOf(navigationCustomArgument<TutorListing>())
    ) { TutorVerificationScreen() }
}
