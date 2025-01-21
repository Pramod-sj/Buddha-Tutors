package com.buddhatutors.feature.tutor.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.tutor.home.TutorHomeScreen
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import kotlinx.serialization.Serializable


@Serializable
object TutorMainPageRoute

fun NavController.navigateToTutorMainPage(navOptions: NavOptions? = null) =
    navigate(route = TutorMainPageRoute, navOptions)

fun NavGraphBuilder.registerTutorMainPage(
    openBookedSlotDetail: (bookedSlot: BookedSlot) -> Unit,
    openUserProfileScreen: () -> Unit
) {
    navComposable<TutorMainPageRoute> {
        TutorHomeScreen(
            openBookedSlotDetailPage = openBookedSlotDetail,
            openUserProfilePage = openUserProfileScreen
        )
    }
}