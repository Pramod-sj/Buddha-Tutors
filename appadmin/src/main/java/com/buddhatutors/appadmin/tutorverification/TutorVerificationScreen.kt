@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.buddhatutors.appadmin.tutorverification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType

@Preview
@Composable
fun PreviewTutorDetailPage() {
    BuddhaTutorTheme {
        TutorVerificationScreenContent(
            uiState = TutorVerificationUiState(
                tutorListing = TutorListing(
                    tutorUser = User(
                        id = "",
                        name = "",
                        email = "",
                        userType = UserType.STUDENT
                    ),
                    expertiseIn = listOf(),
                    availableDays = listOf("Monday", "Tuesday", "Wednesday"),
                    availableTimeSlots = listOf(
                        TimeSlot("08:00 AM", "08:30 AM"),
                        TimeSlot("08:30 AM", "09:00 AM"),
                        TimeSlot("09:00 AM", "09:30 AM"),
                        TimeSlot("09:30 AM", "10:00 AM")
                    ),
                    verification = null,
                )
            ),
            uiEvent = {})
    }
}

@Composable
fun TutorVerificationScreen() {

    val viewModel = hiltViewModel<TutorVerificationViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    TutorVerificationScreenContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent
    )

}

@Composable
internal fun TutorVerificationScreenContent(
    uiState: TutorVerificationUiState, uiEvent: (TutorDetailUiEvent) -> Unit
) {

    val navigator = Navigator

    val topAppBarBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarBehaviour.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = topAppBarBehaviour,
                title = {
                    Text(text = uiState.tutorListing?.tutorUser?.name ?: "")
                }, navigationIcon = {
                    com.buddhatutors.common.ActionIconButton(
                        imageVector = Icons.Filled.ArrowBack, iconTint = Color.Black
                    ) {
                             navigator.popBackStack()
                    }
                })
        }, bottomBar = {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f), Color.White
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                if (uiState.isRejectButtonVisible) {

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f),
                        onClick = { uiEvent(TutorDetailUiEvent.RejectTutorButtonClick) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            disabledContentColor = MaterialTheme.colorScheme.errorContainer.copy(
                                0.4f
                            ),
                            disabledContainerColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                                0.4f
                            ),
                        )
                    ) {
                        Text(text = "Reject")
                    }

                }

                if (uiState.isApproveButtonVisible) {

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                        onClick = { uiEvent(TutorDetailUiEvent.ApproveTutorButtonClick) }) {
                        Text(text = "Approve")
                    }

                }

            }
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text("I'm a pass out willing to teach student who are super interested in getting the knowlege of ")

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Expertise in",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            uiState.tutorListing?.expertiseIn?.forEach {
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(4.dp))
                    Text(text = it.label, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))


            Text(
                text = "Tutor availability",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(8.dp))

            Text("Days :-", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.tutorListing?.availableDays?.forEach {

                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(text = "Hours :-", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            val itemSize: Dp = ((LocalConfiguration.current.screenWidthDp.dp - 40.dp) / 2f)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.tutorListing?.availableTimeSlots?.forEach { item ->
                    Text(
                        text = String.format("%s - %s", item.start, item.end),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .width(itemSize)
                            .wrapContentHeight()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                MaterialTheme.shapes.small
                            )
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}