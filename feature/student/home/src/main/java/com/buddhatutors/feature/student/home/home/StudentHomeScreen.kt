@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.feature.student.home.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.NoDataScreenContent
import com.buddhatutors.common.shimmerBrush
import com.buddhatutors.feature.student.home.home.filter.TutorFilterScreen
import com.buddhatutors.model.tutorlisting.TutorListing

@Preview
@Composable
internal fun PreviewHomePage() {
    StudentHomeScreenContent(
        uiState = StudentHomeUiState(),
        uiEvent = {},
    )
}


@Composable
fun StudentHomeScreen(
    openTutorDetailScreen: (tutorListing: TutorListing) -> Unit,
    openUserProfileScreen: () -> Unit
) {

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

                is StudentHomeUiEffect.NavigateToTutorDetailScreen -> {
                    openTutorDetailScreen(effect.tutorListing)
                    //navigator.navigate(StudentGraph.TutorDetail(effect.tutorListing))
                }

                StudentHomeUiEffect.NavigateToProfileScreen -> {
                    openUserProfileScreen()
                    //navigator.navigate(ProfileGraph.Home)
                }
            }
        }
    }

    if (uiState.showFilterScreen) {

        Dialog(
            onDismissRequest = {
                viewModel.setEvent(StudentHomeUiEvent.HideFilterScreen)
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            TutorFilterScreen(
                filterOption = uiState.filterOption,
                onCloseClick = {
                    viewModel.setEvent(StudentHomeUiEvent.HideFilterScreen)
                },
                onFilterApplied = {
                    viewModel.setEvent(StudentHomeUiEvent.ApplyFilterOption(it))
                })
        }

    }
}

@Composable
internal fun StudentHomeScreenContent(
    uiState: StudentHomeUiState,
    uiEvent: (StudentHomeUiEvent) -> Unit,
) {
    val tutorListingLazyPagingItems: LazyPagingItems<com.buddhatutors.model.tutorlisting.TutorListing> =
        uiState.tutorListing.collectAsLazyPagingItems()

    val pullRefreshState = rememberPullToRefreshState()

    val isRefreshing by remember(tutorListingLazyPagingItems.loadState) {
        mutableStateOf(tutorListingLazyPagingItems.loadState.refresh is LoadState.Loading)
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Find a teachers",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                        ActionIconButton(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.FilterAlt,
                            iconTint = Color.Black
                        ) {
                            uiEvent(StudentHomeUiEvent.FilterButtonClick)
                        }

                        Spacer(Modifier.size(4.dp))

                    }
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


        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
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
                        title = "No Tutors Available",
                        message = "It seems like there are no tutors available at the moment with filter you have applied. Please check back later! Reason:${(tutorListingLazyPagingItems.loadState.refresh as LoadState.Error).error.message}",
                        icon = Icons.Outlined.School,
                        onRetry = null
                    )
                }

                else -> {

                    if (tutorListingLazyPagingItems.itemCount == 0) {

                        NoDataScreenContent(
                            title = "No Tutors Available",
                            message = "It seems like there are no tutors available at the moment with filter you have applied. Please check back later!",
                            icon = Icons.Outlined.School,
                            onRetry = null
                        )

                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            state = rememberLazyListState()
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
                                tutorListingLazyPagingItems[index]?.let { tutorListing ->
                                    Column {
                                        TutorItemCard(
                                            tutorListing = tutorListing,
                                            onClick = {
                                                uiEvent(
                                                    StudentHomeUiEvent.TutorListingItemClick(
                                                        tutorListing
                                                    )
                                                )
                                            })
                                        HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))
                                    }
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

@Composable
fun TutorItemCard(
    modifier: Modifier = Modifier,
    tutorListing: com.buddhatutors.model.tutorlisting.TutorListing,
    onClick: () -> Unit = {},
) {

    Box(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(34.dp),
                    imageVector = Icons.Outlined.School,
                    contentDescription = "Tutor profile"
                )
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
            ) {

                Text(
                    text = tutorListing.tutorUser.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Speaks ${tutorListing.languages.joinToString { it }}",
                    style = MaterialTheme.typography.bodySmall,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Expert in ${
                        tutorListing.expertiseIn.map { it.label }.joinToString(" | ") { it }
                    }",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun TutorItemCardPlaceholder() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .background(
                    brush = shimmerBrush(),
                    shape = CircleShape
                )
        )


        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
        ) {

            Text(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(
                        brush = shimmerBrush(),
                        shape = CircleShape
                    ),
                text = ""
            )

            Spacer(Modifier.height(4.dp))

            Text(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(
                        brush = shimmerBrush(),
                        shape = CircleShape
                    ),
                text = ""
            )


        }

    }

}
