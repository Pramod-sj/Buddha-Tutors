package com.buddhatutors.framework.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.buddhatutors.framework.SessionManager
import com.buddhatutors.framework.ui.common.Navigator
import kotlinx.coroutines.delay

@Preview
@Composable
internal fun PreviewSplashPage() {
    SplashPage()
}


@Composable
internal fun SplashPage() {

    val navigator = Navigator

    val viewModel = hiltViewModel<SplashViewModel>()

    LaunchedEffect(Unit) {

        viewModel.effect.collect {
            when (it) {
                SplashViewModelUiEffect.NavigateToLogin -> {
                    navigator.navigate(
                        "/login",
                        navOptions { popUpTo("/splash") { inclusive = true } })
                }

                SplashViewModelUiEffect.NavigateToStudentHome -> {
                    navigator.navigate(
                        "/home",
                        navOptions { popUpTo("/splash") { inclusive = true } })
                }

                SplashViewModelUiEffect.NavigateToTutorHome -> {
                    navigator.navigate(
                        "/home",
                        navOptions { popUpTo("/splash") { inclusive = true } })
                }
            }
        }

    }

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

    LaunchedEffect(Unit) { viewModel.setEvent(SplashViewModelUiEvent.InitializeApp) }

}