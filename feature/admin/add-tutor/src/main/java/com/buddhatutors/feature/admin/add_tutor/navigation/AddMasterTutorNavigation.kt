package com.buddhatutors.feature.admin.add_tutor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.admin.add_tutor.AddTutorUserScreen
import kotlinx.serialization.Serializable


@Serializable
object AddTutorRoute

fun NavController.navigateToAddTutor(navOptions: NavOptions? = null) =
    navigate(route = AddTutorRoute, navOptions)

fun NavGraphBuilder.registerAddTutor() {
    navComposable<AddTutorRoute> { AddTutorUserScreen() }
}