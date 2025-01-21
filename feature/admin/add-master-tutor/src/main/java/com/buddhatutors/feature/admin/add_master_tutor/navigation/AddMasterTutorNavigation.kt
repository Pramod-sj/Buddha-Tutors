package com.buddhatutors.feature.admin.add_master_tutor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.admin.add_master_tutor.AddMasterTutorUserScreen
import kotlinx.serialization.Serializable


@Serializable
object AddMasterTutorRoute

fun NavController.navigateToAddMasterTutor(navOptions: NavOptions? = null) =
    navigate(route = AddMasterTutorRoute, navOptions)

fun NavGraphBuilder.registerAddMasterTutor() {
    navComposable<AddMasterTutorRoute> { AddMasterTutorUserScreen() }
}