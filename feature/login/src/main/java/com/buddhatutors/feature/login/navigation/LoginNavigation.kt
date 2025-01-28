package com.buddhatutors.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.buddhatutors.feature.login.LoginScreen
import kotlinx.serialization.Serializable


@Serializable
object LoginRoute

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    navigate(route = LoginRoute, navOptions)

fun NavGraphBuilder.registerLoginScreen(loginNavigationHandler: ExternalLoginNavigationHandler) {
    composable<LoginRoute> { LoginScreen(loginNavigationHandler) }
}