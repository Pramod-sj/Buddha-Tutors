package com.buddhatutors.feature.termconditions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.buddhatutors.common.navComposable
import com.buddhatutors.feature.termconditions.TermConditionScreen
import kotlinx.serialization.Serializable

@Serializable
object TermAndConditions

fun NavController.navigateToTermConditionScreen(navOptions: NavOptions? = null) =
    navigate(route = TermAndConditions, navOptions)

fun NavGraphBuilder.registerTermConditionScreen() {
    navComposable<TermAndConditions> { TermConditionScreen() }
}