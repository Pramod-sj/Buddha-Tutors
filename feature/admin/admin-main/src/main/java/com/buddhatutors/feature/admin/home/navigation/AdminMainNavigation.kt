package com.buddhatutors.feature.admin.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.admin.home.AdminMainScreen
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.serialization.Serializable

@Serializable
object AdminMainScreenRoute

fun NavController.navigateToAdminMainPage(navOptions: NavOptions? = null) =
    navigate(route = AdminMainScreenRoute, navOptions)

fun NavGraphBuilder.registerAdminMainScreen(
    openAddTutorPage: () -> Unit,
    openTutorVerificationPage: (tutorListing: TutorListing) -> Unit,
    openUserProfilePage: () -> Unit,
    openAddMasterTutorPage: () -> Unit,
    openAddTopicPage: () -> Unit
) {
    navComposable<AdminMainScreenRoute> {
        AdminMainScreen(
            openAddTutorPage = openAddTutorPage,
            openTutorVerificationPage = openTutorVerificationPage,
            openUserProfilePage = openUserProfilePage,
            openAddMasterTutorPage = openAddMasterTutorPage,
            openAddTopicPage = openAddTopicPage
        )
    }
}
