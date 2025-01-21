package com.buddhatutors.feature.tutor.edit_tutor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.core.navigation.navigationCustomArgument
import com.buddhatutors.feature.tutor.edit_tutor.EditTutorScreen
import kotlinx.serialization.Serializable


@Serializable
data class EditTutorAvailabilityRoute(val tutorId: String)

fun NavController.navigateToEditTutorAvailabilityPage(
    tutorId: String,
    navOptions: NavOptions? = null
) = navigate(route = EditTutorAvailabilityRoute(tutorId), navOptions)

fun NavGraphBuilder.registerEditTutorAvailabilityPage() {
    navComposable<EditTutorAvailabilityRoute>(
        typeMap = mapOf(navigationCustomArgument<String>())
    ) { EditTutorScreen() }
}