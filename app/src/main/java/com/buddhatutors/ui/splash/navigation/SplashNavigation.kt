package com.buddhatutors.ui.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.buddhatutors.ui.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
object SplashRoute


fun NavGraphBuilder.registerSplash(
    openLoginPage: () -> Unit,
    openStudentHomePage: () -> Unit,
    openTutorHomePage: () -> Unit,
    openAdminHomePage: () -> Unit,
    openMasterTutorHomePage: () -> Unit,
) {
    composable<SplashRoute> {
        SplashScreen(
            openLoginPage = openLoginPage,
            openStudentHomePage = openStudentHomePage,
            openTutorHomePage = openTutorHomePage,
            openAdminHomePage = openAdminHomePage,
            openMasterTutorHomePage = openMasterTutorHomePage
        )
    }
}