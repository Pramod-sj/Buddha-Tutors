package com.buddhatutors.framework.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

internal val LocalNavController =
    compositionLocalOf<NavHostController> { error("No NavController found!") }


@Composable
internal fun BuddhaTutorsProvider(
    navHost: NavHostController = rememberNavController(),
    component: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNavController provides navHost) {
        component()
    }
}


internal val Navigator: NavHostController
    @Composable
    @ReadOnlyComposable
    get() = LocalNavController.current

