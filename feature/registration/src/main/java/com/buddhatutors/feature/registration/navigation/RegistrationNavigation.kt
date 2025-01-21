package com.buddhatutors.feature.registration.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.registration.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
object RegisterUser

fun NavController.navigateToUserRegistration(navOptions: NavOptions? = null) =
    navigate(route = RegisterUser, navOptions)

fun NavGraphBuilder.registerUserRegistrationScreen(navigateToTermCondition: () -> Unit) {
    navComposable<RegisterUser> { RegisterScreen(navigateToTermCondition) }
}