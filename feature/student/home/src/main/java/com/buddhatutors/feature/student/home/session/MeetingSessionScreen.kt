@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.feature.student.home.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.NoDataScreenContent
import com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot
import com.buddhatutors.common.shimmerBrush
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@Preview
@Composable
internal fun PreviewHomePage() {
    BookedSlotItemCardPlaceHolder(Modifier)
}


@Composable
fun MeetingSessionScreen() {

    val viewModel = hiltViewModel<SessionHomeViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    SessionScreenContent(uiState, viewModel::setEvent)
}

@Composable
internal fun SessionScreenContent(
    uiState: SessionHomeUiState,
    uiEvent: (SessionHomeUiEvent) -> Unit
) {
    val context = LocalContext.current

    val tabItem = listOf("Past", "Upcoming")

    val pagerState = rememberPagerState(1) { 2 }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Your sessions",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                actions = {}
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Rounded Capsule TabRow
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 48.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.surface),
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .width(tabPositions[pagerState.currentPage].width)
                            .padding(5.dp)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                divider = {}
            ) {
                tabItem.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .height(40.dp)
                            .zIndex(2f)
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = title,
                            textAlign = TextAlign.Center,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 12.dp)
                                .zIndex(5f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                val bookedSlots = remember(uiState.pastBookedSlots, uiState.upcomingBookedSlots) {
                    if (page == 0) uiState.pastBookedSlots
                    else uiState.upcomingBookedSlots
                }

                val noDataTitle = remember(page) {
                    if (page == 0) "No Past sessions"
                    else "No Upcoming Sessions"
                }

                val noDataMessage = remember(page) {
                    if (page == 0) "It seems like there are no past sessions.\nPlease check back later!"
                    else "It seems like there are no sessions scheduled at the moment.\nPlease check back later!"
                }

                val showLoader = remember(
                    page,
                    uiState.showLoaderForPastScreen,
                    uiState.showLoaderForUpcomingScreen
                ) {
                    if (page == 0) uiState.showLoaderForPastScreen
                    else uiState.showLoaderForUpcomingScreen
                }

                AnimatedVisibility(
                    visible = showLoader,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                        BookedSlotItemCardPlaceHolder()
                    }
                }

                AnimatedVisibility(
                    visible = !showLoader,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (bookedSlots.isEmpty()) {
                        NoDataScreenContent(
                            title = noDataTitle,
                            message = noDataMessage,
                            icon = Icons.Outlined.Event
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {

                            items(bookedSlots) { bookedSlot ->
                                BookedSlotItemCard(
                                    bookedSlot = bookedSlot,
                                    onMeetingClick = {
                                        uiEvent(
                                            SessionHomeUiEvent.OpenMeetClick(
                                                WeakReference(context),
                                                bookedSlot
                                            )
                                        )
                                    })
                                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun BookedSlotItemCard(
    bookedSlot: com.buddhatutors.model.tutorlisting.slotbooking.BookedSlot,
    onMeetingClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable { onMeetingClick() },
    ) {

        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = Icons.Outlined.Videocam,
                    contentDescription = "Start meeting"
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 8.dp)
            ) {

                Text(
                    text = "Session on ${bookedSlot.topic?.label} with ${bookedSlot.tutorInfo?.name ?: "Unknown tutor"}",
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${bookedSlot.fancyDate}, ${bookedSlot.startTime} - ${bookedSlot.endTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun BookedSlotItemCardPlaceHolder(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
    ) {

        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(
                        brush = shimmerBrush(),
                        shape = MaterialTheme.shapes.medium,
                    )
            )


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .background(
                            brush = shimmerBrush(),
                        ),
                )

                Spacer(Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .background(
                            brush = shimmerBrush(),
                        ),
                )
            }
        }
    }
}