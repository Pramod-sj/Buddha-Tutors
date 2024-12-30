@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.appadmin.presentation.admin.viewmastertutor

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.buddhatutors.appadmin.Constant.EXTRA_TUTOR_CHANGED
import com.buddhatutors.appadmin.presentation.common.TutorItemCardPlaceholder
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.NoDataScreenContent
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.navigation.AdminGraph
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Preview
@Composable
internal fun PreviewViewMasterTutorListScreen() {
    ViewMasterTutorListContent(
        uiState = ViewMasterTutorUiState(),
        uiEvent = {},
        masterTutorUsersLazyPagingItems = flowOf(PagingData.empty<User>()).collectAsLazyPagingItems()
    )
}

@Composable
fun ViewMasterTutorListScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<ViewMasterTutorUsersHomeViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val masterTutorUsersLazyPagingItems = uiState.masterTutorPagingData.collectAsLazyPagingItems()

    navigator.currentBackStackEntry?.savedStateHandle
        ?.getStateFlow(EXTRA_TUTOR_CHANGED, false)
        ?.collectAsEffect {
            if (it) {
                masterTutorUsersLazyPagingItems.refresh()
            }
            //resetting it to default
            navigator.currentBackStackEntry?.savedStateHandle
                ?.set(EXTRA_TUTOR_CHANGED, false)
        }

    ViewMasterTutorListContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        masterTutorUsersLazyPagingItems = masterTutorUsersLazyPagingItems
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {
                ViewMasterTutorUiEffect.NavigateToAddViewMasterTutorUserScreen -> {
                    navigator.navigate(AdminGraph.AddMasterTutorUser)
                }
            }
        }
    }

}


@Composable
internal fun ViewMasterTutorListContent(
    uiState: ViewMasterTutorUiState,
    uiEvent: (ViewMasterTutorUiEvent) -> Unit,
    masterTutorUsersLazyPagingItems: LazyPagingItems<User>,
) {

    val pullRefreshState = rememberPullToRefreshState()

    val isRefreshing by remember(masterTutorUsersLazyPagingItems.loadState) {
        mutableStateOf(masterTutorUsersLazyPagingItems.loadState.refresh is LoadState.Loading)
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = "Master tutors")
                },
                actions = {}
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { uiEvent(ViewMasterTutorUiEvent.AddMasterTutorUserFABClick) },
                text = {
                    Text(text = "Add master tutor")
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
                .padding(top = it.calculateTopPadding()),
            isRefreshing = isRefreshing,
            onRefresh = {
                masterTutorUsersLazyPagingItems.refresh()
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
                masterTutorUsersLazyPagingItems.loadState.refresh is LoadState.Loading -> {
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

                masterTutorUsersLazyPagingItems.loadState.refresh is LoadState.Error -> {
                    NoDataScreenContent(
                        title = "No  Master tutors",
                        message = "It seems like there are no master tutors available at the moment. Please add some master tutors!",
                        icon = Icons.Outlined.School,
                        onRetry = null
                    )
                }

                else -> {

                    if (masterTutorUsersLazyPagingItems.itemCount == 0) {

                        NoDataScreenContent(
                            title = "No  Master tutors",
                            message = "It seems like there are no master tutors available at the moment. Please add some master tutors!",
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
                                if (masterTutorUsersLazyPagingItems.loadState.prepend is LoadState.Loading) {
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

                            items(masterTutorUsersLazyPagingItems.itemCount) { index ->
                                masterTutorUsersLazyPagingItems[index]?.let { masterTutor ->
                                    MasterTutorUserItemCard(user = masterTutor)
                                }
                            }

                            item {
                                if (masterTutorUsersLazyPagingItems.loadState.append is LoadState.Loading) {
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
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}