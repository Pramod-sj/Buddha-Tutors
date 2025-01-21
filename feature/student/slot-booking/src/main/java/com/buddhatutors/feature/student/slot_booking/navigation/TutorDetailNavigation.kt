package com.buddhatutors.feature.student.slot_booking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.core.navigation.navigationCustomArgument
import com.buddhatutors.feature.student.slot_booking.TutorDetailScreen
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.serialization.Serializable


@Serializable
data class TutorDetailRoute(val tutorListing: TutorListing)

fun NavController.navigateToStudentTutorDetailPage(
    tutorListing: TutorListing,
    navOptions: NavOptions? = null
) =
    navigate(route = TutorDetailRoute(tutorListing), navOptions)

fun NavGraphBuilder.registerStudentTutorDetailPage() {
    navComposable<TutorDetailRoute>(
        typeMap = mapOf(navigationCustomArgument<TutorListing>())
    ) { TutorDetailScreen() }
}