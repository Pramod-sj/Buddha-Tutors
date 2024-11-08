@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.appadmin.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.AuthGraph
import kotlinx.coroutines.launch

@Preview
@Composable
internal fun PreviewAdminHomePage() {
    AdminHomeScreen()
}

@Composable
fun AdminHomeScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<AdminHomeViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    AdminHomeContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {
                AdminHomeUiEffect.LoggedOutSuccess -> {
                    TODO()
                }

                is AdminHomeUiEffect.NavigateToTutorVerificationScreen -> {
                    navigator.navigate(AdminGraph.AdminTutorVerification(uiEffect.tutor))
                }
            }
        }
    }

}


@Composable
internal fun AdminHomeContent(
    uiState: AdminHomeUiState,
    uiEvent: (AdminHomeUiEvent) -> Unit
) {

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = "Home")
                }
            )
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.tutorUsers) { tutor ->
                TutorItemCard(
                    tutor = tutor,
                    onClick = { uiEvent(AdminHomeUiEvent.TutorCardClick(tutor)) }
                )
            }
        }

    }


}