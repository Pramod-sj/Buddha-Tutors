@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.student.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Preview
@Composable
internal fun PreviewHomePage() {
    HomePage()
}


@Composable
internal fun HomePage() {

    val navigator = com.buddhatutors.common.Navigator

    val viewModel = hiltViewModel<HomeViewModel>()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "BuddhaTutors",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    com.buddhatutors.common.ActionIconButton(
                        imageVector = Icons.Default.ExitToApp,
                        iconTint = Color.Black
                    ) {
                        viewModel.setEvent(HomeViewModelUiEvent.Logout)
                    }
                })
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            HomePageContent()
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect {
            when (it) {
                HomeViewModelUiEffect.LoggedOutSuccess -> {
                    navigator.popBackStack()
                    navigator.navigate("/login")
                }
            }
        }
    }
}

@Composable
internal fun HomePageContent() {
    Text(text = "Home page")
}