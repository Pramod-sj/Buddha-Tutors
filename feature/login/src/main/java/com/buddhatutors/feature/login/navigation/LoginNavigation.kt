package com.buddhatutors.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.buddhatutors.feature.login.LoginScreen
import kotlinx.serialization.Serializable


@Serializable
object LoginUser

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    navigate(route = LoginUser, navOptions)

fun NavGraphBuilder.registerLoginScreen(loginNavigationHandler: ExternalLoginNavigationHandler) {
    composable<LoginUser> { LoginScreen(loginNavigationHandler) }
}