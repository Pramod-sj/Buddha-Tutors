@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.buddhatutors.user.presentation.student.tutorslotbooking

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.auth.data.ActivityContextWrapper
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.DynamicSelectTextField
import com.buddhatutors.common.FullScreenLoader
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.domain.model.tutorlisting.Verification
import com.buddhatutors.common.domain.model.user.User
import com.buddhatutors.common.domain.model.user.UserType
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.user.presentation.student.tutorslotbooking.slotbooking.TimeBlock

@Preview
@Composable
fun PreviewTutorDetailPage() {
    BuddhaTutorTheme {
        TutorDetailScreenContent(
            modifier = Modifier.background(Color.White),
            TutorDetailUiState(
                timeSlots = listOf(
                    SlotTimeUiModel(
                        dateString = "12-Mon",
                        startTime = "07:00 PM",
                        endTime = "08:00 PM",
                        isSlotBooked = false
                    ),
                    SlotTimeUiModel(
                        dateString = "12-Mon",
                        startTime = "07:00 PM",
                        endTime = "08:00 PM",
                        isSlotBooked = false
                    )
                ),
                dateSlots = listOf(
                    SlotDateUiModel("Mon", "12-Mon", ""),
                    SlotDateUiModel("Mon", "12-Mon", ""),
                    SlotDateUiModel("Mon", "12-Mon", "")
                ),
                tutorListing = TutorListing(
                    tutorUser = User(
                        id = "Pramod",
                        name = "Pramod",
                        email = "",
                        userType = UserType.TUTOR,
                    ), verification = Verification(
                        isApproved = false,
                        verifiedByUserId = "",
                        verifiedByUserName = "",
                        verifiedDateTime = ""
                    ),
                    expertiseIn = listOf(
                        Topic("", "Story telling"),
                        Topic("", "Story telling"),
                        Topic("", "Story telling"),
                        Topic("", "Story telling")
                    ),
                    availableDays = listOf(),
                    availableTimeSlots = listOf()
                )
            )
        ) {}
    }
}


@Composable
fun TutorDetailScreen() {

    val context = LocalContext.current

    val navigator = Navigator

    val viewModel = hiltViewModel<TutorDetailViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val topAppBarBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val getResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == 1) {
                viewModel.setEvent(
                    TutorDetailUiEvent.BookSlotButtonClick(
                        ActivityContextWrapper(context)
                    )
                )
            }
        }

    LaunchedEffect(Unit) {

        viewModel.effect.collect {

            when (it) {
                is TutorDetailUiEffect.ShowCalendarApiScopeResolutionDialog -> {
                    getResult.launch(
                        IntentSenderRequest.Builder(it.pendingIntent).build()
                    )
                }
            }

        }

    }
    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarBehaviour.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = uiState.tutorListing?.tutorUser?.name.orEmpty(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    ActionIconButton(
                        imageVector = Icons.Filled.ArrowBack,
                        iconTint = Color.Black
                    ) {
                        navigator.popBackStack()
                    }
                }, scrollBehavior = topAppBarBehaviour
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.setEvent(
                            TutorDetailUiEvent.BookSlotButtonClick(
                                ActivityContextWrapper(context)
                            )
                        )
                    }, enabled = uiState.isBookSlotButtonEnabled
                ) {
                    Text(text = "Book slot")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        TutorDetailScreenContent(
            modifier = Modifier.padding(it),
            uiState = uiState,
            uiEvent = viewModel::setEvent
        )
    }

    FullScreenLoader(
        isVisible = uiState.showFullScreenLoader,
    )
}

@Composable
fun TutorDetailScreenContent(
    modifier: Modifier = Modifier,
    uiState: TutorDetailUiState,
    uiEvent: (TutorDetailUiEvent) -> Unit
) {

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    "Speaks in ${uiState.tutorListing?.languages?.joinToString { it }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Expertise in teaching",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Column {
                    uiState.tutorListing?.expertiseIn?.forEach {
                        Row {
                            Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.width(4.dp))
                            Text(text = it.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Choose a topic and slot",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                DynamicSelectTextField(
                    modifier = Modifier.fillMaxWidth(),
                    selectedValue = uiState.selectedTopic?.label.orEmpty(),
                    options = uiState.tutorListing?.expertiseIn?.map { it.label }
                        .orEmpty(),
                    label = "Choose a topic",
                    onValueChangedEvent = { value ->
                        uiState.tutorListing?.expertiseIn
                            ?.find { it.label == value }
                            ?.let {
                                uiEvent(TutorDetailUiEvent.SelectTopic(it))
                            }
                    },
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.dateSlots.forEach { dateSlot ->
                        DateBlock(
                            dateUiModel = dateSlot,
                            isSelected = dateSlot == uiState.selectedDateSlot,
                            onClick = {
                                uiEvent(TutorDetailUiEvent.SelectDate(it))
                            }
                        )
                    }
                }

                if (uiState.timeSlots.isEmpty()) {

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                            .alpha(0.3f),
                        text = "No slots available for today"
                    )

                } else {
                    val itemSize: Dp =
                        ((LocalConfiguration.current.screenWidthDp.dp - 40.dp) / 2f)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.timeSlots.forEach { timeSlot ->
                            TimeBlock(
                                modifier = Modifier.width(itemSize),
                                slotTimeUiModel = timeSlot,
                                isSelected = timeSlot == uiState.selectedTimeSlot,
                                onClick = {
                                    uiEvent(TutorDetailUiEvent.SelectTimeSlot(it))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
internal fun DateBlock() {
    // TimeBlock(timeSlot = "6:00 AM - 7:00 AM", formatDate = "12-Jun", isSelected = false) { }
}


@Composable
internal fun DateBlock(
    dateUiModel: SlotDateUiModel,
    isSelected: Boolean,
    onClick: (dateUiModel: SlotDateUiModel) -> Unit
) {

    Card(
        modifier = Modifier.size(70.dp),
        onClick = { onClick(dateUiModel) },
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(0.2f)
        ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateUiModel.day,
                style = MaterialTheme.typography.bodySmall.copy(color = if (isSelected) Color.White else Color.Black)
            )
            Text(
                text = dateUiModel.formattedDateString,
                style = MaterialTheme.typography.titleSmall.copy(color = if (isSelected) Color.White else Color.Black)
            )
        }

    }
}