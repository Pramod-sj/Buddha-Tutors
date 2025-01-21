package com.buddhatutors.feature.forgotpassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navDialogComposable
import com.buddhatutors.feature.forgotpassword.ForgotPasswordScreen
import kotlinx.serialization.Serializable


@Serializable
object ForgotPassword

fun NavController.navigateToForgotPasswordDialog(navOptions: NavOptions? = null) =
    navigate(route = ForgotPassword, navOptions)

fun NavGraphBuilder.registerForgotPasswordDialog() {
    navDialogComposable<ForgotPassword> { ForgotPasswordScreen() }
}