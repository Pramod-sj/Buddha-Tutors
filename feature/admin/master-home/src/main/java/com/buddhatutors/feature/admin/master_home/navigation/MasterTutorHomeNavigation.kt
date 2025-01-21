package com.buddhatutors.feature.admin.master_home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.admin.master_home.MasterTutorHomeScreen
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.serialization.Serializable


@Serializable
object MasterTutorHomeRoute

fun NavController.navigateToMasterTutorHomePage(navOptions: NavOptions? = null) =
    navigate(route = MasterTutorHomeRoute, navOptions)

fun NavGraphBuilder.registerMasterTutorHomeScreen(
    navigateToTutorVerificationScreen: (tutor: TutorListing) -> Unit,
    navigateToUserProfileScreen: () -> Unit,
    navigateToAddTutorScreen: () -> Unit,
) {
    navComposable<MasterTutorHomeRoute> {
        MasterTutorHomeScreen(
            navigateToTutorVerificationScreen = navigateToTutorVerificationScreen,
            navigateToUserProfileScreen = navigateToUserProfileScreen,
            navigateToAddTutorScreen = navigateToAddTutorScreen
        )
    }
}