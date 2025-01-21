package com.buddhatutors.feature.userprofile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.userprofile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
object UserProfile

fun NavController.navigateToUserProfileScreen(navOptions: NavOptions? = null) =
    navigate(route = UserProfile, navOptions)

fun NavGraphBuilder.registerUserProfileScreen(
    openLoginPage: () -> Unit,
    openEditTutorPage: (tutorId: String) -> Unit
) {
    navComposable<UserProfile> {
        ProfileScreen(
            openLoginPage = openLoginPage,
            openEditTutorPage = openEditTutorPage
        )
    }
}