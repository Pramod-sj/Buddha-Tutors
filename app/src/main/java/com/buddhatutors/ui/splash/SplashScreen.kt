package com.buddhatutors.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import com.buddhatutors.BuildConfig
import com.buddhatutors.R
import com.buddhatutors.common.Navigator

@Preview
@Composable
internal fun PreviewSplashPage() {
    SplashScreen(
        openLoginPage = TODO(),
        openStudentHomePage = TODO(),
        openTutorHomePage = TODO(),
        openAdminHomePage = TODO(),
        openMasterTutorHomePage = TODO()
    )
}

@Composable
fun SplashScreen(
    openLoginPage: () -> Unit,
    openStudentHomePage: () -> Unit,
    openTutorHomePage: () -> Unit,
    openAdminHomePage: () -> Unit,
    openMasterTutorHomePage: () -> Unit,
) {

    val navigator = Navigator

    val viewModel = hiltViewModel<SplashViewModel>()

    SplashScreenContent(
        uiEvent = viewModel::setEvent
    )

    LaunchedEffect(Unit) {

        viewModel.effect.collect {
            when (it) {
                SplashViewModelUiEffect.NavigateToAuthFlow -> {
                    openLoginPage()
/*
                    navigator.navigate(
                        route = AuthGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } }
                    )
*/
                }

                SplashViewModelUiEffect.NavigateToStudentFlow -> {
                    openStudentHomePage()
/*
                    navigator.navigate(
                        route = StudentGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } })
*/
                }

                SplashViewModelUiEffect.NavigateToTutorHomeFlow -> {
                    openTutorHomePage()
/*
                    navigator.navigate(
                        route = TutorGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } })
*/
                }

                SplashViewModelUiEffect.NavigateToAdminFlow -> {
                    openAdminHomePage()
/*
                    navigator.navigate(
                        route = AdminGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } }
                    )
*/
                }

                SplashViewModelUiEffect.NavigateToMasterTutorFlow -> {
                    openMasterTutorHomePage()
/*
                    navigator.navigate(
                        route = MasterTutorGraph,
                        navOptions = navOptions { popUpTo(Splash) { inclusive = true } }
                    )
*/
                }
            }
        }

    }

}


@Composable
fun SplashScreenContent(uiEvent: (SplashViewModelUiEvent) -> Unit) {
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                contentAlignment = Center
            ) {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    fontSize = 12.sp
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Center,
        ) {
            Image(
                modifier = Modifier.size(288.dp),
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentScale = ContentScale.FillBounds,
                contentDescription = "Splash logo"
            )
        }
    }

    LaunchedEffect(Unit) { uiEvent(SplashViewModelUiEvent.InitializeApp) }

}