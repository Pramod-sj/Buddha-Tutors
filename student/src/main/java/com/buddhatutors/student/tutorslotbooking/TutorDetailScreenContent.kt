@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.buddhatutors.student.tutorslotbooking

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.DynamicSelectTextField
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.data.datasourceimpl.ActivityContextWrapper
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.tutorlisting.Verification
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import com.buddhatutors.student.tutorslotbooking.slotbooking.TimeBlock

@Preview
@Composable
fun PreviewTutorDetailPage() {
    BuddhaTutorTheme {
        TutorDetailScreenContent(
            TutorDetailUiState(
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
                    ), bookedSlots = listOf(

                    ),
                    expertiseIn = listOf(
                        Topic("", "Story telling"),
                        Topic("", "Story telling"),
                        Topic("", "Story telling"),
                        Topic("", "Story telling")
                    ),
                    availabilityDay = listOf(),
                    timeAvailability = null
                ),
                timeSlots = listOf()
            )
        ) {}
    }
}


@Composable
fun TutorDetailScreen() {

    val context = LocalContext.current

    val viewModel = hiltViewModel<TutorDetailViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val getResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == 1) {
                viewModel.setEvent(
                    TutorDetailUiEvent.BookSlotButtonClick(
                        ActivityContextWrapper(
                            context
                        )
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

    TutorDetailScreenContent(uiState = uiState, uiEvent = viewModel::setEvent)
}

@Composable
fun TutorDetailScreenContent(
    uiState: TutorDetailUiState,
    uiEvent: (TutorDetailUiEvent) -> Unit
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = uiState.tutorListing?.tutorUser?.name.orEmpty())
                },
                navigationIcon = {
                    com.buddhatutors.common.ActionIconButton(
                        imageVector = Icons.Filled.ArrowBack,
                        iconTint = Color.Black
                    ) {
                        //      navigator.popBackStack()
                    }
                })
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
                        uiEvent(
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
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {

            Text("Expertise in", style = MaterialTheme.typography.titleMedium)

            uiState.tutorListing?.expertiseIn?.forEach {
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(4.dp))
                    Text(text = it.label, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Choose a topic and slot",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            DynamicSelectTextField(
                modifier = Modifier.fillMaxWidth(),
                selectedValue = uiState.selectedTopic?.label.orEmpty(),
                options = uiState.tutorListing?.expertiseIn?.map { it.label }.orEmpty(),
                label = "Choose a topic",
                onValueChangedEvent = { value ->
                    uiState.tutorListing?.expertiseIn
                        ?.find { it.label == value }
                        ?.let {
                            uiEvent(TutorDetailUiEvent.SelectTopic(it))
                        }
                },
            )


            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.timeSlots.forEach { timeSlot ->
                    TimeBlock(
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

    AnimatedVisibility(
        visible = uiState.showFullScreenLoader,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceDim.copy(0.6f))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(34.dp)
            )
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