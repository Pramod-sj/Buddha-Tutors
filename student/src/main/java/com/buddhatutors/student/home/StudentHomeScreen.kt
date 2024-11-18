@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.student.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.navigation.ProfileGraph
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.domain.model.tutorlisting.TutorListing

@Preview
@Composable
internal fun PreviewHomePage() {
    StudentHomeScreenContent(uiState = StudentHomeUiState(tutorListing = listOf()), uiEvent = {})
}


@Composable
fun StudentHomeScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<StudentHomeViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    StudentHomeScreenContent(uiState, viewModel::setEvent)

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                StudentHomeUiEffect.LoggedOutSuccess -> {
                    navigator.popBackStack()
                    navigator.navigate("/login")
                }

                is StudentHomeUiEffect.NavigateToTutorListingScreen -> {
                    navigator.navigate(StudentGraph.TutorDetail(effect.tutorListing))
                }

                StudentHomeUiEffect.NavigateToProfileScreen -> {
                    navigator.navigate(ProfileGraph.Home)
                }
            }
        }
    }
}

@Composable
internal fun StudentHomeScreenContent(
    uiState: StudentHomeUiState,
    uiEvent: (StudentHomeUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                actions = {
                    ActionIconButton(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Person,
                        iconTint = Color.Black
                    ) {
                        uiEvent(StudentHomeUiEvent.ProfileIconClick)
                    }
                })
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {

                item {
                    Text(
                        text = "Find the teacher who will help you excel",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                item { Spacer(Modifier) }

                items(uiState.tutorListing) { tutorListing ->
                    TutorItemCard(
                        tutorListing = tutorListing,
                        onClick = {
                            uiEvent(StudentHomeUiEvent.TutorListingItemClick(tutorListing))
                        })
                }
            }
        }
    }
}

@Composable
fun TutorItemCard(
    tutorListing: TutorListing,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        onClick = onClick
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    )
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
            ) {

                Text(
                    text = tutorListing.tutorUser.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = tutorListing.expertiseIn.map { it.label }.joinToString(" ") { "• $it" },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
