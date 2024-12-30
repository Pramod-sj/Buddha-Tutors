@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.appadmin.presentation.master_tutor.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.buddhatutors.appadmin.presentation.common.TutorItemCard
import com.buddhatutors.appadmin.presentation.common.TutorItemCardPlaceholder
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.NoDataScreenContent
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.MasterTutorGraph
import com.buddhatutors.common.navigation.ProfileGraph
import kotlinx.coroutines.flow.flowOf

@Preview
@Composable
internal fun PreviewAdminHomePage() {
    MasterTutorHomeContent(
        uiState = MasterTutorHomeUiState(),
        uiEvent = {},
        tutorListingLazyPagingItems = flowOf(PagingData.empty<TutorListing>()).collectAsLazyPagingItems()
    )
}

@Composable
fun MasterTutorHomeScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<MasterTutorHomeViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val tutorListingPagingItems = uiState.tutorsPagingData.collectAsLazyPagingItems()

    MasterTutorHomeContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        tutorListingLazyPagingItems = tutorListingPagingItems
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {
                MasterTutorHomeUiEffect.LoggedOutSuccess -> {
                    TODO()
                }

                is MasterTutorHomeUiEffect.NavigateToTutorVerificationScreen -> {
                    navigator.navigate(AdminGraph.AdminTutorVerification(uiEffect.tutor))
                }

                MasterTutorHomeUiEffect.NavigateToProfileScreen -> {
                    navigator.navigate(ProfileGraph.Home)
                }

                MasterTutorHomeUiEffect.NavigateToAddUserScreen -> {
                    navigator.navigate(MasterTutorGraph.AddMasterTutorUser)
                }
            }
        }
    }

}


@Composable
internal fun MasterTutorHomeContent(
    uiState: MasterTutorHomeUiState,
    uiEvent: (MasterTutorHomeUiEvent) -> Unit,
    tutorListingLazyPagingItems: LazyPagingItems<TutorListing>
) {

    val pullRefreshState = rememberPullToRefreshState()

    val isRefreshing by remember(tutorListingLazyPagingItems.loadState) {
        mutableStateOf(tutorListingLazyPagingItems.loadState.refresh is LoadState.Loading)
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = "Home")
                },
                actions = {
                    ActionIconButton(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Person,
                        iconTint = Color.Black
                    ) {
                        uiEvent(MasterTutorHomeUiEvent.ProfileIconClick)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { uiEvent(MasterTutorHomeUiEvent.AddUserFABClick) },
                text = {
                    Text(text = "Add tutor")
                },
                icon = {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add user")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            isRefreshing = isRefreshing,
            onRefresh = {
                tutorListingLazyPagingItems.refresh()
            },
            state = pullRefreshState,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = pullRefreshState
                )
            },
        ) {

            when {
                tutorListingLazyPagingItems.loadState.refresh is LoadState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(10) {
                            TutorItemCardPlaceholder()
                        }
                    }
                }

                tutorListingLazyPagingItems.loadState.refresh is LoadState.Error -> {
                    NoDataScreenContent(
                        title = "No Tutors",
                        message = "It seems like there are no tutors available at the moment. Please add some tutors!",
                        icon = Icons.Outlined.School,
                        onRetry = null
                    )
                }

                else -> {

                    if (tutorListingLazyPagingItems.itemCount == 0) {

                        NoDataScreenContent(
                            title = "No Tutors",
                            message = "It seems like there are no tutors available at the moment. Please add some tutors!",
                            icon = Icons.Outlined.School,
                            onRetry = null
                        )

                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            state = listState
                        ) {

                            item {
                                if (tutorListingLazyPagingItems.loadState.prepend is LoadState.Loading) {
                                    Box(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(24.dp)
                                        )
                                    }
                                }
                            }

                            items(tutorListingLazyPagingItems.itemCount) { index ->
                                tutorListingLazyPagingItems[index]?.let { tutor ->
                                    TutorItemCard(
                                        tutorListing = tutor,
                                        onClick = {
                                            uiEvent(
                                                MasterTutorHomeUiEvent.TutorCardClick(
                                                    tutor
                                                )
                                            )
                                        }
                                    )
                                }
                            }

                            item {
                                if (tutorListingLazyPagingItems.loadState.append is LoadState.Loading) {
                                    Box(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(24.dp)
                                        )
                                    }
                                }
                            }

                        }

                    }

                }
            }

        }
    }
}