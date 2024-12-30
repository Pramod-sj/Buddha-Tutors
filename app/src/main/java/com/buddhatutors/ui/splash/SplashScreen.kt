package com.buddhatutors.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.AuthGraph
import com.buddhatutors.common.navigation.MasterTutorGraph
import com.buddhatutors.common.navigation.Splash
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.TutorGraph

@Preview
@Composable
internal fun PreviewSplashPage() {
    SplashScreen()
}

@Composable
fun SplashScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<SplashViewModel>()

    SplashScreenContent(
        uiEvent = viewModel::setEvent
    )

    LaunchedEffect(Unit) {

        viewModel.effect.collect {
            when (it) {
                SplashViewModelUiEffect.NavigateToAuthFlow -> {
                    navigator.navigate(
                        route = AuthGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } }
                    )
                }

                SplashViewModelUiEffect.NavigateToStudentFlow -> {
                    navigator.navigate(
                        route = StudentGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } })
                }

                SplashViewModelUiEffect.NavigateToTutorHomeFlow -> {
                    navigator.navigate(
                        route = TutorGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } })
                }

                SplashViewModelUiEffect.NavigateToAdminFlow -> {
                    navigator.navigate(
                        route = AdminGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } }
                    )
                }

                SplashViewModelUiEffect.NavigateToMasterTutorFlow -> {
                    navigator.navigate(
                        route = MasterTutorGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } }
                    )
                }
            }
        }

    }

}


@Composable
fun SplashScreenContent(uiEvent: (SplashViewModelUiEvent) -> Unit) {

    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Center,
        ) {
            Text(text = "Buddha Tutors", style = MaterialTheme.typography.headlineMedium)
        }
    }

    LaunchedEffect(Unit) { uiEvent(SplashViewModelUiEvent.InitializeApp) }

}