package com.buddhatutors.common.profile_ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun PreviewProfilePage() {
    ProfileScreen()
}

@Composable
fun ProfileScreen() {

    //val navigator = Navigator

    //val viewModel = hiltViewModel<StudentHomeViewModel>()

    //val uiState by viewModel.uiState.collectAsState()

    //ProfileScreenContent(uiState, viewModel::setEvent)

    /*LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                StudentHomeUiEffect.LoggedOutSuccess -> {
                    navigator.popBackStack()
                    navigator.navigate("/login")
                }

                is StudentHomeUiEffect.NavigateToTutorListingScreen -> {
                    navigator.navigate(StudentGraph.TutorDetail(effect.tutorListing))
                }
            }
        }
    }*/
}

/*
@Composable
internal fun ProfileScreenContent(
    uiState: StudentHomeUiState,
    uiEvent: (StudentHomeUiEvent) -> Unit
) {
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
                    ActionIconButton(
                        imageVector = Icons.Default.ExitToApp,
                        iconTint = Color.Black
                    ) {
                        //viewModel.setEvent(HomeViewModelUiEvent.Logout)
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
}*/
