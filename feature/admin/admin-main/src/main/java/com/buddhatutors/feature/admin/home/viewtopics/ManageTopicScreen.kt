@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.feature.admin.home.viewtopics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.NoDataScreenContent
import com.buddhatutors.common.collectAsEffect
import com.buddhatutors.common.shimmerBrush
import com.buddhatutors.core.constant.ScreenResultConstant.EXTRA_TUTOR_CHANGED_RESULT
import com.buddhatutors.model.Topic
import kotlinx.coroutines.flow.flowOf

@Preview
@Composable
internal fun PreviewManageTopicPage() {
    ManageTopicContent(
        uiState = ManageTopicUiState(topics = listOf()),
        uiEvent = {},
        topicsLazyPagingItems = flowOf(PagingData.empty<Topic>()).collectAsLazyPagingItems()
    )
}

@Composable
fun ManageTopicScreen(openAddTopicPage: () -> Unit) {

    val navigator = Navigator

    val viewModel = hiltViewModel<ManageTopicViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val topicsPagingData = viewModel.topicsPagingData.collectAsLazyPagingItems()

    navigator.currentBackStackEntry?.savedStateHandle
        ?.getStateFlow(EXTRA_TUTOR_CHANGED_RESULT, false)
        ?.collectAsEffect {
            if (it) {
                topicsPagingData.refresh()
            }
            //resetting it to default
            navigator.currentBackStackEntry?.savedStateHandle
                ?.set(EXTRA_TUTOR_CHANGED_RESULT, false)
        }

    ManageTopicContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        topicsLazyPagingItems = topicsPagingData
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {
                ManageTopicUiEffect.NavigateToAddTopic -> {
                    openAddTopicPage()
                }
            }
        }
    }

}


@Composable
internal fun ManageTopicContent(
    uiState: ManageTopicUiState,
    uiEvent: (ManageTopicUiEvent) -> Unit,
    topicsLazyPagingItems: LazyPagingItems<com.buddhatutors.model.Topic>
) {

    val pullRefreshState = rememberPullToRefreshState()

    val isRefreshing by remember(topicsLazyPagingItems.loadState) {
        mutableStateOf(topicsLazyPagingItems.loadState.refresh is LoadState.Loading)
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = "Topics")
                },
                actions = {}
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { uiEvent(ManageTopicUiEvent.AddTopicButtonClick) },
                text = {
                    Text(text = "Add topic")
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
                topicsLazyPagingItems.refresh()
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
                topicsLazyPagingItems.loadState.refresh is LoadState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(10) {
                            TopicItemCardPlaceholder()
                        }
                    }
                }

                topicsLazyPagingItems.loadState.refresh is LoadState.Error -> {
                    NoDataScreenContent(
                        title = "No topics",
                        message = "It seems like there are no topics available at the moment. Please add some topics!",
                        icon = Icons.Outlined.School,
                        onRetry = null
                    )
                }

                else -> {

                    if (topicsLazyPagingItems.itemCount == 0) {

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
                                if (topicsLazyPagingItems.loadState.prepend is LoadState.Loading) {
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

                            items(topicsLazyPagingItems.itemCount) { index ->
                                topicsLazyPagingItems[index]?.let { topic ->
                                    TopicItemCard(
                                        topic = topic,
                                        onClick = {

                                        }
                                    )
                                }
                            }

                            item {
                                if (topicsLazyPagingItems.loadState.append is LoadState.Loading) {
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
fun TopicItemCard(
    topic: com.buddhatutors.model.Topic,
    onClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {

        Text(
            text = topic.label,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )

    }

}


@Composable
fun TopicItemCardPlaceholder() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(shimmerBrush())
        )

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(shimmerBrush())
        )

    }

}