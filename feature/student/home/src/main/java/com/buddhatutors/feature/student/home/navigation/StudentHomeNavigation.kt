package com.buddhatutors.feature.student.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.student.home.StudentMainPage
import com.buddhatutors.model.tutorlisting.TutorListing
import kotlinx.serialization.Serializable


@Serializable
object StudentMainPageRoute

fun NavController.navigateToStudentMainPage(navOptions: NavOptions? = null) =
    navigate(route = StudentMainPageRoute, navOptions)


fun NavGraphBuilder.registerStudentMainPage(
    openTutorDetailScreen: (tutorListing: TutorListing) -> Unit,
    openUserProfileScreen: () -> Unit
) {
    navComposable<StudentMainPageRoute> {
        StudentMainPage(
            openTutorDetailScreen,
            openUserProfileScreen
        )
    }
}